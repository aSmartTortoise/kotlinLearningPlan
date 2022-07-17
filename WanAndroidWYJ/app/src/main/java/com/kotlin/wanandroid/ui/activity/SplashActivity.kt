package com.kotlin.wanandroid.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.kotlin.wanandroid.R
import com.kotlin.wanandroid.base.BaseActivity
import com.kotlin.wanandroid.timemonitor.TimeMonitorConfig
import com.kotlin.wanandroid.timemonitor.TimeMonitorManager
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        TimeMonitorManager
            .get()
            .getTimeMonitor(TimeMonitorConfig.TIME_MONITOR_ID_APPLICATION_START)
            .recordingTimeTag("SplashActivity-onCreate")
        super.onCreate(savedInstanceState)
        TimeMonitorManager
            .get()
            .getTimeMonitor(TimeMonitorConfig.TIME_MONITOR_ID_APPLICATION_START)
            .recordingTimeTag("SplashActivity-onCreate-Over")
    }
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
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onStart() {
        super.onStart()
        TimeMonitorManager
            .get()
            .getTimeMonitor(TimeMonitorConfig.TIME_MONITOR_ID_APPLICATION_START)
            .end("SplashActivity-onStart", false)
    }

    override fun onResume() {
        super.onResume()
        TimeMonitorManager
            .get()
            .getTimeMonitor(TimeMonitorConfig.TIME_MONITOR_ID_APPLICATION_START)
            .recordingTimeTag("SplashActivity-onResume")
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        TimeMonitorManager
            .get()
            .getTimeMonitor(TimeMonitorConfig.TIME_MONITOR_ID_APPLICATION_START)
            .recordingTimeTag("SplashActivity-onWindowFocusChanged")
    }

}