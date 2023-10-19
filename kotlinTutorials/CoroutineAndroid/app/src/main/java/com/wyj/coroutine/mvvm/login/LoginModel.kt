package com.wyj.coroutine.mvvm.login

import com.wyj.coroutine.model.BaseResponse
import com.wyj.coroutine.model.User
import com.wyj.coroutine.network.HttpManager
import java.net.ConnectException
import java.net.UnknownHostException

class LoginModel() {
    suspend fun login(account: String, pwd: String): BaseResponse<User> {
        return HttpManager.getInstance().service.login(account, pwd)
    }

    suspend fun login (
        account: String,
        pwd: String,
        onSuccess:  (User?) -> Unit,
        onError:  (Exception) -> Unit,
        onComplete: () -> Unit,
    ) {
        launchRequest({
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
            onSuccess.invoke(response.data)
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
        }finally {
            onComplete?.invoke()
        }
    }

}