package com.wyj.coroutine.network.interceptor

import android.util.Log
import com.wyj.coroutine.network.CookieManager
import okhttp3.Interceptor
import okhttp3.Response

class CookieInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request.newBuilder().build())
        val cookieHeader = response.headers("set-cookie")
        if (request.url().toString()
                .contains("user/login") && cookieHeader.isNotEmpty()
        ) {
            for (element in cookieHeader) {
                Log.d("CookieInterceptor", "intercept: wyj element:$element")
            }
            CookieManager.encodeCookie(cookieHeader)
        }
        return response
    }
}