package com.kotlin.coroutine

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 *  协程完成流程调试
 */
fun main(args: Array<String>) {
    runBlocking {
        val parentJob = launch {
            val childJob = async {
                delay(30_000L)
                println("childJob end.")
            }
            println("parentJob end.")
        }

        println("runBlocking end.")
    }
}