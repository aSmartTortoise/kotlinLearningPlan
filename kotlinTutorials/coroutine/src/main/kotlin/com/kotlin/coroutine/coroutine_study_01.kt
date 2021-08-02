package com.kotlin.coroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 *  19 携程
 *  https://johnnyshieh.me/posts/kotlin-coroutine-introduction/
 *  19.1 挂起函数
 *      被修饰符suspend修饰的函数。挂起函数通普通函数一样接收参数和返回结果。调用挂起函数会引起协程挂
 *  起。挂起函数引起协程挂起时候，不会引起协程所在的线程挂起。挂起函数执行完以后会恢复协程，接着后面的
 *  代码才可以继续执行。挂起函数只能在协程中或其他挂起函数中被调用。事实上要启动一个协程至少要有一个挂
 *  起函数，它通常是一个Lambda表达式。suspend修饰符可以修饰普通函数、扩展函数和Lambda表达式。
 *  19.2 CoroutineDispatcher 协程调度器
 *      决定协程所在的线程或线程池。它可以决定协程运行于在一个特定的线程、一个线程池或者不指定任何线
 *  程。CoroutineDispatcher有四种标准实现：Dispatchers.Default、Dispatchers.IO、
 *  Dispatchers.Main和Dispatcher.Unconfined。Unconfined就是不指定线程。
 *
 */

fun main() {
    GlobalScope.launch {
        println("构建并启动了一个协程。")
    }
    println("main, end~")
}