package com.kotlin.wanandroid.base

import com.kotlin.wanandroid.ext.showToast

@Suppress("UNCHECKED_CAST")
abstract class BaseMVPActivity<in V : IView, P : IPresenter<V>> : BaseActivity(), IView {
    protected var mPresenter: P? = null

    protected abstract fun createPrenter(): P

    override fun initView() {
        mPresenter = createPrenter()
        mPresenter?.attachView(this as V)
    }

    override fun showLoading() {
    }

    override fun hideLoading() {
    }

    override fun showError(errorMsg: String) {
        showToast(errorMsg)
    }

    override fun showDefaultMsg(msg: String) {
        showToast(msg)
    }

    override fun showMsg(msg: String) {
        showToast(msg)
    }


}