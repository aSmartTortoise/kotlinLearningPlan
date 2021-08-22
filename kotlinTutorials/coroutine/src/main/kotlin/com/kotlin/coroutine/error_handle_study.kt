package com.kotlin.coroutine

import kotlinx.coroutines.*
import java.lang.ArithmeticException
import java.lang.RuntimeException

/**
 *  19 异常的处理
 *      https://www.kotlincn.net/docs/reference/coroutines/exception-handling.html
 *      被取消的协程会在挂起点抛出CancellationException并且会被协程的机制忽略。
 *  19.1 异常的传播
 *   https://juejin.cn/post/6935472332735512606
 *      如果要在协程完成之前重试该操作或尝试其他操作，使用try-catch。通过在协程中捕获异常（try-catch），该异常不会在Job
 *  层次结构中传播，也不会利用结构化并发的取消功能。而CoroutineExceptionHandler是处理协程完成后发生的逻辑。绝大多数，我
 *  们使用CoroutineExceptionHandler。
 *  19.2 coroutineScope函数 异常处理特性
 *  coroutineScope函数包裹的子协程发生的异常，该异常可以被重新抛出，并在Job层次结构中进行传播。而
 *  supervisorScope包裹的协程，会创建一个新的独立作用域，并将SupervisorJob作为其Job，该异常不会
 *  在Job层次结构中传播，该协程属于顶级协程。
 *  19.3 取消与异常
 *      协程内部通过CancellationException来取消。当调用job.cancel的时候，协程会被终止，但是它的
 *  父协程不会取消。
 *
 *
 **/

fun main() {
//    coroutineStudy01Exception()
//    coroutineStudy02Exception()
//    coroutineStudy03Exception()
//    coroutineStudy04ExceptionHandler()
//    coroutineStudy05ExceptionHandler()
//    coroutineStudy06ExceptionHandler()
//    coroutineStudy07ExceptionAsync()
//    coroutineStudy08ExceptionAsync()
//    coroutineStudy09ExceptionAsync()
//    coroutineStudy10ExceptionCoroutineScope()
//    coroutineStudy11ExceptionSupervisorScope()
    coroutineStudyExceptionSupervisorScope()
}

/**
 * supervisorScope函数包裹的由launch创建的协程是顶级协程，该协程中发生的异常，可以在
 * CoroutineExceptinHandler中被捕获
 */
private fun coroutineStudyExceptionSupervisorScope() {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("handle $throwable in handler.")
    }

    val topLevelScope = CoroutineScope(Job())
    topLevelScope.launch {
        val job1 = launch {
            println("starting coroutin 1.")
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
    }

    Thread.sleep(100L)
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

/**
 * 有launch构建的子协程，被coroutineScope函数所包裹，并且在子协程中发生了异常，这样异常不会传播
 * 到Job层次结构中，如果只对该子协程使用try-catch语句是可以捕获到异常的。
 * coroutineScope函数主要用在suspend函数中已实现“并行分解”，这些suspend函数将重新抛出其失败协程
 * 的异常，我们可以使用try-catch捕获异常。
 */
private fun coroutineStudy10ExceptionCoroutineScope() {
    val topLevelScope = CoroutineScope(SupervisorJob())
    topLevelScope.launch {
        try {
            coroutineScope {
                launch {
                    throw RuntimeException("RuntimeExceptin in child launch coroutine.")
                }
            }
        } catch (e: Exception) {
            println("handle $e in try-catch.")
        }
    }
    Thread.sleep(100L)
}

/**
 *  async构建的非顶级协程中发生了异常，该异常会传播到Job的层次结构中，并有CoroutineExceptionHandler捕获，甚至传递该
 *  线程的未捕获的异常程序，即使不调用await函数。
 */
private fun coroutineStudy09ExceptionAsync() {
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
 * async启动的顶层协程中发生了异常，在当Deferred调用await函数的时候是会抛出异常的。
 */
private fun coroutineStudy08ExceptionAsync() {
    val topLevelScope = CoroutineScope(SupervisorJob())
    val deferred = topLevelScope.async {
        throw RuntimeException("RuntimeException in async coroutine.")
    }
    topLevelScope.launch {
        try {
            deferred.await()
        } catch (e: Exception) {
            println("handle $e in try-catch.")
        }
    }

    Thread.sleep(100L)
}

/**
 * async构建的顶级携程中throw RuntimeException，且不调用await函数，则运行程序，不会抛出异常。
 *  async构建的携程的返回结果是Deferred是一种特殊的Job，有结果的，如果对应的协程中发生异常，会将异常封装在Deferred中，
 *  所以如果不调用await函数是不会抛出异常的。
 */
private fun coroutineStudy07ExceptionAsync() {
    val topLevelScope = CoroutineScope(SupervisorJob())
    topLevelScope.async {
        throw RuntimeException("runTimeException in async coroutine.")
    }
    Thread.sleep(100L)
}

/**
 *  在构建顶层域CoroutineScope的时候向其构造方法中传入CoroutineExceptionHanlder，并在启动
 *  子协程的构建器中传入同一个CoroutineExceptionHandler的对象。如此在子协程发生异常的时候
 *  CoroutineExceptionHandler可以捕获到异常。
 */
private fun coroutineStudy06ExceptionHandler() {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("$throwable in exceptionHandler.")
    }

    val topLevelScope = CoroutineScope(Job() + exceptionHandler)
    topLevelScope.launch {
        launch(exceptionHandler) {
            throw RuntimeException("runTimeException in nested coroutine.")
        }
    }
    Thread.sleep(100L)
}

/**
 *  给父协程的构建器传入CoroutineExceptionHandler，当子协程出现异常的时候，CoroutineExceptionHandler
 *  可以捕获该异常。
 */
private fun coroutineStudy05ExceptionHandler() {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("$throwable in exceptionHandler.")
    }

    val topLevleScope = CoroutineScope(Job())
    topLevleScope.launch(exceptionHandler) {
        launch {
            throw RuntimeException("runTimeException in nested coroutine.")
        }
    }

    Thread.sleep(100L)
}

