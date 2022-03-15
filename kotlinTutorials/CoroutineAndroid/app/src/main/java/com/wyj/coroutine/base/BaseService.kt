package com.wyj.coroutine.base

import android.app.Service
import com.wyj.coroutine.exception.GlobalCoroutineExceptionHandler
import com.wyj.coroutine.extention.NormalScope
import kotlinx.coroutines.*

abstract class BaseService : Service() {
    private val mNormalScope = NormalScope()

    override fun onDestroy() {
        mNormalScope.cancel()
        super.onDestroy()
    }

    protected fun requestMain(
        errorCode: Int = -1, errorMsg: String = "", response: Boolean = false,
        block: suspend CoroutineScope.() -> Unit
    ) {
        mNormalScope.launch(GlobalCoroutineExceptionHandler(errorCode, errorMsg, response)) {
            block.invoke(this)
        }
    }

    protected fun requestIO(
        errorCode: Int = -1, errorMsg: String = "", response: Boolean = false,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return mNormalScope.launch(
            Dispatchers.IO + GlobalCoroutineExceptionHandler(
                errorCode,
                errorMsg,
                response
            )
        ) {
            block.invoke(this)
        }
    }

    protected fun delayMain(
        delayTime: Long, errorCode: Int = -1, errorMsg: String = "", response: Boolean = false,
        block: suspend CoroutineScope.() -> Unit
    ) {
        mNormalScope.launch(GlobalCoroutineExceptionHandler(errorCode, errorMsg, response)) {
            withContext(Dispatchers.IO) {
                delay(delayTime)
            }
            block.invoke(this)
        }
    }

}