package com.kotlin.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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
 *  18.6 协程上下文与调度器
 *  18.6.1 子协程
 *      在一个协程中创建并启动一个新协程，新协程将继承父协程的上下文，而且子协程的任务Job会成为父协
 *  程Job的子Job。当父协程的Job被取消后，它的子协程的Job也会被取消。
 *      但是通过GlobalScope.launch创建的协程，该协程没有父协程，该协程与启动它的协程无关。
 *      父协程总是等待所有的子协程执行结束。
 *      有时候我们需要在协程的上下文中定义多个元素，可以使用+操作符来实现。
 *  18.7 异步流
 *      流采用与协程相同的协作取消。
 *      流的构建器  flow、flowOf、List.asFlow等。
 *      流与序列的一个重要区别是流的操作符（map、filter）中可以调用挂起函数。
 *      transform操作符；使用transform操作符，可以在其代码块儿中发射任意值，发射任意次。
 *      take操作符；在流触及响应的限制的时候，流会被取消。协程中的取消操作总是通过抛出异常来实现。会
 *  抛出异常：kotlinx.coroutines.flow.internal.AbortFlowException: Flow was aborted, no more elements needed
 *      conflate操作符；当发射和收集的处理都很慢的时候，合并是加快处理速度的一种方式。
 *      collectLatest
 *      流完成后的处理可以在命令式的finally代码块中实现，也可以在声明式的onCompletion的操作符函数
 *  中实现。
 *
 */
fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")
val threadLocal = ThreadLocal<String?>()
fun getFlow0(): Flow<Int> = flow {
    println("flow started!")
    for (i in 1..3) {
        delay(100)
        println("emit $i")
        emit(i)
    }
}

suspend fun performRequest(request: Int): String {
    delay(1000L)
    return "response $request"
}

fun numbers(): Flow<Int> = flow {
    try {
        emit(1)
        emit(2)
        emit(3)
    } catch (e: Exception) {
        println("e: $e")
    } finally {
        println("finally in numbers")
    }
}

fun getFlow1(): Flow<Int> = flow {
    withContext(Dispatchers.Default) {
        for (i in 1..3) {
            Thread.sleep(100L)
            emit(i)
        }
    }
}

fun getFlow2(): Flow<Int> = flow {
    for (i in 1..3) {
        Thread.sleep(100L)
        emit(i)
    }
}.flowOn(Dispatchers.Default)

fun getFlow3(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(100L)
        emit(i)
    }
}

fun request(i: Int): Flow<String> = flow {
    emit("$i:first")
    delay(500)
    emit("$i:second")
}

fun getFlow4(): Flow<String> = flow {
    for (i in 1..3) {
        println("emit:$i")
        emit(i)
    }
}.map {
    check(it <= 1) { "crashed on $it" }
    "strin: it"
}

fun getFlow5(): Flow<Int> = flow {
    for (i in 1..5) {
        println("emit $i")
        emit(i)
    }
}

fun main() {
//    runBlockingFunctionStudy0()
//    childCoroutineStudy0()
//    threadLocalData()
//    flowStudy0()
//    flowStudy02()
//    flowStudy03Cancel()
//    flowStudy04OperatorMap()
//    flowStudy04TransformOperator()
//    flowStudy05TakeOperator()
//    flowStudy06Reduce()
//    flowStudy07Reduce()
//    flowStudy08Filter()
//    flowStudy09WithContext()
//    flowStudy10FlowOn()
//    flwStudy11NotBuffer()
//    flowStudy12Buffer()
//    flowStudy13Conflate()
//    flowStudy14CollectLatest()
//    flowStudy15Zip()
//    flowStudy16FlatMapConcat()
//    flowStudy17FlatMapMerge()
//    flowStudy18FlatMapLatest()
//    flowStudy19Catch()
//    flowStudy20Catch()
//    flowStudy21Catch()
//    flowStudy22OnCompletion()
//    flowStudy23OnCompletion()
//    flowStudy24LaunchIn()
//    flowStudy25Cancel()
    flowStudy26Cancellable()

}

/**
 * cacellable操作符可以声明流的发射器中的操作是可以取消的。
 */
private fun flowStudy26Cancellable() {
    runBlocking {
        (1..5).asFlow()
            .cancellable()
            .collect {
                if (it == 3) cancel()
                println("collect $it")
            }
    }
}

/**
 * flow发出的繁忙循环接收之后才收到取消的异常堆栈，这意味着繁忙循环没有得到取消。
 */
private fun flowStudy25Cancel() {
    runBlocking {
        (1..5).asFlow()
            .onCompletion { it?.let { println("caught $it") } }
            .collect {
                if (it == 2) cancel()
                println("collect $it")
            }
    }
}

/**
 * 末端操作符launchIn可以在单独的协程中启动流的收集。runBlocking作用域等待它的子协程执行完后，在
 * 执行主线程的代码。
 */
