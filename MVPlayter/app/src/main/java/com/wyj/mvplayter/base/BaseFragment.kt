package com.wyj.mvplayter.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.toast

abstract class BaseFragment: Fragment(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    protected fun init() {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return getContentView()
    }

    abstract fun getContentView(): View

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        initListener()
        initData()
    }

    protected open fun initView() {
    }

    protected open fun initListener() {
    }

    protected open fun initData() {
    }

    protected open fun showToast(msg: String) {
        toast(msg)
    }


}