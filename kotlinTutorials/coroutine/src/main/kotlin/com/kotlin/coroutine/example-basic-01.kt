package com.kotlin.coroutine

import kotlinx.coroutines.*
import kotlin.concurrent.thread

/**
 *  18 协程
 *      当我们使用GlobalScope.launch的时候，会创建一个顶层协程。如果我们忘记对这个协程的引用，它对内存资源
 *  的占用会得不到释放。https://www.kotlincn.net/docs/reference/coroutines/basics.html。一个更好的解
 *  决办法是在执行操作的指定作用域中启动协程。
 *  18.1 作用域构建器
 *      除了由不同的协程构建器提供协程作用域外，还可以使用coroutineScope构建器声明自己的作用域，它会创建一个
 *  协程作用域，并在其他已启动的协程执行完毕之前不会结束。
 *
 *
 *
 *
 *
 */


//fun main(args: Array<String>) {
//    startGlobalScopeCoroutin()
//    startANormalThread()
//    runBlockingTest()
//    jobJoinStudy()
//}
//fun main() = runBlocking {
//    //CoroutineScope的实例将在该作用域中得以引用。不需要使用Job引用并join操作，避免了对协程引用的资源得不到
//    //及时释放的问题。
//    launch {
//        delay(1000L)
//        println("coroutine!")
//    }
//    println("hello world, ")
//}

fun main() = runBlocking {
    launch {
        delay(200L)
        println("Task from runBlocing!")
    }

    coroutineScope {
        launch {
//            delay(500L)
            println("Task from nested launch")
        }

        delay(100L)
        println("Task from coroutin scope!")
    }

    println("Coroutine scope is over!")
}

private fun jobJoinStudy() {
    val job = GlobalScope.launch {
        delay(3000L)
        println("coroutine!")
    }
    println("hello world, ")
    runBlocking {
        job.join()
    }
}

private fun runBlockingTest() {
    GlobalScope.launch {
        delay(1000L)
        println("Coroutine!")
    }
    println("hello world, ")
    //调用runBlcoking函数的主线程 会被阻塞，直到runBlocking内部的协程执行结束。
    runBlocking {
        delay(2000L)
    }
}

private fun startANormalThread() {
    thread {
        Thread.sleep(1000L)
        println("Coroutines!")
    }
    println("Hello World, ")
    Thread.sleep(2000L)
}

private fun startGlobalScopeCoroutin() {
    GlobalScope.launch {
        delay(1000L)
        println("coroutine!")
    }
    println("Hello World")
    Thread.sleep(2000L)
}