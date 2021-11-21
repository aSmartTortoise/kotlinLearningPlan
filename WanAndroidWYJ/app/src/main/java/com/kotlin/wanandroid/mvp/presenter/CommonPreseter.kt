package com.kotlin.wanandroid.mvp.presenter

import com.kotlin.wanandroid.base.BasePresenter
import com.kotlin.wanandroid.ext.ss
import com.kotlin.wanandroid.mvp.contract.CommonContract

open class CommonPreseter<M : CommonContract.Model, V : CommonContract.View> :
    BasePresenter<M, V>(), CommonContract.Presenter<V> {

    override fun addCollectArticle(id: Int) {
        mModel?.addCollectArticle(id)?.ss(mModel, mView) {
            mView?.showCollectSuccess(true)
        }
    }

    override fun cancleCollectArticle(id: Int) {
        mModel?.addCollectArticle(id)?.ss(mModel, mView) {
            mView?.showCancelCollectSuccess(true)
        }
    }

}