package com.kotlin.wanandroid.mvp.model

import com.kotlin.wanandroid.base.BaseModel
import com.kotlin.wanandroid.http.RetrofitHelper
import com.kotlin.wanandroid.mvp.contract.ScoreContract
import com.kotlin.wanandroid.mvp.model.bean.BaseListResponseBody
import com.kotlin.wanandroid.mvp.model.bean.HttpResult
import com.kotlin.wanandroid.mvp.model.bean.UserScoreBean
import io.reactivex.Observable

class ScoreModel : BaseModel(), ScoreContract.Model {
    override fun getUserScoreList(page: Int): Observable<HttpResult<BaseListResponseBody<UserScoreBean>>> {
        return RetrofitHelper.service.getUserScoreList(page)
    }
}