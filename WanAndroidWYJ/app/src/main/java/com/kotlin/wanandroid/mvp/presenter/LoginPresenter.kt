package com.kotlin.wanandroid.mvp.presenter

import androidx.lifecycle.LifecycleObserver
import com.kotlin.wanandroid.base.BasePresenter
import com.kotlin.wanandroid.ext.ss
import com.kotlin.wanandroid.mvp.contract.LoginContract
import com.kotlin.wanandroid.mvp.model.LoginModel

class LoginPresenter : BasePresenter<LoginContract.Model, LoginContract.View>(), LoginContract.Presenter {
    override fun createModel(): LoginContract.Model? = LoginModel()

    override fun login(userName: String, psw: String) {
        mModel?.login(userName, psw)?.ss(mModel, mView) {
            mView?.loginSuccess(it.data)
        }
    }
}