package com.kotlin.coroutine

import kotlinx.coroutines.coroutineScope

/**
 *  调试`coroutineScope`顶层挂起函数的实现原理。需要结合反编译为Java代码理解
 */


/**
 *  协程运行过程中出现未捕获的异常，则`coroutineScope`函数会抛出指定的异常。
 */
suspend fun coroutineScopeTest() {
    coroutineScope {
        println("test.")
    }
}

