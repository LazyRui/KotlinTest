package com.wj.kotlintest.adapter

import android.view.View
import com.wj.kotlintest.R
import com.wj.kotlintest.activity.MoviesDetailsActivity
import com.wj.kotlintest.base.BaseRvAdapter
import com.wj.kotlintest.base.BaseRvViewHolder
import com.wj.kotlintest.databinding.ItemTrailersBinding
import com.wj.kotlintest.entity.TrailersEntity
import javax.inject.Inject

/**
 * 特别收录列表适配器类
 */
class TrailersAdapter @Inject constructor()
    : BaseRvAdapter<TrailersAdapter.ViewHolder,
        ItemTrailersBinding,
        MoviesDetailsActivity.MoviesDetailsHandler,
        TrailersEntity.Result>() {

    override fun layoutResID() = R.layout.item_trailers

    override fun createViewHolder(view: View) = null

    override fun createViewHolder(binding: ItemTrailersBinding) = ViewHolder(binding)

    class ViewHolder(binding: ItemTrailersBinding) : BaseRvViewHolder<ItemTrailersBinding, TrailersEntity.Result>(binding)
}