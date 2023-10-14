package com.kotlin.coroutine.practise

import kotlinx.coroutines.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun main(args: Array<String>): Unit = runBlocking {

}
suspend fun Call.await(): ResponseBody = suspendCancellableCoroutine { cont ->
    enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            cont.resumeWithException(e)
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                cont.resume(response.body!!)
            } else {
                cont.resumeWithException(IOException("网络请求错误。"))
            }
        }
    })

}