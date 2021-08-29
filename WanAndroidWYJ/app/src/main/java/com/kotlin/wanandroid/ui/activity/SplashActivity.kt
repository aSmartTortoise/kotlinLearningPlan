package com.kotlin.wanandroid.ui.activity

import android.view.animation.AlphaAnimation
import com.kotlin.wanandroid.R
import com.kotlin.wanandroid.ui.base.BaseActivity

class SplashActivity : BaseActivity() {
    private var mAlphaAnimation: AlphaAnimation? = null
    override fun attachLayoutRes(): Int = R.layout.activity_splash

    override fun initData() {

    }

    override fun initView() {
        mAlphaAnimation = AlphaAnimation(0.3F, 1.0F)

    }

    override fun start() {
        TODO("Not yet implemented")
    }

}