package com.kotlin.wanandroid.mvp.contract

import com.kotlin.wanandroid.base.IModel
import com.kotlin.wanandroid.base.IPresenter
import com.kotlin.wanandroid.base.IView
import com.kotlin.wanandroid.mvp.model.bean.BaseListResponseBody
import com.kotlin.wanandroid.mvp.model.bean.HttpResult
import com.kotlin.wanandroid.mvp.model.bean.UserScoreBean
import io.reactivex.Observable

interface ScoreContract {
    interface View : IView {
        fun showUserScoreList(body: BaseListResponseBody<UserScoreBean>)
    }

    interface Presenter : IPresenter<View> {
        fun getUserScoreList(page: Int)
    }

    interface Model : IModel {
        fun getUserScoreList(page: Int): Observable<HttpResult<BaseListResponseBody<UserScoreBean>>>
    }
}