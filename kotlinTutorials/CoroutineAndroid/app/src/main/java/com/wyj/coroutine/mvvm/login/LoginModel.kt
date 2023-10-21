package com.wyj.coroutine.mvvm.login

import android.util.Log
import com.google.gson.Gson
import com.wyj.coroutine.model.BaseResponse
import com.wyj.coroutine.model.User
import com.wyj.coroutine.network.HttpManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LoginModel() {

    suspend fun loginCoroutine(account: String, pwd: String) = suspendCancellableCoroutine { cont ->
        login(account, pwd, object : Callback<User> {
            override fun onSuccess(value: User) {
                cont.resume(value)
            }

            override fun onError(t: Throwable) {
                cont.resumeWithException(t)
            }
        })
    }

    private fun login(account: String, pwd: String, callback: Callback<User>) {
        try {
            val responseBody = HttpManager.getInstance().service.loginNotSuspend(account, pwd)
            println("xxx")
        } catch (e: Exception) {
            callback.onError(e)
        }
    }


    suspend fun login (
        account: String,
        pwd: String,
        onSuccess:  (User?) -> Unit,
        onError:  (Exception) -> Unit,
        onComplete: () -> Unit,
    ) {
        launchRequest({
            Log.d("LoginModel", "login: login")
            HttpManager.getInstance().service.login(account, pwd)
        }, onSuccess, onError, onComplete)
    }


    private suspend inline fun <reified T : Any> launchRequest(
        crossinline block: suspend () -> BaseResponse<T>,
        onSuccess:  (T?) -> Unit,
        noinline onError:  ((Exception)-> Unit) ? = null,
        noinline onComplete:  (() -> Unit)? = null ) {
        try {
            val response = block()
            when (response.errorCode) {
                0 -> {
                    onSuccess.invoke(response.data)
                }
                else -> onError?.invoke(IllegalStateException(response.errorMsg))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            when (e) {
                is UnknownHostException -> {
                    //...
                }
                //...  各种需要单独处理的异常
                is ConnectException -> {
                    //...
                }
                else -> {
                    //...
                }
            }
            onError?.invoke(e)
        } finally {
            onComplete?.invoke()
        }
    }

}