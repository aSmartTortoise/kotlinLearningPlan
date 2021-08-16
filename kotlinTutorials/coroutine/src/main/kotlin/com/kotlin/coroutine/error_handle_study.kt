package com.kotlin.coroutine

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.ArithmeticException

/**
 *  19 异常的处理
 *      https://www.kotlincn.net/docs/reference/coroutines/exception-handling.html
 *      被取消的协程会在挂起点抛出CancellationException并且会被协程的机制忽略。
 *  19.1 异常的传播
 *
 *
 **/

fun main() {
    runBlocking {
        val job = GlobalScope.launch {
            println("throw exceptin from launch")
            throw IndexOutOfBoundsException()
        }
        job.join()
        println("joined failed job")
        val deferred = GlobalScope.async {
            println("throws exception from aync")
            throw ArithmeticException()
        }
        try {
            deferred.await()
            println("defered wait")
        } catch (e: ArithmeticException) {
            println("caugth ArithmeticException")
        }
    }
}