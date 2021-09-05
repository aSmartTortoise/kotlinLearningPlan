package com.kotlin.wanandroid.mvp.contract

import com.kotlin.wanandroid.base.IModel
import com.kotlin.wanandroid.base.IPresenter
import com.kotlin.wanandroid.base.IView
import com.kotlin.wanandroid.mvp.model.bean.HttpResult
import com.kotlin.wanandroid.mvp.model.bean.LoginData
import io.reactivex.Observable

interface LoginContract {

    interface View : IView {

        fun loginSuccess(data: LoginData)

        fun loginFail()
    }

    interface Presenter : IPresenter<View> {
        fun login(userName: String, psw: String)
    }

    interface Model : IModel {
        fun login(userName: String, psw: String): Observable<HttpResult<LoginData>>
    }

}