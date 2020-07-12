package com.kotlin.wanandroid.ui.base

import android.content.Context
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.cxz.multiplestatusview.MultipleStatusView
import com.kotlin.wanandroid.R
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity: AppCompatActivity() {

    lateinit var mTipView: View
    lateinit var mWindowManager: WindowManager
    lateinit var mLayoutParams: WindowManager.LayoutParams
    protected var mLayoutStatusView: MultipleStatusView? = null
    open val mRetryClickListener: View.OnClickListener = View.OnClickListener {
        start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onCreate(savedInstanceState)
        setContentView(attachLayoutRes())
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
        initData()
        initTipView()
        initView()
        start()
        initListener()
    }

    abstract fun attachLayoutRes(): Int

    open fun useEventBus(): Boolean = true

    abstract fun initData()

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
        mLayoutParams.windowAnimations = R.style.anim_float_view
    }

    abstract fun initView()

    /**
     * 开始请求
     */
    abstract fun start()

    private fun initListener() {
        mLayoutStatusView?.setOnClickListener(mRetryClickListener)
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")

    }
}