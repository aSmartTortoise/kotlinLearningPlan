package com.kotlin.wanandroid.http.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 *  [Retrofit和OkHttp使用网络缓存数据](https://www.jianshu.com/p/e0dd6791653d)
 */
class CacheNetworkInterceptor : Interceptor {

    companion object {
        private const val TAG = "CacheNetworkInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

//        Log.d(TAG, "intercept: request url:${request.url().toString()}")

        val response = chain.proceed(request)
        val cacheControl = response.cacheControl().toString()

//        Log.d(TAG, "intercept: response header:${response.headers().toString()}")
//        Log.d(TAG, "intercept: response cache control:$cacheControl")
        val maxAge = 60
        return response
            .newBuilder()
            .removeHeader("Pragma")
            //对响应进行最大60秒有效期的缓存，会对CacheInterceptor的CacheStrategy有影响
//            .header("Cache-Control", "public, max-age=$maxAge")
            .build()
    }
}