private fun flowStudy24LaunchIn() {
    runBlocking {
        getFlow5().onEach {
            println("collect $it")
        }
            .onCompletion { println("done") }
            .launchIn(this)
        println("main")
    }
}

/**
 * onCompletion 函数中的函数类型参数 Lambda表达式中的可空参数cause可以判断流的完成是否是因为异常
 * 引起 如果cause不为空，则说明是异常导致流的完成。
 * onCompletion操作符与catch操作符不同，它不处理异常。当异常放生的时候，异常仍然会流向下游，传递给
 * onCompletion函数，并向下传递给catch函数，在catch函数中处理异常。
 */
private fun flowStudy23OnCompletion() {
    runBlocking {
        getFlow5()
            .onEach {
                check(it <= 1) { println("collect check $it") }
                println("collect $it")
            }.onCompletion { it?.let { println("flow completed exceptionally.") } }
            .catch { println("caught $it") }
            .collect()
    }
}

private fun flowStudy22OnCompletion() {
    runBlocking {
        getFlow5()
            .onCompletion { println("done") }
            .collect { println("collect $it") }
    }
}

/**
 * 由于catch操作符只能捕获上游异常，如果下游的代码可能会有异常，则我们可将末端操作符collect代码块
 * 中的代码逻辑放在上游的onEach操作符中实现，并调用无参的collect函数。
 */
private fun flowStudy21Catch() {
    runBlocking {
        getFlow5()
            .onEach {
                check(it <= 1) { println("collect check $it") }
                println("collect $it")
            }.catch { println("caught $it") }
            .collect()
    }
}

private fun flowStudy20Catch() {
    runBlocking {
        getFlow5().catch { println("caught $it") }
            .collect {
                check(it <= 1) { println("collect check $it") }
                println("collect $it")
            }
    }
}

/**
 * catch操作符遵循异常透明性，仅捕获上游异常。
 */
private fun flowStudy19Catch() {
    runBlocking {
        getFlow4().catch { emit("caught:$it") }
            .collect {
                println("collect $it")
            }
    }
}

private fun flowStudy18FlatMapLatest() {
    runBlocking {
        val startTime = System.currentTimeMillis()
        (1..3).asFlow().onEach { delay(100) }
            .flatMapLatest { request(it) }
            .collect {
                println("$it ${System.currentTimeMillis() - startTime} ms from start.")
            }
    }
}

private fun flowStudy17FlatMapMerge() {
    runBlocking {
        val startTime = System.currentTimeMillis()
        (1..3).asFlow().onEach { delay(100) }
            .flatMapMerge { request(it) }
            .collect {
                println("$it ${System.currentTimeMillis() - startTime} ms from start.")
            }
    }
}

/**
 * flatMapConcat操作符
 */
private fun flowStudy16FlatMapConcat() {
    runBlocking {
        val startTime = System.currentTimeMillis()
        (1..3).asFlow().onEach { delay(100) }
            .flatMapConcat { request(it) }
            .collect {
                println("$it ${System.currentTimeMillis() - startTime} ms from start")
            }
    }
}

private fun flowStudy15Zip() {
    runBlocking {
        val numbsers = (1..3).asFlow()
        val strs = flowOf("one", "two", "three")
        numbsers.zip(strs) { a, b ->
            "$a -> $b"
        }.collect { println("collect $it") }
    }
}

private fun flowStudy14CollectLatest() {
    runBlocking {
        val time = measureTimeMillis {
            getFlow3().collectLatest {
                println("collect $it")
                delay(300L)
                println("done $it")
            }
        }
        println("colleted in $time ms")
    }
}

/**
 * 虽然第2个元素仍在处理中，但是第一个和第三个已经产生，第二个是conflated，只有最新的第三个被交付给
 * 了收集器。
 */
private fun flowStudy13Conflate() {
    runBlocking {
        val time = measureTimeMillis {
            getFlow3().conflate().collect {
                delay(300L)
                println("collect:$it")
            }
        }
        println("collected in $time ms")
    }
}

/**
 * 相对不使用Flow的buffer操作符，使用buffer操作符处理流花费的时间更少了。仅仅需要花费等待第一个元素
 * 发射等待的时间和处理每一个元素等待的时间之和。
 */
private fun flowStudy12Buffer() {
    runBlocking {
        val time = measureTimeMillis {
            getFlow3().buffer().collect {
                delay(300L)
                println("collect:$it")
            }
        }
        println("buffer collected in $time ms")
    }
}

private fun flwStudy11NotBuffer() {
    runBlocking {
        val time = measureTimeMillis {
            getFlow3().collect {
                delay(300)
                println("collect:$it")
            }
        }
        println("collected in $time ms")
    }
}

/**
 * flowOn操作符改变流发射的上下文。流的搜集发生在主线程的某一个协程中，flowOn操作符会创建另一个协
 * 程，该协程在后台线程中，两个协程的代码同步执行，
 */
