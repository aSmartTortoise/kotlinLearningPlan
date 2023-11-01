package com.wyj.coroutine.mvvm.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wyj.coroutine.extention.requestMain
import com.wyj.coroutine.model.ApiResult
import com.wyj.coroutine.model.User
import com.wyj.coroutine.network.HttpManager
import kotlinx.coroutines.launch

/**
 * https://blog.csdn.net/taotao110120119/article/details/110878525
 * https://juejin.cn/post/6962921891501703175#heading-3
 */
class LoginViewModel() : ViewModel() {
    companion object {
        private const val TAG = "LoginViewModel"
    }
    private var loginModel: LoginModel = LoginModel()
    private val userInfoData = MutableLiveData<User>()

    fun login(account: String, pwd: String) {
        requestMain {
            loginModel.login(account, pwd,
                onSuccess = {
                    userInfoData.postValue(it)
                    Log.d(TAG, "login: user:$it")
                },
                onError = {
                    Log.d(TAG, "login: error:$it")
                },
                onComplete = {

                }
            )
        }
        Log.d(TAG, "login: wyj ")
    }

    fun login2(account: String, pwd: String) {
        viewModelScope.launch {
            when (val userApiResult = HttpManager.service.login2(account, pwd)) {
                is ApiResult.Success -> {
                    val user = userApiResult.data.data
                    Log.d(TAG, "login2: name: ${user.username}")
                }

                is ApiResult.Failure -> {
                    Log.d(
                        TAG,
                        "login2: errorCode:${userApiResult.errorCode}, errorMsg:${userApiResult.errorMsg}"
                    )
                }
            }

        }
    }
}