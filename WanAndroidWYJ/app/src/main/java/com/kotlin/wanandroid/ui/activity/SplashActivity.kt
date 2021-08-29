package com.kotlin.wanandroid.ui.activity

import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.kotlin.wanandroid.R
import com.kotlin.wanandroid.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {
    override fun attachLayoutRes(): Int = R.layout.activity_splash

    override fun initData() {

    }

    override fun initView() {
        val alphaAnimation = AlphaAnimation(0.3F, 1.0F)
        alphaAnimation.run {
            duration = 2000L
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    jumpToMain()
                }

                override fun onAnimationStart(animation: Animation?) {
                }

            })
        }
        layout_splash.startAnimation(alphaAnimation)
    }

    override fun start() {
    }

    override fun initColor() {
        super.initColor()
        layout_splash.setBackgroundColor(mThemeColor)
    }

    fun jumpToMain() {
        Log.d("Splash", "jumpToMain: wyj")
    }

}