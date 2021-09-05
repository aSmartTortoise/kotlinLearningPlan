package com.kotlin.wanandroid.mvp.model

import com.kotlin.wanandroid.base.BaseModel
import com.kotlin.wanandroid.http.RetrofitHelper
import com.kotlin.wanandroid.mvp.contract.RegisterContract
import com.kotlin.wanandroid.mvp.model.bean.HttpResult
import com.kotlin.wanandroid.mvp.model.bean.LoginData
import io.reactivex.Observable

class RegistModel : BaseModel(), RegisterContract.Model {
    override fun regist(
        userName: String,
        pwd: String,
        rePwd: String
    ): Observable<HttpResult<LoginData>> = RetrofitHelper.service.registerWanAndroid(
        userName, pwd, rePwd
    )

}