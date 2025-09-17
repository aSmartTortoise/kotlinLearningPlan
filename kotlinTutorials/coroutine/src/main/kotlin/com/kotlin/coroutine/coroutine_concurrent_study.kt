package com.kotlin.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 *  学习协程自己内部的并发实现方式：Mutex。
 */
fun main(args: Array<String>) {

    runBlocking {
//        mutexStudy()
        val threadLocal = ThreadLocal<String>().apply { set("Init") }
        printlnValue(threadLocal)
        val job = GlobalScope.launch(threadLocal.asContextElement("launch")) {
            printlnValue(threadLocal)
            threadLocal.set("launch changed")
            printlnValue(threadLocal)
            yield()
            printlnValue(threadLocal)
        }
        job.join()
        printlnValue(threadLocal)
    }
}

private fun printlnValue(threadLocal: ThreadLocal<String>) {
    println("${Thread.currentThread().name}, thread local value: ${threadLocal.get()}")
}

private suspend fun mutexStudy() {
    var count = 0
    val mutex = Mutex()
    repeat(100) {
        GlobalScope.launch {
            mutex.withLock {
                println("launch count:$it")
                count++
            }
        }
    }
    delay(10_000L)
    println("total count: $count")
}