package com.kotlin.wanandroid.mvp.presenter

import com.kotlin.wanandroid.base.BasePresenter
import com.kotlin.wanandroid.ext.ss
import com.kotlin.wanandroid.mvp.contract.RegisterContract
import com.kotlin.wanandroid.mvp.model.RegistModel

class RegistPresenter : BasePresenter<RegisterContract.Model, RegisterContract.View>(), RegisterContract.Presenter {
    override fun createModel(): RegisterContract.Model? = RegistModel()

    override fun regist(userName: String, pwd: String, rePwd: String) {
        mModel?.regist(userName, pwd, rePwd)?.ss(mModel, mView) {
            mView?.run {
                if (it.errorCode != 0) {
                    registFail()
                } else {
                    registSuccess(it.data)
                }
            }
        }
    }
}