package com.kotlin.coroutine

import kotlinx.coroutines.*
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

/**
 *  18 协程
 *      当我们使用GlobalScope.launch的时候，会创建一个顶层协程。如果我们忘记对这个协程的引用，它对内存资源
 *  的占用会得不到释放。https://www.kotlincn.net/docs/reference/coroutines/basics.html。一个更好的解
 *  决办法是在执行操作的指定作用域中启动协程。
 *  18.1 作用域构建器
 *      除了由不同的协程构建器提供协程作用域外，还可以使用coroutineScope构建器声明自己的作用域，它会创建一个
 *  协程作用域，并在其他已启动的协程执行完毕之前不会结束。
 *
 *      协程较线程比较轻量。全局协程像守护线程。
 *  18.2 取消和超时
 *      lauch函数返回了一个可以取消运行中的协程的Job。
 *      一段协程代码只有是协做的才能被取消。但是协程做的是计算任务，并且没有检查取消的话，它是不能被取消的。
 *      isActive是CoroutineScope的一个扩展属性，在协程中恰当使用该属性可以使得该协程是协作的，是可取消的。
 *      在finally中释放资源。通常使用try finally代码块处理协程被取消的时候，可被取消的挂起函数抛出的
 *      CancellationException。
 *  18.2.1 运行不能被取消的代码块
 *      当在finally代码块中调用挂起函数都会抛出CancellationException，调用挂起函数视图挂起被取消的协程
 *  ，这时候可以使用withContext函数以及NonCancellable上下文。
 *      CancellationExceptin被认为是协程执行结束的正常原因，所以不会在控制台打印该exception。
 *      但是在使用withTimeout函数的时候如果协程超时运行则会抛出
 *      kotlinx.coroutines.TimeoutCancellationException。
 *      如果避免抛出上述异常，可以使用withTimeoutOrNull函数，并将会超时执行的代码放在try代码块中。
 *      withTimeoutOnNull返回的数据为null的时候代表超时，
 *  18.3 组合挂起函数
 *  18.3.1 async并发
 *     使用async会启动一个协程，它会返回一个Deferred，可以使用await函数在延期的值上得到结果
 *  18.4 协程上下文与调度器
 *      协程总是运行在以CoroutineContext类型为代表的上下文中。协程上下文是各种不同元素的集合，其中主元素是
 *  Job。
 *      协程调度器CoroutineDispatcher确定了协程在哪个线程或哪些线程上执行。协程调度器可以将协程限定在一个
 *  线程上执行，或者分派到一个线程池，或者让它不受限制地执行。
 *
 *  18.5 携程的相关函数
 *  18.5.1 runBlocking 函数https://blog.csdn.net/u011133887/article/details/98617852
 *      被suspend修饰符修饰的函数就是挂起函数，它不阻塞线程，但是会挂起协程。并且只能在协程中使用，我们
 *  可以在runBlocking函数中使用挂起函数。
 *      我们可以使用runBlocking函数构建一个主协程，从而调试我们的协程代码。可以在协程中调用launch函数
 *  构建一个子携程，用来运行后台阻塞任务。
 *      每个coroutine builder都是CoroutineScope的扩展函数。
 *      runBlocking是桥接阻塞代码和挂起代码之间的桥梁。可在内部运行挂起函数，在其内部的所有子协程任务
 *  执行完之前是阻塞线程的。
 *      coroutineScope是个挂起函数。未完待续......
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

//fun main() = runBlocking {
//    launch {
//        suspendFuctionStudy()
//    }
//
//    coroutineScope {
//        launch {
//            delay(500L)
//            println("Task from nested launch")
//        }
//
//        delay(100L)
//        println("Task from coroutin scope!")
//    }
//
//    println("Coroutine scope is over!")
//}

//fun main() = runBlocking {
//    GlobalScope.launch {
//        repeat(1000) {
//            println("I am sleeping $it")
//            delay(500L)
//        }
//    }
//
//    delay(1300L)
//}

//fun main() = runBlocking {
//    val job = launch {
//        repeat(1000) {
//            println("job i am sleeping $it")
//            delay(500L)
//        }
//    }
//
//    delay(1300L)
//    println("i am tired of waiting!")
//    //取消正在活动中的协程
//    job.cancel()
//    job.join()
//    println("now i am quit!")
//}

//fun main() = runBlocking {
//    val startTime = System.currentTimeMillis()
//    val job = launch(Dispatchers.Default) {
//        var nextPrintTime = startTime
//        var i = 0
//        while (i < 15) {
//            if (System.currentTimeMillis() >= nextPrintTime) {
//                println("job, i am sleeping ${i++}")
//                nextPrintTime += 500L
//            }
//        }
//    }
//
//    println("main thread will be blocked")
//    delay(1300L)
//    println("i am tired of waiting!")
//    job.cancelAndJoin()
//    // 一段协程代码只有是协做的才能被取消。但是协程做的是计算任务，并且没有检查取消的话，它是不能被取消的。
//    println("now i am quit!")
//}

//fun main() = runBlocking {
//    val startTime = System.currentTimeMillis()
//    val job = launch(Dispatchers.Default) {
//        var nextPrintTime = startTime
//        var i = 0
//        while (isActive) {
//            if (System.currentTimeMillis() >= nextPrintTime) {
//                println("job, i am sleeping ${i++}")
//                nextPrintTime += 500L
//            }
//        }
//    }
//
//    println("main thread will to be blocked.")
//    delay(1300L)
//    println("main , i am tired of waiting.")
//    job.cancelAndJoin()
//    println("main, now i am out")
//}

//fun main() = runBlocking {
//    val job = launch(Dispatchers.Default) {
//        try {
//            repeat(1000) {
//                println("job, i am sleeping $it")
//                delay(500L)
//            }
//        } catch (e: Exception) {
//            println("something is wrong e:" + e)
//        } finally {
//            withContext(NonCancellable) {
//                println(" finally!")
//                delay(1000L)
//                println("job, i have just delayed for 1 second because i am not cancellable!")
//            }
//        }
//    }
//
//    delay(2000L)
//    println("main, i am tired of waiting.")
//    job.cancelAndJoin()
//    println("main, now i am out.")
//}

//fun main() = runBlocking {
//    withTimeout(1300L) {
//        repeat(1000) {
//            println(" i am sleeping $it")
//            delay(500L)
//        }
//    }
//}

//fun main() = runBlocking<Unit> {
////    defaultOrderExecute()
////    asyncFunctionStudy()
//    val time = measureTimeMillis {
//        val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
//        val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
//        one.start()
//        two.start()
//        println("the answer is ${one.await() + two.await()}")
//    }
//    println("complete in $time ms.")
//}

//fun main() = runBlocking<Unit> {
//    launch {
//        println("i am woring in thread:${Thread.currentThread().name}")
//    }
//
//
//}
fun main() {
    /**
     *  runBlocking函数，开启一个主协程，并在主携程内开启一个子协程，子携程的任务会阻塞主线程，当子
     *  协程的任务执行完后，才会继续执行主线程的代码。
     */
    runBlocking {
        launch {
            println("delay before, the time is ${System.currentTimeMillis()} and workking" +
                    " in thread ${Thread.currentThread().name}")
            delay(3000L)
            println("World")
            println("delay after, the time is ${System.currentTimeMillis()}")
        }
        println("runBlocking working in thread ${Thread.currentThread().name}")
    }
    println("Hello")
    println("main the time is ${System.currentTimeMillis()}")
}
private suspend fun asyncFunctionStudy() {
    coroutineScope {
        val time = measureTimeMillis {
            val one = async { doSomethingUsefulOne() }
            val two = async { doSomethingUsefulTwo() }
            println("the answer is ${one.await() + two.await()}")
        }
        println ("complete in $time ms.")
    }
}

private suspend fun defaultOrderExecute() {
    val time = measureTimeMillis {
        val one = doSomethingUsefulOne()
        val two = doSomethingUsefulTwo()
        println("the answer is ${one + two}")
    }
    println("complete in $time ms.")
}

//fun main() = runBlocking {
//    val result = withTimeoutOrNull(1300L) {
//        repeat(1000) {
//            println("i am sleeping $it")
//            delay(500L)
//        }
//
//        "Done"
//    }
//
//    println("the result is $result")
//}

suspend fun doSomethingUsefulOne(): Int {
    delay(1000L)
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L)
    return 29
}

/**
 *  挂起函数
 */
private suspend fun suspendFuctionStudy() {
    delay(200L)
    println("Task from runBlocing!")
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