/**
 *  由于CoroutineExceptionHandler是一个ContextElement，可以在启动子协程时传递给协程构建器。
 *  但是只给子协程设置是没有用的。
 */
private fun coroutineStudy04ExceptionHandler() {
    val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("$throwable in exceptionHandler")
    }

    val topLevelScope = CoroutineScope(Job())
    topLevelScope.launch {
        launch(coroutineExceptionHandler) {
            throw RuntimeException("runtimeException in nested coroutine.")
        }
    }

    Thread.sleep(100L)
}

/**
 * 应用程序crash，并且try-catch捕获不了子协程的异常。
 *  CoroutineScope的Job对象以及Coroutines和Child-Coroutines的Job对象形成了父子关系的层次结构，
 *  异常在工作层次结构中传播，进而导致父Job的失败，导致其所有子级的Job的取消。
 *  这样异常有子协程传递给了父协程，且try-catch捕获不了这种异常。
 *  传播的异常可以通过CoroutineExceptionHandler来捕获。如果未设置，则将调用线程的未捕获异常处理程
 *  序，可能会导致退出应用程序。
 *
 */
private fun coroutineStudy03Exception() {
    val topLevelScope = CoroutineScope((Job()))
    topLevelScope.launch {
        try {
            launch {
                throw RuntimeException("runtimeException in nest coroutine.")
            }
        } catch (e: Exception) {
            println("handle $e")
        }
    }
    Thread.sleep(100L)
}

private fun coroutineStudy02Exception() {
    val topLevelScope = CoroutineScope(Job())
    topLevelScope.launch {
        try {
            throw RuntimeException("runtimeException in coroutine!")
        } catch (e: Exception) {
            println("handle $e")
        }
    }
    Thread.sleep(100L)
}

/**
 *  由launch构建的顶级协程中发生的异常，异常将由CoroutineExceptionHandler处理或传播给线程的未捕获的异常程序。
 */
private fun coroutineStudy01Exception() {
    runBlocking {
        val job = GlobalScope.launch {
            println("throw exceptin from launch")
            throw IndexOutOfBoundsException()
        }
        job.join()
        println("joined failed job")
        val deferred = GlobalScope.async {
            println("throws exception from aync")
            throw ArithmeticException()
        }
        try {
            deferred.await()
            println("defered wait")
        } catch (e: ArithmeticException) {
            println("caugth ArithmeticException")
        }
    }
}