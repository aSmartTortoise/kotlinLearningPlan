package com.kotlin.wanandroid.ui.activity

import com.kotlin.wanandroid.R
import com.kotlin.wanandroid.base.BaseSwipeBackMVPActivity
import com.kotlin.wanandroid.mvp.contract.ContentContract
import com.kotlin.wanandroid.mvp.presenter.ContentPresenter

class ContentActivity : BaseSwipeBackMVPActivity<ContentContract.View, ContentContract.Presenter>(),
    ContentContract.View {
    override fun attachLayoutRes() = R.layout.activity_content

    override fun initData() {
    }

    override fun start() {
    }

    override fun createPrenter(): ContentContract.Presenter = ContentPresenter()

    override fun showCollectSuccess(seccess: Boolean) {
    }

    override fun showCancelCollectSuccess(success: Boolean) {
    }
}