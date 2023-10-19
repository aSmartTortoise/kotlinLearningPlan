package com.wyj.coroutine.network

import android.text.TextUtils
import com.wyj.coroutine.MyApplication
import com.wyj.coroutine.manager.PreferenceManager

object CookieManager {
    fun encodeCookie(cookies: List<String>) {
        val set = HashSet<String>()
        for (cookie in cookies) {
            val split = cookie.split(";").toTypedArray()
            for (element in split) {
                if (!TextUtils.isEmpty(element)) {
                    set.add(element)
                }
            }
        }
        if (set.isNotEmpty()) {
            val sb = StringBuilder()
            for (s in set) {
                sb.append(s).append(";")
            }
            val lastIndex = sb.lastIndexOf(";")
            if (lastIndex != -1) {
                sb.deleteCharAt(lastIndex)
            }
            PreferenceManager.writeString(MyApplication.application, "cookie", sb.toString())
        }
    }
}