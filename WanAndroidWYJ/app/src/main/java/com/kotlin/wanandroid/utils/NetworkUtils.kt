package com.kotlin.wanandroid.utils

import android.content.Context
import android.net.ConnectivityManager

class NetworkUtils {

    companion object {
        fun isNetworkConnected(context: Context): Boolean {
            val manager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = manager.activeNetworkInfo
            return (info != null && info.isConnected)
        }
    }


}