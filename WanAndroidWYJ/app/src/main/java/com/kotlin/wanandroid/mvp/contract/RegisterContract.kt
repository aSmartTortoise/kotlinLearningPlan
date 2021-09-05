package com.kotlin.wanandroid.mvp.contract

import com.kotlin.wanandroid.base.IModel
import com.kotlin.wanandroid.base.IPresenter
import com.kotlin.wanandroid.base.IView
import com.kotlin.wanandroid.mvp.model.bean.HttpResult
import com.kotlin.wanandroid.mvp.model.bean.LoginData
import io.reactivex.Observable

interface RegisterContract {

    interface View: IView {
        fun  registSuccess(data: LoginData)

        fun registFail()
    }

    interface Presenter: IPresenter<View> {
        fun regist(userName: String, pwd: String, rePwd: String)
    }

    interface Model: IModel {
        fun regist(userName: String, pwd: String, rePwd: String): Observable<HttpResult<LoginData>>
    }
}