package com.cxz.wanandroid.http.interceptor

import android.util.Log
import com.cxz.wanandroid.constant.HttpConstant
import com.kotlin.wanandroid.constant.Constant
import com.kotlin.wanandroid.utils.Preference
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author chenxz
 * @date 2018/9/26
 * @desc HeaderInterceptor: 设置请求头
 */
class HeaderInterceptor : Interceptor {

    companion object {
        private const val TAG = "HeaderInterceptor"
    }

    /**
     * token
     */
    private var token: String by Preference(Constant.TOKEN_KEY, "")

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val builder = request.newBuilder()

        builder.addHeader("Content-type", "application/json; charset=utf-8")
        // .header("token", token)
        // .method(request.method(), request.body())

        val domain = request.url().host()
        val url = request.url().toString()
        if (domain.isNotEmpty() && (url.contains(HttpConstant.COLLECTIONS_WEBSITE)
                        || url.contains(HttpConstant.UNCOLLECTIONS_WEBSITE)
                        || url.contains(HttpConstant.ARTICLE_WEBSITE)
                        || url.contains(HttpConstant.TODO_WEBSITE)
                        || url.contains(HttpConstant.COIN_WEBSITE))) {
            val spDomain: String by Preference(domain, "")
            val cookie: String = if (spDomain.isNotEmpty()) spDomain else ""
            if (cookie.isNotEmpty()) {
                // 将 Cookie 添加到请求头
                builder.addHeader(HttpConstant.COOKIE_NAME, cookie)
            }
        }
//        Log.d(TAG, "intercept: request url:${url}")
//        Log.d(TAG, "intercept: request cacheControl:${request.cacheControl().toString()}")
        val response = chain.proceed(builder.build())

        val cacheControl = response.cacheControl().toString()
//        Log.d(TAG, "intercept: response headers:${response.headers().toString()}")
//        Log.d(TAG, "intercept: response cacheControl:$cacheControl")
//        Log.d(TAG, "intercept: response code:${response.code()}")
        return response
    }

}