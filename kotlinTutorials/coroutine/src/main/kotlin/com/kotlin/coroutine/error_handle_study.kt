package com.kotlin.coroutine

import kotlinx.coroutines.*
import java.lang.ArithmeticException
import java.lang.RuntimeException

fun main() {
//    coroutineStudy01Exception()
//    coroutineExceptionHandler12()

//    globalScopeException()
//    coroutineScopeException()
//    supervisorScopeException()

    // launch启动的协程中出现的异常，处理这样的异常
//    launch_coroutine_exception()
//    launch_coroutine_exception_propagate_root_coroutine_01()
//      launch_coroutine_exception_propagate_root_coroutine_02()
//      launch_coroutine_exception_propagate_root_coroutine_03()
//      supervisor_job_launch_coroutine_exception_not_propagate_root_coroutine_04()

    // async启动的协程中出现的异常，处理这样的异常
//    async_exception_deferred_01()
//    async_exception_deferred_02()
//    async_exception_deferred_05()
//    async_exception_deferred_06()
//    async_exception_deferred_03()
//    async_exception_deferred_04()
//    async_exception_deferred_07()
//    async_exception_deferred_08()
//    async_exception_deferred_09()
    async_exception_deferred_10()

//    lauch_coroutine_exception_in_coroutine_scope_01()
//    lauch_coroutine_exception_in_coroutine_scope_02()
//    coroutineStudy11ExceptionSupervisorScope()
//    launch_coroutine_exception_in_supervisor_scope_01()
//    launch_coroutine_exception_in_supervisor_scope_02()

//    test()
}

fun test()= runBlocking {
    val handler = CoroutineExceptionHandler { coroutineContext, exception ->
        println("CoroutineExceptionHandler got $exception  coroutineContext $coroutineContext")
    }
    val job = GlobalScope.launch(handler) {
        println("1")
        delay(1000)
        coroutineScope {
            println("2")
            val job2 = launch(handler) {
                println("throw error test.")
                throwErrorTest()
            }
            println("3")
            job2.join()
            println("4")
        }
    }
    job.join()
}
fun throwErrorTest(){
    throw Exception("error test")
}



/**
 *  c2和c3都是协程c1的子协程，c2和c3协程中出现的异常不会影响到c1，且c2和c3互相不影响。
 */
private fun supervisorScopeException() {
    GlobalScope.launch {//协程c1
        supervisorScope {
            launch {//协程c2
                println("协程c2")
                throw RuntimeException()
            }
            launch {//协程c3
                println("协程c3")
            }
        }
        delay(100)
        println("协程c1")
    }
    Thread.sleep(1000)
    println("main thread end.")
}

/**
 *  c2、c3是c1的子协程，c2或c3协程中出现的异常会导致c1取消。
 */
private fun coroutineScopeException() {
    GlobalScope.launch { // 协程c1
        coroutineScope {
            launch { // 协程c2
                println("协程c2")
                throw RuntimeException()
            }
            launch { // 协程c3
                println("协程c3")
            }
        }
        delay(100)
        println("协程c1")
    }
    Thread.sleep(1000)
    println("main thread end.")
}

/**
 *  c1和c2相互独立。
 */
private fun globalScopeException() {
    GlobalScope.launch {//协程 c1
        GlobalScope.launch { // 协程c2
            println("协程 c2")
            throw RuntimeException()
        }
        delay(100)
        println("协程 c1")
    }
    Thread.sleep(1000L)
    println("main thread end.")
}

private fun launch_coroutine_exception_in_supervisor_scope_02() {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("handle $throwable in handler.")
    }

    val topLevelScope = CoroutineScope(Job())
    topLevelScope.launch {
        val job1 = launch {
            println("starting coroutine 1.")
        }

        supervisorScope {
            val job2 = launch(exceptionHandler) {
                println("starting coroutine 2.")
                throw RuntimeException("RuntimeException in coroutine 2.")
            }

            val job3 = launch {
                println("starting coroutine 3.")
            }
        }

        delay(100)
        println("parent job after delay 100")
    }

    Thread.sleep(300L)
    println("main thread end.")
}

