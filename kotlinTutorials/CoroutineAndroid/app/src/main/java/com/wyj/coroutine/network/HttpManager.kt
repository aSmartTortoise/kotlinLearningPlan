package com.wyj.coroutine.network

import android.util.Log
import com.wyj.coroutine.BuildConfig
import com.wyj.coroutine.MyApplication
import com.wyj.coroutine.adapter.ApiResultCallAdapterFactory
import com.wyj.coroutine.network.interceptor.BusinessErrorInterceptor
import com.wyj.coroutine.network.interceptor.CookieInterceptor
import com.wyj.coroutine.network.interceptor.HeaderInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object HttpManager {
    val service: ApiInterface by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        build()
    }

    private fun build(): ApiInterface {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("HttpManager", "log: wyj message:$message")
        }
        if (BuildConfig.DEBUG) {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        val cache = Cache(
            File(MyApplication.application.cacheDir, "cache"),
            1024 * 1024 * 50
        )
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(CookieInterceptor())
            .addInterceptor(BusinessErrorInterceptor())
            .cache(cache)
            .build()
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://www.wanandroid.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .build().run {
                create(ApiInterface::class.java)
            }
    }
}