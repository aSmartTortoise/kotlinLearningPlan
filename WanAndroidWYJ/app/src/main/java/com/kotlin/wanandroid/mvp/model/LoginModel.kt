package com.kotlin.wanandroid.mvp.model

import com.kotlin.wanandroid.base.BaseModel
import com.kotlin.wanandroid.http.RetrofitHelper
import com.kotlin.wanandroid.mvp.contract.LoginContract
import com.kotlin.wanandroid.mvp.model.bean.HttpResult
import com.kotlin.wanandroid.mvp.model.bean.LoginData
import io.reactivex.Observable

class LoginModel : BaseModel(), LoginContract.Model {
    override fun login(userName: String, psw: String): Observable<HttpResult<LoginData>> =
        RetrofitHelper.service.loginWanAndroid(userName, psw)
}