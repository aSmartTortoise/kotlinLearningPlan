package com.kotlin.wanandroid.mvp.presenter

import com.kotlin.wanandroid.base.BasePresenter
import com.kotlin.wanandroid.ext.ss
import com.kotlin.wanandroid.ext.sss
import com.kotlin.wanandroid.mvp.contract.MainContract
import com.kotlin.wanandroid.mvp.model.MainModel


/**
 * @author chenxz
 * @date 2018/8/30
 * @desc
 */
class MainPresenter : BasePresenter<MainContract.Model, MainContract.View>(), MainContract.Presenter {

    override fun createModel(): MainContract.Model? = MainModel()

    override fun logout() {
        mModel?.logout()?.ss(mModel, mView) {
            mView?.showLogoutSuccess(success = true)
        }
    }

    override fun getUserInfo() {
        mModel?.getUserInfo()?.sss(mView, false, {
            mView?.showUserInfo(it.data)
        }, {})
    }

}