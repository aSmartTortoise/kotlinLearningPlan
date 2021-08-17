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
 *
 **/

fun main() {
//    coroutineStudy01Exception()
//    coroutineStudy02Exception()
//    coroutineStudy03Exception()
//    coroutineStudy04ExceptionHandler()
//    coroutineStudy05ExceptionHandler()
    coroutineStudy06ExceptionHandler()
}

/**
 *  在构建顶层于CoroutineScope的时候向其构造方法中传入CoroutineExceptionHanlder，并在启动
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