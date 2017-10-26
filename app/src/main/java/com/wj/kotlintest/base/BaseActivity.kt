package com.wj.kotlintest.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import com.wj.kotlintest.R
import com.wj.kotlintest.databinding.LayoutBaseBinding
import com.wj.kotlintest.databinding.RootHandler
import com.wj.kotlintest.utils.AppManager
import com.wj.kotlintest.utils.statusbar.StatusBarUtil
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

/**
 * Activity 基类
 */
abstract class BaseActivity<P : BaseMVPPresenter<*, *>, DB : ViewDataBinding>
    : DaggerAppCompatActivity(),
        BaseMVPView,
        RootHandler.OnTitleClickListener {

    /** 当前界面 Context 对象*/
    protected lateinit var mContext: AppCompatActivity

    /** 当前界面 Presenter 对象 */
    @Inject
    protected lateinit var presenter: P

    /** 根布局 DataBinding 对象 */
    protected lateinit var rootBinding: LayoutBaseBinding
    /** 当前界面布局 DataBinding 对象 */
    protected lateinit var mBinding: DB

    /**
     * 重写 onCreate() 方法，添加了 Dagger2 注入、Activity 管理以及根布局等初始化操作
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // Dagger2 注入
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        // 保存当前 Context 对象
        mContext = this

        // 添加到 AppManager 应用管理
        AppManager.addActivity(this)

        // 加载根布局，初始化 DataBinding
        rootBinding = DataBindingUtil.inflate(
                LayoutInflater.from(mContext),
                R.layout.layout_base, null, false
        )
        // 绑定事件处理
        rootBinding.handler = RootHandler(this)
    }

    /**
     * 重写 onDestroy() 方法，移除 Activity 管理以及 MVP 生命周期管理
     */
    override fun onDestroy() {

        // 从应用管理移除当前 Activity 对象
        AppManager.removeActivity(this)

        // 界面销毁时，消费所有事件，清空引用
        presenter.dispose()
        presenter.detach()

        super.onDestroy()
    }

    /**
     * 重写 setContentView(layoutResID) 方法，使其支持 DataBinding 以及标题栏、状态栏初始化操作
     */
    override fun setContentView(layoutResID: Int) {

        // 初始化标题栏
        initTitleBar()

        // 加载布局，初始化 DataBinding
        mBinding = DataBindingUtil.inflate(
                LayoutInflater.from(mContext),
                layoutResID, null, false
        )

        // 将当前布局添加到根布局
        rootBinding.flContent.removeAllViews()
        rootBinding.flContent.addView(mBinding.root)

        // 设置布局
        super.setContentView(rootBinding.root)

        // 初始化状态栏
        initStatusBar()
    }

    /**
     * 初始化标题栏，抽象方法，子类实现标题栏自定义
     */
    protected abstract fun initTitleBar()

    /**
     * 初始化状态栏，默认主题色、不透明，修改需重写
     */
    protected fun initStatusBar() {
        setStatusBar()
    }

    /**
     * 设置状态栏，默认主题色、不透明
     *
     * @param colorResId    状态栏颜色，默认主题色
     * @param alpha         状态栏透明度，默认不透明，取值范围 0~255
     */
    protected fun setStatusBar(@ColorRes colorResId: Int = R.color.colorTheme, alpha: Int = 0) {
        if (alpha !in 0..255) {
            RuntimeException("The value of the alpha must between 0 and 255")
        } else {
            StatusBarUtil.setResColor(this, colorResId, alpha)
        }
    }

    /**
     * 显示标题栏
     */
    protected fun showTitle() {
        rootBinding.handler.showTitle = true
    }

    /**
     * 设置标题文本
     *
     * @param strResID 标题文本资源id
     */
    protected fun setTitleStr(@StringRes strResID: Int) {
        rootBinding.handler.showTvTitle = true
        rootBinding.handler.tvTitle = getString(strResID)
    }

    /**
     * 设置标题文本
     *
     * @param str      标题文本
     */
    protected fun setTitleStr(str: String) {
        rootBinding.handler.showTvTitle = true
        rootBinding.handler.tvTitle = str
    }

    /**
     * 设置标题栏左侧图标，默认返回按钮
     *
     * @param resID     标题栏左侧图标资源id，默认返回按钮
     */
    protected fun setIvLeft(@DrawableRes resID: Int = R.mipmap.arrow_left_white) {
        rootBinding.handler.showIvLeft = true
        rootBinding.handler.ivLeftResID = resID
    }

    /**
     * 设置右侧图标
     *
     * @param resID 图片资源id
     */
    protected fun setIvRight(@DrawableRes resID: Int) {
        rootBinding.handler.showIvRight = true
        rootBinding.handler.ivRightResID = resID
    }

    /**
     * 设置右侧文本
     *
     * @param strResID 文本资源id
     */
    protected fun setTvRight(@StringRes strResID: Int) {
        rootBinding.handler.showTvRight = true
        rootBinding.handler.tvRight = getString(strResID)
    }

    /**
     * 重写BaseMvpView中方法，网络异常时调用
     */
    override fun onNetError() {
        val handler = rootBinding.handler
        if (handler.showNoData) {
            handler.showNoData = false
        }
        if (handler.showLoading) {
            val drawable = rootBinding.ivLoading.drawable
            (drawable as? AnimationDrawable)?.stop()
            handler.showLoading = false
        }
        if (!handler.showNetError) {
            handler.showNetError = true
        }
        onListComplete()
    }

    /**
     * 重写BaseMvpView中方法，无数据时调用
     */
    override fun onNoData() {
        val handler = rootBinding.handler
        if (handler.showNetError) {
            handler.showNetError = false
        }
        if (handler.showLoading) {
            val drawable = rootBinding.ivLoading.drawable
            (drawable as? AnimationDrawable)?.stop()
            handler.showLoading = false
        }
        if (!handler.showNoData) {
            handler.showNoData = true
        }
        onListComplete()
    }

    /**
     * 重写BaseMvpView中方法，加载数据时调用
     */
    override fun onLoading() {
        val handler = rootBinding.handler
        if (handler.showNetError) {
            handler.showNetError = false
        }
        if (handler.showNoData) {
            handler.showNoData = false
        }
        if (!handler.showLoading) {
            val drawable = rootBinding.ivLoading.drawable
            (drawable as? AnimationDrawable)?.start()
            handler.showLoading = true
        }
    }

    /**
     * 重写BaseMvpView中方法，网络请求结束后调用，隐藏其他界面
     */
    override fun onNetFinished() {
        val handler = rootBinding.handler
        if (handler.showNetError) {
            handler.showNetError = false
        }
        if (handler.showNoData) {
            handler.showNoData = false
        }
        if (handler.showLoading) {
            val drawable = rootBinding.ivLoading.drawable
            (drawable as? AnimationDrawable)?.stop()
            handler.showLoading = false
        }
        onListComplete()
    }

    /**
     * 使用SwipeToLoadView时重写，完成刷新步骤
     */
    protected fun onListComplete() {}

    /**
     * 标题栏左侧点击事件，默认结束当前界面
     */
    override fun onLeftClick() {
        finish()
    }

    /**
     * 标题栏右侧点击事件
     */
    override fun onRightClick() {}

    /**
     * 无数据界面点击事件，默认显示加载中
     */
    override fun onNoDataClick() {
        onLoading()
    }

    /**
     * 网络异常界面点击事件，默认显示加载中
     */
    override fun onNetErrorClick() {
        onLoading()
    }

}