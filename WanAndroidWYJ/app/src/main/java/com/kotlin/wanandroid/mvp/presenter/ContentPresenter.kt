package com.kotlin.wanandroid.mvp.presenter

import com.kotlin.wanandroid.mvp.contract.ContentContract
import com.kotlin.wanandroid.mvp.model.ContentModel

class ContentPresenter : CommonPreseter<ContentContract.Model, ContentContract.View>(), ContentContract.Presenter {
    override fun createModel(): ContentContract.Model = ContentModel()
}