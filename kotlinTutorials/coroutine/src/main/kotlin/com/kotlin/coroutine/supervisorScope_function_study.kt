package com.kotlin.coroutine

import kotlinx.coroutines.supervisorScope

suspend fun supervisorScopeFunctionTest() {
    supervisorScope {
        println("SupervisorCoroutine end.")
    }
}