package com.kotlin.wanandroid.mvp.presenter

import com.kotlin.wanandroid.base.BasePresenter
import com.kotlin.wanandroid.ext.ss
import com.kotlin.wanandroid.mvp.contract.LoginContract
import com.kotlin.wanandroid.mvp.model.LoginModel

class LoginPresenter : BasePresenter<LoginContract.Model, LoginContract.View>(), LoginContract.Presenter {
    override fun createModel(): LoginContract.Model? = LoginModel()

    override fun login(userName: String, psw: String) {
        model?.login(userName, psw)?.ss(model, view) {
            view?.loginSuccess(it.data)
        }
    }
}