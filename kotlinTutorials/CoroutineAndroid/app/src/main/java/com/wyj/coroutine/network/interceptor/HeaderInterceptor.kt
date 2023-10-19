package com.wyj.coroutine.network.interceptor

import android.text.TextUtils
import com.wyj.coroutine.MyApplication
import com.wyj.coroutine.manager.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newBuilder = request.newBuilder()
        newBuilder.addHeader("Content-Type", "application/json; charset=utf-8")
        val host = request.url().host()
        val url = request.url().toString()
        if (!TextUtils.isEmpty(host) && url.contains("article")) {
            val cookie: String = PreferenceManager.readString(MyApplication.application, "cookie")
            if (!TextUtils.isEmpty(cookie)) {
                newBuilder.addHeader("Cookie", cookie)
            }
        }
        return chain.proceed(newBuilder.build())
    }
}