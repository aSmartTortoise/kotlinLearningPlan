package com.wyj.coroutine.exception

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

class GlobalCoroutineExceptionHandler(
    private val mErrorCode: Int,
    private val mErrorMsg: String = "",
    private val mResponse: Boolean = false
) : CoroutineExceptionHandler {
    override val key: CoroutineContext.Key<*>
        get() = CoroutineExceptionHandler

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        val msg = exception.stackTraceToString()
        Log.e("GlobalCoroutineExceptionHandler", "handleException: errorCode:$mErrorCode msg:$msg")
    }
}