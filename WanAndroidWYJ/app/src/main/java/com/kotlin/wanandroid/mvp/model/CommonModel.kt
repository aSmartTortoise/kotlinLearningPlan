package com.kotlin.wanandroid.mvp.model

import com.kotlin.wanandroid.base.BaseModel
import com.kotlin.wanandroid.http.RetrofitHelper
import com.kotlin.wanandroid.mvp.contract.CommonContract
import com.kotlin.wanandroid.mvp.model.bean.HttpResult
import io.reactivex.Observable

open class CommonModel : BaseModel(), CommonContract.Model {
    override fun addCollectArticle(id: Int): Observable<HttpResult<Any>> {
        return RetrofitHelper.service.addCollectArticle(id)
    }

    override fun cancleCollectArticle(id: Int): Observable<HttpResult<Any>> {
        return RetrofitHelper.service.cancelCollectArticle(id)
    }
}