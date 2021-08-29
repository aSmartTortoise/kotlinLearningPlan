package com.kotlin.wanandroid.ui.activity

import android.util.Log
import com.kotlin.wanandroid.R
import com.kotlin.wanandroid.base.BaseActivity
import com.kotlin.wanandroid.base.BaseMVPActivity
import com.kotlin.wanandroid.mvp.contract.MainContract
import com.kotlin.wanandroid.mvp.model.bean.UserInfoBody

class MainActivity : BaseMVPActivity<MainContract.View, MainContract.Preseter>(), MainContract.View {
    override fun attachLayoutRes(): Int = R.layout.activity_main

    override fun initData() {
        Log.d("Main", "initData: wyj")
    }

    override fun initView() {
    }

    override fun start() {
    }

    override fun createPrenter(): MainContract.Preseter {
        TODO("Not yet implemented")
    }

    override fun showLogoutSuccess(success: Boolean) {
        TODO("Not yet implemented")
    }

    override fun showUserInfo(bean: UserInfoBody) {
        TODO("Not yet implemented")
    }


}
