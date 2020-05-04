package com.kotlin.kt08

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface GithubService {
    @GET("/repos/enbandari/Kotlin-Tutorials/stargazers")
    fun getStarGazers():Call<List<UserEntity>>
}

object Service {
    val githubService by lazy {
        Retrofit
            .Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GithubService::class.java)
    }
}

fun main() {
    Service.githubService.getStarGazers().execute().body()?.map(::println)
}