package com.kotlin.kt06

import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    // 源码中runBlocking函数的签名部分使用了actual修饰符关键字。
    runBlocking {
        println("Hello Kotlin Coroutine.")
    }
}