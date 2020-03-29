package com.wyj.mvplayter.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast

abstract class BaseActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initView()
        initListener()
        initData()

    }

    abstract fun getLayoutId(): Int

    protected fun initView() {
    }

    protected fun initListener() {
    }

    protected fun initData() {
    }

    protected fun showToast(msg: String) {
        toast(msg)
    }


}