/**
 * supervisorScope函数包裹的由launch创建的协程是顶级协程，该协程中发生的异常，可以在
 * CoroutineExceptinHandler中被捕获
 */
private fun launch_coroutine_exception_in_supervisor_scope_01() {
    val topLevelScope = CoroutineScope(Job())
    topLevelScope.launch {
        val job1 = launch {
            println("starting coroutin 1.")
        }

        supervisorScope {
            val job2 = launch {
                println("starting coroutine 2.")
                throw RuntimeException("RuntimeException in coroutine 2.")
            }

            val job3 = launch {
                println("starting coroutine 3.")
            }
        }

        delay(100)
        println("parent job after delay 100")
    }

    Thread.sleep(300L)
    println("main thread end.")
}

/**
 * supervisorScope是一个必须独立处理异常的独立作用域。它不会像coroutineScope函数那样重新抛出异
 * 常，也不会将异常传播该父级Job中。异常只会向上传播并到达顶级Job或SupervisorJob。这意味着本例中
 * job2和job3是顶级作业对应的协程是顶级的。
 */
private fun coroutineStudy11ExceptionSupervisorScope() {
    val topLevleScope = CoroutineScope(Job())
    topLevleScope.launch {
        val job1 = launch {
            println("starting coroutine 1.")
        }

        supervisorScope {
            val job2 = launch {
                println("starting coroutine 2.")
            }

            val job3 = launch {
                println("starting coroutine 3.")
            }
        }
    }

    Thread.sleep(100L)
}

private fun lauch_coroutine_exception_in_coroutine_scope_02() {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("exceptionHandler throwable:$throwable")
    }
    val topLevelScope = CoroutineScope(SupervisorJob() + exceptionHandler)
    topLevelScope.launch {
        coroutineScope {
            launch {
                throw RuntimeException("RuntimeException in child launch coroutine.")
            }
        }
        delay(100)
        println("parent job after delay 100")

    }
    Thread.sleep(300L)
}

/**
 * 有launch构建的子协程，被coroutineScope函数所包裹，并且在子协程中发生了异常，这样异常不会传播
 * 到Job层次结构中，如果只对该子协程使用try-catch语句是可以捕获到异常的。
 * coroutineScope函数主要用在suspend函数中已实现“并行分解”，这些suspend函数将重新抛出其失败协程
 * 的异常，我们可以使用try-catch捕获异常。
 */
private fun lauch_coroutine_exception_in_coroutine_scope_01() {
    val topLevelScope = CoroutineScope(SupervisorJob())
    topLevelScope.launch {
        try {
            coroutineScope {
                launch {
                    throw RuntimeException("RuntimeExceptin in child launch coroutine.")
                }
            }
        } catch (e: Exception) {
            println("try-catch e:$e")
        }
        delay(100)
        println("parent job after delay 100")

    }
    Thread.sleep(300L)
}

private fun async_exception_deferred_10() {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("exceptionHandler throwable:$throwable")
    }
    val topLevelScope = CoroutineScope(SupervisorJob())
    val deferred = topLevelScope.async {
        launch {
            println("launch")
            throw RuntimeException("RuntimeException in async coroutine.")
        }
        delay(5)
        println("after delay 5.")
    }

    topLevelScope.launch {
        deferred.await()
    }

    Thread.sleep(100L)
    println("main thread end.")
}

private fun async_exception_deferred_09() {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("exceptionHandler throwable:$throwable")
    }
    val topLevelScope = CoroutineScope(SupervisorJob() + exceptionHandler)
    topLevelScope.async {
        val deferred = async {
            println("async")
            throw RuntimeException("RuntimeException in async coroutine.")
        }

        try {
            deferred.await()
        } catch (e: Exception) {
            println("try-catch e:$e")
        }
    }

    Thread.sleep(100L)
    println("main thread end.")
}

