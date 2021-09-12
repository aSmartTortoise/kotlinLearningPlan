package com.kotlin.wanandroid.mvp.model

import com.kotlin.wanandroid.base.BaseModel
import com.kotlin.wanandroid.http.RetrofitHelper
import com.kotlin.wanandroid.mvp.contract.RankContract
import com.kotlin.wanandroid.mvp.model.bean.BaseListResponseBody
import com.kotlin.wanandroid.mvp.model.bean.CoinInfoBean
import com.kotlin.wanandroid.mvp.model.bean.HttpResult
import io.reactivex.Observable

class RankModel : BaseModel(), RankContract.Model {
    override fun getRankList(page: Int): Observable<HttpResult<BaseListResponseBody<CoinInfoBean>>> {
        return RetrofitHelper.service.getRankList(page)
    }
}