package com.kotlin.coroutine

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import java.lang.IllegalStateException

fun main(args: Array<String>) {
    runBlocking {
        supervisorScopeFunctionExceptionTest()
    }
}
suspend fun supervisorScopeFunctionExceptionTest() {
    try {
        supervisorScope {
            println("SupervisorCoroutine end.")
            throw IllegalStateException("SupervisorCoroutine Exception")
        }
    } catch (e: Exception) {
        println("exception occur, e:$e")
    }
}