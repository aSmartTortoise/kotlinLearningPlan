package com.kotlin.coroutine

import kotlinx.coroutines.*

/**
 *  19 协程
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
 *  19.3 Job & Deferred
 *      Job，任务，它封装了协程中需要执行的代码逻辑。Job可以取消，并由简单的声明周期。Job对应的状
 *  态有New、Active、Completing、Cancelling、Cancelled、Completed公六种状态。
 *      Job完成是没有返回值的，如果需要返回值的话，用Deferred，Deferred是Job的子类。
 *  19.4 协程构建器 coroutine builder
 *      CoroutineScope.launch函数就是一个coroutine builder。Kotlin中还有其他几种builder，负责
 *  创建协程。
 *  19.4.1 CoroutineScope.launch{}
 *      CoroutineScope.launch函数是最常用的coroutine builder，不阻塞当前线程，在后台创建一个协
 *  程，也可以指定协程调度器，比如在Android中常用的GlobalScope.launch(Dispatchers.Main){}。
 *  19.4.2 runBlocking{}
 *      runBlocking{}创建一个新的协程，阻塞当前线程，直到协程结束。这个不应该在协程中使用，主要是
 *  为main函数和测试设计的。
 *  19.4.3 withContext{}
 *      不创建协程，在指定协程上运行挂起代码块，并挂起该协程直至代码块运行完成。
 *  19.4.4 async{}
 *      可以与launch函数一样在后台创建一个新的协程，与launch不同的是，它结束后有返回值，Deferred。
 *  获取CoroutineScope.launch函数的返回值，需要通过Deferred.await函数，await函数是一个挂起函数
 *  ，调用它将会挂起当前协程。
 *  20 深入理解协程的挂起、恢复和调度 https://johnnyshieh.me/posts/kotlin-coroutine-deep-diving/
 *  20.1 挂起函数的工作原理
 *      协程的内部实现使用了Kotlin编译期的编译技术：当挂起函数或挂起Lambda表达式被调用的时候，都
 *  有一个隐式的参数传入，这个参数是Continuation类型，封装了协程恢复后执行的代码逻辑。
 *      协程内部实现使用状态机来处理不同的挂起点，是CPS（Continuation Passing Style）风格。
 *  每一个挂起点都会和初始挂起点的Continuation转化为一种状态，协程恢复只是跳转到下一个状态。挂起
 *  函数将执行过程分为多个Continuation片段，并且利用状态机的方式来保证各个片段是顺序执行的。
 *  20.2 挂起函数可能会挂起协程
 *      挂起函数不一定会挂起协程，当相关调用的结果已经可用，库可以决定协程继续执行而不是挂起。
 *  20.3 挂起函数不会阻塞线程
 *  20.4 挂起函数恢复协程后，协程运行在哪个线程。
 *      协程运行在哪个线程上有协程的CoroutineDispatcher控制。CoroutineDispatcher可以指定协
 *  程运行在某一特定线程上、运作在线程池中或者不指定运行的线程。协程调度可以分为confined dispatcher
 *  和unconfined dispatcher。Dispacher.Default、Dispacher.IO、Dispatcher.Main属于confined
 *  Dispatcher。都指定了协程运行的线程或线程池。挂起函数执行完成后，协程得以恢复，携程继续运行在
 *  指定的线程或线程池上的。而Dispather.Undefined，协程运行时在Caller Thread上的，但是只是在第一
 *  个挂起点之前是这样的，挂起恢复后运行在哪个线程完全由调用的挂起函数决定的。
 *  20.5 协程的创建与启动
 *  未完待续....
 *
 *
 *
 *
 */

fun main() {
//    launchFunctionStudy0()

//    launchFunctionStudy01()
    withContextFunctionStudy0()
//    suspendFuctionSuspendResumeStudy()
//    suspendCoroutineNotBlockThreadStudy()
//    coroutineRunThreadWhenResume()
}

/**
 * 协程恢复后，运行在哪个线程的示例
 */
private fun coroutineRunThreadWhenResume() {
    runBlocking {
        launch {
            println("main runBlocking, i am working in thread ${Thread.currentThread().name}")
            delay(300L)
            println("main runBlocking, after delay in thread ${Thread.currentThread().name}")
        }

        launch(Dispatchers.Unconfined) {
            println("unconfined, i am working in thread ${Thread.currentThread().name}")
            delay(300L)
            println("unconfined, after delay in thread ${Thread.currentThread().name}")
        }
    }
    println(" runBlocking 创建协程")
}

/**
 * 挂起函数会挂起协程，不会阻塞线程示例
 */
private fun suspendCoroutineNotBlockThreadStudy() {
    val job = GlobalScope.launch {
        println("launch start")
        delay(5L)
        println("launch end")
    }

    while (!job.isCompleted) {
        println("main thread working time:${System.currentTimeMillis()}")
    }
    println("main thread end")
}

private fun suspendFuctionSuspendResumeStudy() {
    runBlocking {
        //await是挂起函数，当前协程的执行逻辑卡在第一个分支，第一种状态，当async的协程执行完成后
        //才会恢复当前runBlocking协程，才会切换到下一个分支
        val resultThree = async { doSomethingUsefulThree() }.await()
        // 继续挂起runBlocking协程，执行aync协程这个状态的代码，将该分支之后的代码作为下一个状态的Continuation，
        //直至async协程代码执行完成。
        val resultFour = async { doSomethingUsefulFour() }.await()
        //继续执行runBlocking协程的代码。
        println("最后的结果为:${resultThree + resultFour}")
    }
}

suspend fun doSomethingUsefulThree(): Int {
    delay(1000L)
    return 13
}

suspend fun doSomethingUsefulFour(): Int {
    delay(1000L)
    return 29
}

/**
 * 创建一个协程，不阻塞主线程。
 */
private fun launchFunctionStudy01() {
    var time = System.currentTimeMillis()
    val startTime = time
    println("main start time: $time")
    val job = GlobalScope.launch {
        println("launch start")
        delay(10L)
        println("launch end")
    }
    while (!job.isCompleted) {
        time += System.currentTimeMillis()
        if ((time - startTime) > 10L) {
            println("main going time: $time")
        }
    }
    time += System.currentTimeMillis()
    println("main end time: $time")
}

private fun withContextFunctionStudy0() {
    //创建一个协程，运行其中的代码，并阻塞主线程代码运行，直至其中的代码运行完成。
    runBlocking {
        var time = System.currentTimeMillis()
        //调用withContext函数，并运行挂起代码块，直至其中的代码块运行完成。
        println("runBlocking start 当前的时间time:$time")
        withContext(coroutineContext) {
            time += System.currentTimeMillis()
            println("withContext start 当前的时间time:$time")
            delay(1000L)
            time += System.currentTimeMillis()
            println("withContext end 当前的时间time:$time")
        }
        time += System.currentTimeMillis()
        println("runBlocking end 当前的时间time:$time")
        println("运行携程代码结束")
    }
}

private fun launchFunctionStudy0() {
    GlobalScope.launch {
        println("构建并启动了一个协程, context:$coroutineContext, thread name is:${Thread.currentThread().name}")
    }

    GlobalScope.launch(Dispatchers.IO) {
        println("Dispatchers.io context:$coroutineContext, thread name is:${Thread.currentThread().name}")
    }
    Thread.sleep(1_000)
    println("main, end~")
}