private fun async_exception_deferred_08() {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("exceptionHandler throwable:$throwable")
    }
    val topLevelScope = CoroutineScope(SupervisorJob() + exceptionHandler)
    topLevelScope.launch {
        val deferred = async {
            println("async")
            throw RuntimeException("RuntimeException in async coroutine.")
        }

        try {
            deferred.await()
        } catch (e: Exception) {
            println("try-catch e:$e")
        }
    }

    Thread.sleep(100L)
    println("main thread end.")
}

/**
 *  async构建的子协程中出现了异常，异常会传播到root协程中，通过try-catch代码块并不能阻止线程发生未捕获的异常，需要通过
 *  root协程的CoroutineExceptionHandler处理。
 */
private fun async_exception_deferred_07() {
    val topLevelScope = CoroutineScope(SupervisorJob())
    topLevelScope.launch {
        val deferred = async {
            println("async")
            throw RuntimeException("RuntimeException in async coroutine.")
        }

        try {
            deferred.await()
        } catch (e: Exception) {
            println("try-catch e:$e")
        }
    }

    Thread.sleep(100L)
    println("main thread end.")
}

/**
 *  async构建的子协程中发生了异常，该异常会委托给root协程处理，可以通过CoroutineExceptionHandler捕获。
 */
private fun async_exception_deferred_04() {
    val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        println("handle $throwable in handler")
    }
    val topLevelScope = CoroutineScope(SupervisorJob() + exceptionHandler)
    topLevelScope.launch {
        async {
            throw RuntimeException("RuntimeException in async coroutine.")
        }
    }
    Thread.sleep(100L)
}

/**
 *  通过async构建的子协程，出现的异常会委托给root协程处理。
 *  打印异常。
 */
private fun async_exception_deferred_03() {
    val topLevelScope = CoroutineScope(SupervisorJob())
    topLevelScope.launch {
        async {
            println("async")
            throw RuntimeException("RuntimeException in async coroutine.")
        }
    }

    Thread.sleep(100L)
}

private fun async_exception_deferred_06() {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("exceptionHandler throwable:$throwable")
    }
    val topLevelScope = CoroutineScope(SupervisorJob())
    val deferred = topLevelScope.async {
        println("async")
        throw RuntimeException("RuntimeException in async coroutine.")
    }

    topLevelScope.launch {
        try {
            deferred.await()
        } catch (e: Exception) {
            println("try-catch e:$e")
        }
    }

    Thread.sleep(100L)
}
private fun async_exception_deferred_05() {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("exceptionHandler throwable:$throwable")
    }
    val topLevelScope = CoroutineScope(SupervisorJob() + exceptionHandler)
    val deferred = topLevelScope.async {
        println("async")
        throw RuntimeException("RuntimeException in async coroutine.")
    }

    topLevelScope.launch {
        deferred.await()
    }

    Thread.sleep(100L)
}

/**
*  通过async构建的root协程，出现了异常，该协程将异常反映到返回值Deferred中。通过Deferred#await函数可以重新抛出异常
 * 会打印异常。需要使用try-catch代码块，包裹await函数调用来捕获异常。
*
*/
private fun async_exception_deferred_02() {
    val topLevelScope = CoroutineScope(SupervisorJob())
    val deferred = topLevelScope.async {
        println("async")
        throw RuntimeException("RuntimeException in async coroutine.")
    }

    topLevelScope.launch {
        deferred.await()
    }

    Thread.sleep(100L)
}

/**
 *  通过async构建的root协程，出现了异常，该协程会将异常反映到返回值Deferred中。
 *  不会打印异常。
 */
private fun async_exception_deferred_01() {
    val topLevelScope = CoroutineScope(SupervisorJob())
    topLevelScope.async {
        println("async")
        throw RuntimeException("runTimeException in async coroutine.")
    }
    Thread.sleep(100L)
}

