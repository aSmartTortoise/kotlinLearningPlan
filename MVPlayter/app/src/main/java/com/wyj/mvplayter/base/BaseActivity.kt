package com.wyj.mvplayter.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wyj.mvplayter.ui.activity.MainActivity
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

abstract class BaseActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(getLayoutId())
        initView()
        initListener()
        initData()

    }

    abstract fun getLayoutId(): Int

    protected open fun initView() {
    }

    protected open fun initListener() {
    }

    protected open fun initData() {
    }

    protected open fun showToast(msg: String) {
        toast(msg)
    }

    inline fun <reified T : BaseActivity> startActivityAndFinish() {
        startActivity<T>()
        finish()
    }


}