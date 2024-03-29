package com.wyj.coroutine.extention

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.wyj.coroutine.exception.GlobalCoroutineExceptionHandler
import kotlinx.coroutines.*

public fun NormalScope(): CoroutineScope = CoroutineScope(Dispatchers.Main.immediate)

inline fun AppCompatActivity.requestMain(
    errorCode: Int = -1,
    errorMsg: String = "",
    report: Boolean = false,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch(GlobalCoroutineExceptionHandler(errorCode, errorMsg, report)) {
        block.invoke(this)
    }
}

inline fun AppCompatActivity.requestIO(
    errorCode: Int = -1,
    errorMsg: String = "",
    report: Boolean = false,
    crossinline block: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launch(
        Dispatchers.IO + GlobalCoroutineExceptionHandler(errorCode, errorMsg, report)) {
        block.invoke(this)
    }
}

inline fun AppCompatActivity.delayMain(
    delayTime: Long,
    errorCode: Int = -1,
    errorMsg: String = "",
    report: Boolean = false,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch(GlobalCoroutineExceptionHandler(errorCode, errorMsg, report)) {
        withContext(coroutineContext) {
            delay(delayTime)
        }
        block.invoke(this)
    }
}

inline fun Fragment.requestMain(
    errorCode: Int = -1, errorMsg: String = "", report: Boolean = false,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch(GlobalCoroutineExceptionHandler(errorCode, errorMsg, report)) {
        block.invoke(this)
    }
}

inline fun Fragment.requestIO(
    errorCode: Int = -1, errorMsg: String = "", report: Boolean = false,
    crossinline block: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launch(Dispatchers.IO + GlobalCoroutineExceptionHandler(errorCode, errorMsg, report)) {
        block.invoke(this)
    }
}

inline fun Fragment.delayMain(
    delayTime: Long, errorCode: Int = -1, errorMsg: String = "", report: Boolean = false,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch(GlobalCoroutineExceptionHandler(errorCode, errorMsg, report)) {
        withContext(Dispatchers.IO) {
            delay(delayTime)
        }
        block.invoke(this)
    }
}

inline fun ViewModel.requestMain(
    errorCode: Int = -1, errorMsg: String = "", report: Boolean = false,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch(GlobalCoroutineExceptionHandler(errorCode, errorMsg, report)) {
        block.invoke(this)
    }
}

inline fun ViewModel.requestIO(
    errorCode: Int = -1, errorMsg: String = "", report: Boolean = false,
    crossinline block: suspend CoroutineScope.() -> Unit
): Job {
    return viewModelScope.launch(Dispatchers.IO + GlobalCoroutineExceptionHandler(errorCode, errorMsg, report)) {
        block.invoke(this)
    }
}

inline fun ViewModel.delayMain(
    delayTime: Long, errorCode: Int = -1, errorMsg: String = "", report: Boolean = false,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch(GlobalCoroutineExceptionHandler(errorCode, errorMsg, report)) {
        withContext(Dispatchers.IO) {
            delay(delayTime)
        }
        block.invoke(this)
    }
}
