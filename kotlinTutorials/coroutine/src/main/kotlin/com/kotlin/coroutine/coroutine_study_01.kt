package com.kotlin.coroutine

import kotlinx.coroutines.*

fun main() {
//    launchFunctionStudy0()

//    launchFunctionStudy01()
//    withContextFunctionStudy0()
//    suspendFuctionSuspendResumeStudy()
//    suspendCoroutineNotBlockThreadStudy()
//    coroutineRunThreadWhenResume()

//    suspendFunctionStudy()

    yieldStudy()
}

@OptIn(DelicateCoroutinesApi::class)
private fun yieldStudy() {
    GlobalScope.launch {
        launch {
            repeat(3) {
                println("job1 repeat $it times")
                yield()
            }
        }
        launch {
            repeat(100) {
                println("job2 repeat $it times")

            }
        }
    }
}


/**
 *  挂起函数可能会挂起协程，但是不会阻塞线程。
 */
private fun suspendFunctionStudy() {
    val coroutineDispatcher = newSingleThreadContext("ctx")
    GlobalScope.launch(coroutineDispatcher) {
        println("the first coroutine")
        delay(200)
        println("the first coroutine")
    }
    GlobalScope.launch(coroutineDispatcher) {
        println("the second coroutine")
        delay(100)
        println("the second coroutine")
    }
    // 保证 com.kotlin.coroutine.practise.main 线程存活，确保上面两个协程运行完成
    Thread.sleep(500)
}

/**
 * 协程恢复后，运行在哪个线程的示例
 */
private fun coroutineRunThreadWhenResume() {
    runBlocking {
        launch {
            println("com.kotlin.coroutine.practise.main runBlocking, i am working in thread ${Thread.currentThread().name}")
            delay(300L)
            println("com.kotlin.coroutine.practise.main runBlocking, after delay in thread ${Thread.currentThread().name}")
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
        println("com.kotlin.coroutine.practise.main thread working time:${System.currentTimeMillis()}")
    }
    println("com.kotlin.coroutine.practise.main thread end")
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
    println("com.kotlin.coroutine.practise.main start time: $time")
    val job = GlobalScope.launch {
        println("launch start")
        delay(10L)
        println("launch end")
    }
    while (!job.isCompleted) {
        time += System.currentTimeMillis()
        if ((time - startTime) > 10L) {
            println("com.kotlin.coroutine.practise.main going time: $time")
        }
    }
    time += System.currentTimeMillis()
    println("com.kotlin.coroutine.practise.main end time: $time")
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
    println("com.kotlin.coroutine.practise.main, end~")
}