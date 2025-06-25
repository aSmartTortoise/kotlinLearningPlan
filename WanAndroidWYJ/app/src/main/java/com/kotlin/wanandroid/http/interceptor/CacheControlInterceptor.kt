package com.kotlin.wanandroid.http.interceptor

import com.kotlin.wanandroid.WanAndroidApplication
import com.kotlin.wanandroid.utils.NetWorkUtil
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit
/**
 *  [Retrofit和OkHttp使用网络缓存数据](https://www.jianshu.com/p/e0dd6791653d)
 */
class CacheControlInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request
        if (!NetWorkUtil.isNetworkAvailable(WanAndroidApplication.context)) {
            // 无网络时，设置候选缓存响应过期之后的最大可使用的时间24h，如果缓存时间超过了有效期后的12h，
            // 则因为CacheControl#onlyIfCached 为true，则返回504响应。
            request = chain.request().newBuilder()
                .cacheControl(CacheControl.Builder()
                    .onlyIfCached()
                    .maxStale(60 * 60 * 24, TimeUnit.SECONDS)
                    .build())
                .build()

        } else {
            // 有网络时，设置候选缓存响应最大有效时间30s，该设置会CacheInterceptor的CacheStrategy有效果
            // 这样在缓存有效期内的请求会复用缓存响应。
           request = chain.request().newBuilder()
                .cacheControl(CacheControl.Builder()
                    .maxAge(30, TimeUnit.SECONDS)
                    .build())
               .build()
        }
        return chain.proceed(request)
    }
}