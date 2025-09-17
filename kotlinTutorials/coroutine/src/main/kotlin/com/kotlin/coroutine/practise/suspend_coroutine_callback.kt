package com.kotlin.coroutine.practise

import kotlinx.coroutines.*
import kotlin.coroutines.suspendCoroutine

class UserCall {

    // 这是一个使用回调的异步函数
    fun fetchUserData(callback: Callback) {
        // ... 执行网络请求等耗时操作
        callback.onSuccess("用户数据")
    }

}

interface Callback {
    fun onSuccess(data: String)
    fun onFailure(error: Exception)
}

suspend fun UserCall.fetchUserDataSuspend(): String = suspendCoroutine { cont ->
    fetchUserData(object : Callback {
        override fun onSuccess(data: String) {
            cont.resumeWith(Result.success(data))
        }

        override fun onFailure(error: Exception) {
            cont.resumeWith(Result.failure(error))
        }
    })

}

suspend fun UserCall.fetchUserDataSuspendCancellable(): String = suspendCancellableCoroutine { cont ->
    fetchUserData(object : Callback {
        override fun onSuccess(data: String) {
            cont.resumeWith(Result.success(data))
        }

        override fun onFailure(error: Exception) {
            cont.resumeWith(Result.failure(error))
        }
    })
}


fun main(args: Array<String>) {
    runBlocking {
//        val result = UserCall().fetchUserDataSuspend()
//        launch(Dispatchers.Default) {
//            val result = UserCall().fetchUserDataSuspendCancellable()
//            delay(2500L)
//            println("result:$result")
//        }

        launch {
            repeat(3) {
                println("job1 repeat $it times.")
                yield()
            }
        }

        launch {
            repeat(3) {
                println("job2 repeat $it times")
                yield()
            }
        }

    }
}




