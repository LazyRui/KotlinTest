package com.wj.kotlintest.mvp

import android.util.Log
import com.wj.kotlintest.base.BaseMVPPresenter
import com.wj.kotlintest.base.OnNetFinishedListener
import com.wj.kotlintest.entity.MoviesListEntity
import javax.inject.Inject

/**
 * 电影列表界面 Presenter
 *
 * @author 王杰
 */
class MoviesListPresenter @Inject constructor() : BaseMVPPresenter<MoviesListView, MoviesModule>() {

    /**
     * 获取评分最高电影列表
     */
    fun getHighestRatedMovies() {

        val dispose = mModule.getHighestRatedMovies(object : OnNetFinishedListener<MoviesListEntity> {
            override fun onSuccess(entity: MoviesListEntity) {
                mView?.notifyData(entity)
            }

            override fun onFail(fail: Throwable) {
                Log.e("NET_ERROR", "HIGHEST_RATED_MOVIES", fail)
            }
        })

        addDisposable(dispose)
    }
}