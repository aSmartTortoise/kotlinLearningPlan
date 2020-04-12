package com.wyj.mvplayter.utils

import android.os.Handler
import android.os.Looper

object ThreadUtils {
    var handler = Handler(Looper.getMainLooper())

    fun runOnUIThread(runnable: Runnable) {
        handler.post(runnable)
    }
}