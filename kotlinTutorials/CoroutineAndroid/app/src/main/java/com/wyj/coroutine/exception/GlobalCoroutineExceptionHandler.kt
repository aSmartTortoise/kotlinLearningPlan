package com.wyj.coroutine.exception

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

class GlobalCoroutineExceptionHandler(
    private val errorCode: Int,
    private val errorMsg: String = "",
    private val report: Boolean = false
) : CoroutineExceptionHandler {
    override val key: CoroutineContext.Key<*>
        get() = CoroutineExceptionHandler

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        val msg = exception.stackTraceToString()
        Log.e("GlobalExceptionHandler", "handleException: errorCode:$errorCode msg:$msg")
    }
}