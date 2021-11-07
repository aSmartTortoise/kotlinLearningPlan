package com.kotlin.wanandroid.base

import android.view.View
import com.kotlin.wanandroid.ext.showToast

@Suppress("UNCHECKED_CAST")
abstract class BaseMVPFragment<in V: IView, P: IPresenter<V>> : BaseFragment(), IView {

    protected var mPresenter: P? = null

    protected abstract fun creatPresenter(): P
    override fun initView(view: View) {
        mPresenter = creatPresenter()
        mPresenter?.attachView(this as V)
    }

    override fun showLoading() {
    }

    override fun hideLoading() {
    }

    override fun showDefaultMsg(msg: String) {
        showToast(msg)
    }

    override fun showMsg(msg: String) {
        showToast(msg)
    }

    override fun showError(errorMsg: String) {
        showToast(errorMsg)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.detachView()
        mPresenter = null
    }
}