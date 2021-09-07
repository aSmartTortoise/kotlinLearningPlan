package com.kotlin.wanandroid.mvp.contract

import com.kotlin.wanandroid.base.IModel
import com.kotlin.wanandroid.base.IPresenter
import com.kotlin.wanandroid.base.IView
import com.kotlin.wanandroid.mvp.model.bean.BaseListResponseBody
import com.kotlin.wanandroid.mvp.model.bean.CoinInfoBean
import com.kotlin.wanandroid.mvp.model.bean.HttpResult
import io.reactivex.Observable

interface RankContract {
    interface View : IView {
        fun showRankList(body: BaseListResponseBody<CoinInfoBean>)
    }

    interface Presenter : IPresenter<View> {
        fun getRankList(page: Int)
    }

    interface Model : IModel {
        fun getRankList(page: Int): Observable<HttpResult<BaseListResponseBody<CoinInfoBean>>>
    }
}