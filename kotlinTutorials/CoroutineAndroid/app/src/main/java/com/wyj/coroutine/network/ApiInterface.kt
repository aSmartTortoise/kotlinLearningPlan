package com.wyj.coroutine.network

import com.wyj.coroutine.model.ApiResult
import com.wyj.coroutine.model.BaseResponse
import com.wyj.coroutine.model.User
import okhttp3.Call
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {
    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     */
    @FormUrlEncoded
    @POST("/user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): BaseResponse<User>

    @FormUrlEncoded
    @POST("/user/login")
    suspend fun login2(
        @Field("username") username: String,
        @Field("password") password: String
    ): ApiResult<BaseResponse<User>>

    @FormUrlEncoded
    @POST("/user/login")
    fun loginNotSuspend(
        @Field("username") username: String,
        @Field("password") password: String
    ): ResponseBody
}