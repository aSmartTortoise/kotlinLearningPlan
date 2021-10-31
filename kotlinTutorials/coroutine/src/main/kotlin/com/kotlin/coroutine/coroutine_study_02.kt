package com.kotlin.coroutine

import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

/**
 *  21 协程
 *  https://www.jianshu.com/p/6e6835573a9c
 *  https://www.bennyhuo.com/2019/04/01/basic-coroutines/
 *  协程是一种非抢占式或协作式的程序并发与调度的框架，协程中的程序可以自动挂起或恢复执行。JVM
 *  中的线程的实现会映射到内核的线程，线程中的代码只有当该线程抢占到cpu时间片才会执行。而
 *  协程的实现不会映射到内核的线程或其他比较重的资源，所以协程相对于线程比较轻量。
 *  22 协程的几种常见实现
 *  22.1 按有无调用栈分类
 *      由于协程支持挂起、恢复，因此挂起点的状态保存极为重要。类似地线程会因CPU的调度权切换
 *  而被中断，它的中断状态会保存在调用栈中，因而协程的实现也按照是否开辟调用栈而分为下面两种
 *  类型。
 *      （1）stackfull coroutine有栈协程；每个协程都会有自己的调用栈，这种情况下的协程实现
 *  很大程度上类似于线程。
 *      （2）stackless coroutine无栈协程；协程没有自己的调用栈，挂起点状态的保持通过状态机
 *  或者闭包等语法来实现。
 *      Kotlin的协程是一种无栈协程，它的控制流转依靠对协程体本身编译生成的状态机的状态流转来
 *  实现，变量保存是通过闭包语法来实现的。不过Kotlin的协程可以在任意层级挂起。
 *  22.2 按调度方式分类
 *      调度过程中，按照转移调度权的目标分为对称协程和非对称协程。
 *      （1）symmetric coroutine对称协程；任何一个协程是相互独立且平等的，调度权可以在任意
 *  协程之间转换。
 *      （2）asymmetric coroutine非对称协程；协程出让调度权的目标只能是它的调用者，即协程
 *  之间有调用和被调用的关系。
 *
 *      不管怎么分类，协程的本质就是程序自己处理挂起和恢复。协程描述了多个程序之间如何出让
 *  运行调度权来完成执行。基于对基本的调度权转移的实现衍生出了各种异步模型，并发模型比如
 *  async/await、channel、Flow等。
 *  23 协程调度 https://www.bennyhuo.com/2019/04/11/coroutine-dispatchers/
 *  23.1 协程上下文
 *      协程上下文的接口类型为CoroutineContext，通常我们见到的上下文类型是CombinedContext和Empty-
 *  CoroutineContext，前者表示上下文的组合，后者表示什么也没有。
 *      CoroutineContext是一个集合，元素是Element，每一个Element都有一个Key。Element同时也是
 *  CoroutineContext的子接口。
 *      我们在协程体中访问到的coroutineContext大多是CombinedContext类型，表示有很多的上下文实现的集合。
 *  如果要获取特定的上下午实现，就需要通过指定的key来获取。
 *      在协程都构建器里，可以指定上下文为协程添加一些特性，比如添加CoroutineName，如果有多个上下文可以通过
 *  + 操作符连接组合。
 *
 *
 *
 *
 */
class MyContinuationInterceptor : ContinuationInterceptor {
    override val key = ContinuationInterceptor
    override fun <T> interceptContinuation(continuation: Continuation<T>) = MyContinuation(continuation)
}
class MyContinuation<T>(val continuation: Continuation<T>) : Continuation<T> {
    override val context: CoroutineContext = continuation.context
    override fun resumeWith(result: Result<T>) {
        log("<MyContinuation> $result")
        continuation.resumeWith(result)
    }



}
fun main() {
    runBlocking {
//        CoroutineContextStudy01()
        ContinuationInterceptorStudy02()
    }
}

/**
 *  需要运行代码根据运行结果加以理解。
 *  所有协程启动的时候，都会有一次Continuation.resumeWith的操作，这一次操作对于调度器来说就是一次调度的机会，协程有机
 *  会调度到其他线程也是基于此。delay是挂起点，1000ms之后需要继续调度执行该协程。delay之后会有线程切换的操作。在JVM上
 *  delay实际是在一个ScheduledExecutor中添加一个延时任务，所以会发生线程切换。
 */
private suspend fun ContinuationInterceptorStudy02() {
    GlobalScope.launch(MyContinuationInterceptor()) {
        log(1)
        val job = async {
            log(2)
            delay(1000)
            log(3)
            "Hello"
        }
        log(4)
        val result = job.await()
        log("5.$result")
    }.join()
    log(6)
}

private fun CoroutineContextStudy01(): Job {
    return GlobalScope.launch(CoroutineName("Hello")) {
        println(coroutineContext[Job])//这里的Job实际上是对Job这个接口定义的伴生对象的引用。
    }
}
