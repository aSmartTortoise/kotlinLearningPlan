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
        model?.logout()?.ss(model, view) {
            view?.showLogoutSuccess(success = true)
        }
    }

    override fun getUserInfo() {
        model?.getUserInfo()?.sss(view, false, {
            view?.showUserInfo(it.data)
        }, {})
    }

}