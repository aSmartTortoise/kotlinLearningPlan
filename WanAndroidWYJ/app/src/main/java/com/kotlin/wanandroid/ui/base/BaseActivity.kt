package com.kotlin.wanandroid.ui.base

import android.content.Context
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.wanandroid.R
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity: AppCompatActivity() {

    lateinit var mTipView: View
    lateinit var mWindowManager: WindowManager
    lateinit var mLayoutParams: WindowManager.LayoutParams

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onCreate(savedInstanceState)
        setContentView(attachLayoutRes())
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
        initData()
        initTipView()
    }

    private fun initTipView() {
        mTipView = layoutInflater.inflate(R.layout.layout_network_tip, null)
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mLayoutParams = WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT)
        mLayoutParams.gravity = Gravity.TOP
        mLayoutParams.x = 0
        mLayoutParams.y = 0
        mLayoutParams.windowAnimations =
    }

    abstract fun attachLayoutRes(): Int

    open fun useEventBus(): Boolean = true

    abstract fun initData()
}