private fun flowStudy10FlowOn() {
    runBlocking {
        getFlow2().collect { println("collect:$it") }
    }
}

/**
 * withContext在协程中改变上下文，flow构建器中的代码遵循上下文保存属性，不允许从其他上下文中发射值
 * 会出现异常。
 * Exception in thread "main" java.lang.IllegalStateException: Flow invariant is violated:
Flow was collected in [BlockingCoroutine{Active}@3baaf4d9, BlockingEventLoop@5e04eedf],
but emission happened in [DispatchedCoroutine{Active}@76fe6e5e, Dispatchers.Default].
Please refer to 'flow' documentation or use 'flowOn' instead
 *
 *
 */
private fun flowStudy09WithContext() {
    runBlocking {
        getFlow1().collect { println("collect:$it") }
    }
}

private fun flowStudy08Filter() {
    runBlocking {
        (1..5).asFlow()
            .filter {
                println("filter:$it")
                it % 2 == 0
            }
            .map {
                println("map:$it")
                "string:$it"
            }
            .collect { println("collect:$it") }
    }
}

private fun flowStudy07Reduce() {
    runBlocking {
        val result = (1..5).asFlow()
            .reduce { a, b -> a * b }
        println(result)
    }
}

/**
 * 求平方和
 */
private fun flowStudy06Reduce() {
    runBlocking {
        val sum = (1..5).asFlow()
            .map { it * it }
            .reduce { a, b -> a + b }
        println(sum)
    }
}

private fun flowStudy05TakeOperator() {
    runBlocking {
        numbers()
            .take(2)
            .collect { value -> println(value) }
    }
}

private fun flowStudy04TransformOperator() {
    runBlocking {
        (1..3).asFlow()
            .transform { value ->
                emit("make request $value")
                emit(performRequest(value))
            }
            .collect { value -> println(value) }
    }
}

private fun flowStudy04OperatorMap() {
    runBlocking {
        (1..3).asFlow()
            .map { value -> performRequest(value) }
            .collect { value -> println(value) }
    }
}

private fun flowStudy03Cancel() {
    runBlocking {
        withTimeoutOrNull(250L) {
            getFlow0().collect { value -> println(value) }
        }
        println("Done")
    }
}

/**
 * flow构建器的代码，直到流被收集的时候才会执行。流在每次收集的时候才会启动，
 */
private fun flowStudy02() {
    runBlocking {
        println("Call getFlow0 function...")
        val flow = getFlow0()
        println("call collect...")
        flow.collect { value -> println(value) }
        println("call collect again...")
        flow.collect { value -> println(value) }
    }
}

private fun flowStudy0() {
    runBlocking {
        launch {
            for (k in 1..3) {
                println("i am not blocked $k")
                delay(100)
            }
        }
        getFlow0().collect { value -> println(value) }
    }
}

/**
 * 线程的局部数据在协程中的使用。
 */
private fun threadLocalData() {
    runBlocking {
        threadLocal.set("main")
        println(
            "pre-main:current thread: ${Thread.currentThread().name}" +
                    " threadLocal value:${threadLocal.get()}"
        )
        val job = launch(Dispatchers.Default + threadLocal.asContextElement(value = "launch")) {
            println(
                "launch start, current thread: ${Thread.currentThread().name}" +
                        " threadLocal value: ${threadLocal.get()}"
            )
            yield()
            println(
                "after yield: current thread:${Thread.currentThread().name}" +
                        " threadLocal value: ${threadLocal.get()}"
            )
        }
        job.join()
        println(
            "post-main: current thread:${Thread.currentThread().name}" +
                    " threadLocal value: ${threadLocal.get()}"
        )
    }
}

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


private fun childCoroutineStudy0() {
    runBlocking {
        val request = launch(Dispatchers.Default + CoroutineName("request")) {
            GlobalScope.launch {
                log("job1: i run in GlobalScope and execute independently!")
                delay(1000L)
                log("job1: i am not affected by cancellation by the request!")
            }

            launch {
                delay(100L)
                log("job2: i am a child of the request coroutine!")
                delay(1000L)
                log("job2: i will not execute this line if my parent request is cancelled.")
            }
        }
        delay(500L)
        request.cancel()
        delay(1000)
        log("main: who has survived request cancellation?")
    }
}

private fun runBlockingFunctionStudy0() {
    /**
     *  runBlocking函数，开启一个主协程，并在主携程内开启一个子协程，子携程的任务会阻塞主线程，当子
     *  协程的任务执行完后，才会继续执行主线程的代码。
     */
    runBlocking {
        launch {
            println(
                "delay before, the time is ${System.currentTimeMillis()} and workking" +
                        " in thread ${Thread.currentThread().name}"
            )
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
        println("complete in $time ms.")
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