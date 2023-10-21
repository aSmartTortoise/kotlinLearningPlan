package com.wyj.coroutine.mvvm.login

interface Callback<T> {
    fun onSuccess(value: T)

    fun onError(t: Throwable)
}