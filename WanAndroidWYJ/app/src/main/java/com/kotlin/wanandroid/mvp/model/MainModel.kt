package com.kotlin.wanandroid.mvp.model

import com.kotlin.wanandroid.base.BaseModel
import com.kotlin.wanandroid.http.RetrofitHelper
import com.kotlin.wanandroid.mvp.contract.MainContract
import com.kotlin.wanandroid.mvp.model.bean.HttpResult
import com.kotlin.wanandroid.mvp.model.bean.UserInfoBody
import io.reactivex.Observable

class MainModel : BaseModel(), MainContract.Model {
    override fun logout(): Observable<HttpResult<Any>> {
        return RetrofitHelper.service.logout()
    }

    override fun getUserInfo(): Observable<HttpResult<UserInfoBody>> {
        return RetrofitHelper.service.getUserInfo()
    }
}