private fun supervisor_job_launch_coroutine_exception_not_propagate_root_coroutine_04() {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("exceptionHandler throwable:$throwable")
    }
    val topLevelScope = CoroutineScope(Job() )
    topLevelScope.launch(exceptionHandler) {
        supervisorScope {
            launch {
                println("throw exception.")
                throw RuntimeException("runtimeException in nest coroutine.")
            }
        }
        delay(200)
        println("parent job")
    }
    Thread.sleep(1000L)
}

private fun launch_coroutine_exception_propagate_root_coroutine_03() {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("exceptionHandler throwable:$throwable")
    }
    val topLevelScope = CoroutineScope((Job() + exceptionHandler))
    topLevelScope.launch {
        launch {
            println("throw exception.")
            throw RuntimeException("runtimeException in nest coroutine.")
        }
    }
    Thread.sleep(100L)
}

/**
 *  CoroutineExceptionHandler需要设置到root协程的CoroutineContext中才能捕获到子协程中出现的异常。
 */
private fun launch_coroutine_exception_propagate_root_coroutine_02() {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("exceptionHandler throwable:$throwable")
    }
    val topLevelScope = CoroutineScope((Job()))
    topLevelScope.launch {
        launch(exceptionHandler) {
            println("throw exception.")
            throw RuntimeException("runtimeException in nest coroutine.")
        }
    }
    Thread.sleep(100L)
}

/**
 *  try-catch捕获不了子协程的异常。
 *  在普通Job中，通过launch构建的子协程，产生的异常会传播到root 协程，由root协程处理。如果root协程的上下文中没有
 *  CoroutineExceptionHandler，则会打印该异常。
 *
 */
private fun launch_coroutine_exception_propagate_root_coroutine_01() {
    val topLevelScope = CoroutineScope((Job()))
    topLevelScope.launch {
        try {
            launch {
                throw RuntimeException("runtimeException in nest coroutine.")
            }
        } catch (e: Exception) {
            println("handle $e")
        }
        delay(5)
        println("after delay 5 ms")
    }
    Thread.sleep(100L)
}

/**
 *  在普通Job中，通过launch启动的协程，通过try-catch代码块捕获异常。
 */
private fun launch_coroutine_exception() {
    val topLevelScope = CoroutineScope(Job())
    topLevelScope.launch {
        try {
            throw RuntimeException("runtimeException in coroutine!")
        } catch (e: Exception) {
            println("handle $e")
        }
        delay(5)
        println("after delay 5ms")
    }
    Thread.sleep(100L)
}

private fun coroutineExceptionHandler12() {
    runBlocking {
        // handler不会catch async构建的协程中出现的异常，会catch launch构建的协程出现的异常
        val handler = CoroutineExceptionHandler { context, throwable ->
            println("coroutine exception handler, throwable:$throwable")
        }
        val job = GlobalScope.launch(handler) {
            throw AssertionError()
        }
        val deferred = GlobalScope.async(handler) {
            throw ArithmeticException()
        }
        joinAll(job, deferred)
        println("runBlocking coroutine.")
    }
}

/**
 *  由launch构建的顶级协程中发生的异常，异常将由CoroutineExceptionHandler处理或传播给线程的未捕获的异常程序。
 */
private fun coroutineStudy01Exception() {
    runBlocking {
        val job = GlobalScope.launch {
            println("throw exception from launch")
            delay(100)
            throw IndexOutOfBoundsException()
        }
        //try 代码块未能捕获到异常。
        try {
            job.join()
        } catch (e: Exception) {
            println("launch join e:$e")
        }
        println("joined failed job")
        val deferred = GlobalScope.async {
            println("throws exception from async")
            throw ArithmeticException()
        }
        // try代码块捕获到了子协程中出现的异常。
        try {
            deferred.await()
        } catch (e: ArithmeticException) {
            println("caught ArithmeticException")
        }
        println("deferred wait")
    }
}