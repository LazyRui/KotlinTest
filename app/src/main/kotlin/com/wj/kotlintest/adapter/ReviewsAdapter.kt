package com.wj.kotlintest.adapter

import com.wj.kotlintest.R
import com.wj.kotlintest.activity.MoviesDetailsActivity
import com.wj.kotlintest.base.BaseRvAdapter
import com.wj.kotlintest.base.BaseRvViewHolder
import com.wj.kotlintest.databinding.ItemReviewsBinding
import com.wj.kotlintest.entity.ReviewsEntity
import javax.inject.Inject

/**
 * 评论列表适配器类
 */
class ReviewsAdapter @Inject constructor()
    : BaseRvAdapter<ReviewsAdapter.ViewHolder,
        ItemReviewsBinding,
        MoviesDetailsActivity.MoviesDetailsHandler,
        ReviewsEntity.Result>() {

    override fun layoutResID() = R.layout.item_reviews

    class ViewHolder(binding: ItemReviewsBinding) : BaseRvViewHolder<ItemReviewsBinding, ReviewsEntity.Result>(binding)
}