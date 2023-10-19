package com.wyj.coroutine

import android.app.Application

class MyApplication : Application() {
    companion object {
        lateinit var application: MyApplication
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}