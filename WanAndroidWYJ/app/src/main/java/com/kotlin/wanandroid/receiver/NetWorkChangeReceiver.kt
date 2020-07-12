package com.kotlin.wanandroid.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kotlin.wanandroid.utils.NetworkUtils

class NetWorkChangeReceiver: BroadcastReceiver() {


    private val hasNetwork: Boolean by

    override fun onReceive(context: Context, intent: Intent) {
        val connected = NetworkUtils.isNetworkConnected(context)

    }
}