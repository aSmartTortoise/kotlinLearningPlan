package com.kotlin.wanandroid.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Trace
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    companion object {
        const val TAG = "SplashActivity"
    }
    @SuppressLint("UnclosedTrace")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Trace.beginSection("splashOnCreate")
        jumpToMain()
        Trace.endSection()
    }

    private fun jumpToMain() {
        //发现冷启动过程中有黑屏，应该时application的初始化以及MainActivity的初始化耗时导致的
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

}