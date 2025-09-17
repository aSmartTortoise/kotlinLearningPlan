package com.kotlin.coroutine.practise

import kotlinx.coroutines.suspendCancellableCoroutine

/**
 *  用来演示顶层函数suspendCancellableCoroutine 经反编译后的Java代码实现。
 */

suspend fun test() {
    suspendCancellableCoroutine<String> {
        println("test")
    }
}