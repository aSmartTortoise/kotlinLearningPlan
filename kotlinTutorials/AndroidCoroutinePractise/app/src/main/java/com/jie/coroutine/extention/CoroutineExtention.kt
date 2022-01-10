package com.jie.coroutine.extention

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jie.coroutine.GlobalCoroutineExceptionHandler
import kotlinx.coroutines.*

/**
 * @param errCode 错误码
 * @param errMsg 简要错误信息
 * @param report 是否需要上报
 * @param block 需要执行的任务
 */
inline fun AppCompatActivity.requestMain(
    errCode: Int = -1, errMsg: String = "", report: Boolean = false,
    noinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch(GlobalCoroutineExceptionHandler(errCode, errMsg, report)) {
        block.invoke(this)
    }

}

/**
 * @param errCode 错误码
 * @param errMsg 简要错误信息
 * @param report 是否需要上报
 * @param block 需要执行的任务
 */
inline fun AppCompatActivity.requestIO(
    errCode: Int = -1, errMsg: String = "", report: Boolean = false,
    noinline block: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launch(
        Dispatchers.IO + GlobalCoroutineExceptionHandler(
            errCode,
            errMsg,
            report
        )
    ) {
        block.invoke(this)
    }
}

/**
 * @param errCode 错误码
 * @param errMsg 简要错误信息
 * @param report 是否需要上报
 * @param block 需要执行的任务
 */
inline fun AppCompatActivity.delayMain(
    delayTime: Long, errCode: Int = -1, errMsg: String = "", report: Boolean = false,
    noinline block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch(GlobalCoroutineExceptionHandler(errCode, errMsg, report)) {
        withContext(Dispatchers.IO) {
            delay(delayTime)
        }

        block.invoke(this)
    }
}



