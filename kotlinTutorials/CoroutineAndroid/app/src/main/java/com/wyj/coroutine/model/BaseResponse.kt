package com.wyj.coroutine.model

data class BaseResponse<T>(val errorCode: Int, val errorMsg: String, val data: T)
