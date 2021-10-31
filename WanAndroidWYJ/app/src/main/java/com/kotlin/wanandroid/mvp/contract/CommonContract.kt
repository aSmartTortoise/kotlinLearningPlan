package com.kotlin.wanandroid.mvp.contract

import com.kotlin.wanandroid.base.IModel
import com.kotlin.wanandroid.base.IPresenter
import com.kotlin.wanandroid.base.IView
import com.kotlin.wanandroid.mvp.model.bean.HttpResult
import io.reactivex.Observable

interface CommonContract {
    interface View : IView {
        fun showCollectSuccess(seccess: Boolean)
        fun showCancelCollectSuccess(success: Boolean)
    }

    interface Presenter<in V: View> : IPresenter<V> {
        fun addCollectArticle(id: Int)
        fun cancleCollectArticle(id: Int)
    }

    interface Model : IModel {
        fun addCollectArticle(id: Int): Observable<HttpResult<Any>>
        fun cancleCollectArticle(id: Int): Observable<HttpResult<Any>>
    }
}