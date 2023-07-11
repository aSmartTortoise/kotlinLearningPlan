package com.kotlin.wanandroid.mvp.presenter

import com.kotlin.wanandroid.base.BasePresenter
import com.kotlin.wanandroid.ext.ss
import com.kotlin.wanandroid.mvp.contract.CommonContract

open class CommonPreseter<M : CommonContract.Model, V : CommonContract.View> :
    BasePresenter<M, V>(), CommonContract.Presenter<V> {

    override fun addCollectArticle(id: Int) {
        model?.addCollectArticle(id)?.ss(model, view) {
            view?.showCollectSuccess(true)
        }
    }

    override fun cancleCollectArticle(id: Int) {
        model?.addCollectArticle(id)?.ss(model, view) {
            view?.showCancelCollectSuccess(true)
        }
    }

}