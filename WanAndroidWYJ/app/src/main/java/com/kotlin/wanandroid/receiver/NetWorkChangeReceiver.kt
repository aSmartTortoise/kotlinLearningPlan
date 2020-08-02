package com.kotlin.wanandroid.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kotlin.wanandroid.constant.Constant
import com.kotlin.wanandroid.event.NetworkChangeEvent
import com.kotlin.wanandroid.utils.NetworkUtils
import com.kotlin.wanandroid.utils.Preference
import org.greenrobot.eventbus.EventBus

class NetWorkChangeReceiver: BroadcastReceiver() {


    private val hasNetwork: Boolean by Preference(Constant.HAS_NETWORK_KEY, true)

    override fun onReceive(context: Context, intent: Intent) {
        val connected = NetworkUtils.isNetworkConnected(context)
        if (connected) {
            if (connected != hasNetwork) {
                EventBus.getDefault().post(NetworkChangeEvent(connected))
            }
        } else {
            EventBus.getDefault().post(NetworkChangeEvent(connected))
        }
    }
}