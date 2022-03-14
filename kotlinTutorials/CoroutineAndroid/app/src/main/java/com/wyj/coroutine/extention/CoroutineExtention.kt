package com.wyj.coroutine.extention

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.wyj.coroutine.exception.GlobalCoroutineExceptionHandler
import kotlinx.coroutines.*

inline fun AppCompatActivity.requestMain(
    errorCode: Int = -1,
    errorMsg: String = "",
    response: Boolean = false,
    noinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch(GlobalCoroutineExceptionHandler(errorCode, errorMsg, response)) {
        block.invoke(this)
    }
}

inline fun AppCompatActivity.requestIO(
    errorCode: Int = -1,
    errorMsg: String = "",
    response: Boolean = false,
    noinline block: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launch(
        Dispatchers.IO + GlobalCoroutineExceptionHandler(
            errorCode,
            errorMsg,
            response
        )
    ) {
        block.invoke(this)
    }
}

inline fun AppCompatActivity.delayMain(
    delayTime: Long,
    errorCode: Int = -1,
    errorMsg: String = "",
    response: Boolean = false,
    noinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch(GlobalCoroutineExceptionHandler(errorCode, errorMsg, response)) {
        withContext(Dispatchers.IO) {
            delay(delayTime)
        }
        block.invoke(this)
    }
}