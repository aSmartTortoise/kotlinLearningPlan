package com.wyj.coroutine.mvvm.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wyj.coroutine.extention.requestMain
import com.wyj.coroutine.model.User

class LoginViewModel() : ViewModel() {
    var loginModel: LoginModel = LoginModel()
    private val userInfoData = MutableLiveData<User>()

    fun login(account: String, pwd: String) {
        requestMain {
            loginModel.login(account, pwd,
                onSuccess = {
                    userInfoData.postValue(it)
                },
                onError = {

                },
                onComplete = {

                }
            )
        }
    }
}