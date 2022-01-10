package com.jie.coroutine

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

class GlobalCoroutineExceptionHandler(private val errCode: Int, private val errMsg: String = "",
                                      private val report: Boolean = false) : CoroutineExceptionHandler {
    override val key: CoroutineContext.Key<*>
        get() = CoroutineExceptionHandler

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        val msg = exception.stackTraceToString()
        Log.e("$errCode", "GlobalCoroutineExceptionHandler: $msg")
    }
}