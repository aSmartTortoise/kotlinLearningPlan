# 19 协程
2 https://johnnyshieh.me/posts/kotlin-coroutine-introduction/

1 [Kotlin Coroutines(协程) 完全解析（一），协程简介](https://www.jianshu.com/p/2659bbe0df16)

3 [Kotlin Coroutine github](https://github.com/Kotlin/kotlinx.coroutines)

4 [Kotlin Coroutines(协程) 完全解析（二），深入理解协程的挂起、恢复与调度](https://www.jianshu.com/p/2979732fb6fb)

5 [Kotlin Coroutines(协程) 完全解析（三），封装异步回调、协程间关系及协程的取消](https://www.jianshu.com/p/2857993af646)
## 19.1 为什么需要协程？
协程可以是异步编程更简洁和可阅读。

异步编程中我们为了解决后一个任务和前一个任务的依赖关系，通常会使用到回调、Rx异步响应式框架。

比如以下的代码中涉及三个函数任务，后两个函数是依赖于第一个函数的返回结果，且三个函数都是耗时的，
运行在子线程中。
```K
fun requestToken(): Token {
    // makes request for a token & waits
    return token // returns result when received 
}

fun createPost(token: Token, item: Item): Post {
    // sends item to the server & waits
    return post // returns resulting post 
}

fun processPost(post: Post) {
    // does some local processing of result
}
```
为了解决后两个函数对于第一个函数的依赖，由如下的解决方式。
### 19.1.1 回调
将后面执行的函数封装成回调，比如可以使用高阶函数。
```K
fun requestTokenAsync(cb: (Token) -> Unit) { ... }
fun createPostAsync(token: Token, item: Item, cb: (Post) -> Unit) { ... }
fun processPost(post: Post) { ... }

fun postItem(item: Item) {
    requestTokenAsync { token ->
        createPostAsync(token, item) { post ->
            processPost(post)
        }
    }
}
```
异步编程中，涉及两个函数，回调解决两个函数的依赖关系是比较简洁实用的，但是三个以上的函数场景
使用回调会出现多层嵌套的问题，而且不方便处理异常。
### 19.1.2 Rx响应式框架
```K
fun requestToken(): Token { ... }
fun createPost(token: Token, item: Item): Post { ... }
fun processPost(post: Post) { ... }

fun postItem(item: Item) {
    Single.fromCallable { requestToken() }
            .map { token -> createPost(token, item) }
            .subscribe(
                    { post -> processPost(post) }, // onSuccess
                    { e -> e.printStackTrace() } // onError
            )
}
```
Rx响应式框架，丰富的操作符、编写的线程调度、异常处理，可以简化异步编程。
### 19.1.3 协程
```K
suspend fun requestToken(): Token { ... }   // 挂起函数
suspend fun createPost(token: Token, item: Item): Post { ... }  // 挂起函数
fun processPost(post: Post) { ... }

fun postItem(item: Item) {
    GlobalScope.launch {
        val token = requestToken()
        val post = createPost(token, item)
        processPost(post)
        // 需要异常处理，直接加上 try/catch 语句即可
    }
```
协程处理异步编程也非常的简洁，异常处理也很方便。
## 19.2 协程的概念
协程的开发人员 Roman Elizarov 是这样描述协程的：协程就像非常轻量级的线程。线程是由系统调度
的，线程切换或线程阻塞的开销都比较大。而协程依赖于线程，但是协程挂起时不需要阻塞线程，
协程是由开发者控制的。所以协程也像用户态的线程，非常轻量级，一个线程中可以创建任意个协程。

协程可以简化异步编程，可以顺序地表达程序，协程也提供了一种避免阻塞线程并用更轻量、更可控的操作替
代线程阻塞的方法 -- 协程挂起。
### 19.2.1 挂起函数
suspend 修饰的函数就是挂起函数。挂起函数可能挂起协程（如果相关调用的结果已经可用，则库决定
继续进行而不挂起），挂起函数挂起协程时，不会阻塞线程。挂起函数执行完成后会恢复协程，只能在协程
中或其他挂起函数中调用挂起函数。
```K
fun postItem(item: Item) {
    GlobalScope.launch {
        val token = requestToken()
        val post = createPost(token, item)
        processPost(post)
        // 需要异常处理，直接加上 try/catch 语句即可
    }
```
上面的代码中，GlobalScope.launch就创建了一个协程。

launch函数:
```K
public fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    val newContext = newCoroutineContext(context)
    val coroutine = if (start.isLazy)
        LazyStandaloneCoroutine(newContext, block) else
        StandaloneCoroutine(newContext, active = true)
    coroutine.start(start, coroutine, block)
    return coroutine
}
```
launch函数定义中出现了一些重要的概念：CoroutineScope、CoroutineContext, 
CoroutineStart, Job等。
### 19.2.2 CoroutineScope
CoroutineScope是个接口，可以理解为协程。
### 19.2.3 CoroutineContext
CoroutineContext是一个接口，可以理解为协程的上下文。它是一个持有一组Element元素的集合，
定义了增plus、删minusKey、查get等方法。而Element也是继承CoroutineContext的内部接口。

EmptyCoroutineContext是个对象，实现了CoroutineContext，集合为空。
### 19.2.4 CoroutineStart
是一个枚举类，定义了协程的启动选项。启动类型有
DEFAULT, LAZILY, ATOMIC, UNDISPATCHED四种。

### 19.2.5 CoroutineDispatcher
协程调度器，也叫做协程的线程调度器，决定协程所在的线程，coroutines-core库中内置了三种协程调度器，分别是
Dispatchers.Default，Dispatchers.IO, Dispatchers.Main, Dispatchers.UnConfined。
#### 19.2.5.1 Dispatchers.Default
内部维护了一个线程池，该线程池核心线程数至少是2个至多是设备的cpu数目个，最大线程数为2的21次方-1个
，keepAliveTime为60s。协程执行的任务适合耗cpu资源的任务。
#### 19.2.5. Dispatchers.IO
协程执行的任务适合IO密集型的任务。io/socket io。
#### 19.2.5.3 Dispatchers.Main
协程运行在主线程中。
#### 19.2.5.4 Dispatchers.Unconfined
没有指定协程所在的线程，默认就是上游的线程。在协程的构建器中不能指定该种类型的
CoroutineDispatcher。
#### 19.2.6 Job
Job是一个接口，继承了CoroutineContext.Element。它具有生命周期周期，可以被取消，有六种状态，
分别是New, Active, Completing, Cancelling, Cancelled, Completed。Cancelled也
是一种完成状态。

Job可以被组织成父子层级关系，父Job的取消会递归导致所有的子Job取消，子Job如果因为运行过程中出
现异常而被取消，也会导致父Job被取消，进而导致其他子Job被取消。

Completing状态是Job内部的一个状态，该状态下parent Job在等待child Job，对于外部观察者来说，
Completing状态仍然是Active。

Job接口及其派生接口中定义的函数都是线程安全的。

Job完成后是没有result的。
#### 19.2.7 Deferred
Deferred是一个接口，继承Job，它的完成是有result的。在Deferred完成后，通过await方法获取
result。
## 19.3 Coroutine builders
创建协程的方法。
### 19.3.1 CoroutineScope.launch
创建一个协程，不阻塞当前线程，
### 19.3.2 runBlocking
创建一个协程，阻塞当前线程，直到协程完成。是为main函数和测试中使用协程设计的。
### 19.3.3 withContext
不会创建协程，在指定的协程上运行挂起代码块，挂起协程直到代码块运行完成。
### 19.3.4 async
创建一个协程，不阻塞当前线程，返回的Deferred。
## 19.4 挂起函数的工作原理
协程的内部使用了Kotlin编译器的一些编译技术，当调用挂起函数时，都有一个隐式的对象传入，该对象
是Continuation，它封装了协程从最后挂起点恢复执行的逻辑。
### 19.4.1 Continuation
是一个接口，封装了协程从挂起点恢复执行的逻辑。

协程中的代码经Kotlin编译器编译后，挂起函数被将协程中的执行过程拆分成Continuation片段，利用状态机的
控制逻辑保证各个Continuation片段按顺序执行。

```K
fun postItem(item: Item) {
    GlobalScope.launch {
        // await() 是挂起函数，当前协程执行逻辑卡在第一个分支，第一种状态，当 async 的
        // 协程执行完后恢复当前协程，才会切换到下一个分支
        val token = async { requestToken() }.await()
        // 在第二个分支状态中，又新建一个协程，使用 await 挂起函数将之后代码作为 
        // Continuation 放倒下一个分支状态，直到 async 协程执行完
        val post = aync { createPost(token, item) }.await()
        // 最后一个分支状态，直接在当前协程处理
        processPost(post)
    }
}
```
挂起函数不一定会挂起协程，当挂起函数的结果可用，库可以决定不挂起协程而继续执行。挂起函数可能会挂起
协程，但是不会阻塞线程。

协程的挂起是通过suspend函数实现，协程的恢复是通过Continuation.resumeWith实现。

协程从挂起点恢复后所在的线程由挂起函数所在的线程决定。

## 19.5 协程的取消
对于协程的取消，Job#cancel()只是将协程的状态修改为已取消状态，并不能取消协程的运算逻辑，
协程库中很多挂起函数都会检测协程状态，如果想及时取消协程的运算，最好使用isActive判断协程状态,
作为结束协程运算逻辑的条件。
```K
fun main(args: Array<String>): Unit = runBlocking {
    val job1 = launch(Dispatchers.Default) {
        repeat(5) {
            println("job1 sleep ${it + 1} times")
            // job1.cancel之后，协程的状态是Cancelled，delay会坚持协程（Job）的状态，从而不再继续执行下去。
            delay(500)
        }
    }
    delay(700)
    job1.cancel()
    val job2 = launch(Dispatchers.Default) {
        var nextPrintTime = 0L
        var i = 1
        // job2.cancel调用之后，没有判断协程（Job）的状态，所以循环体的代码仍然会执行，
        // 可以使用isActive获取协程的状态来作为条件。
        while (i <= 5) {
            val currentTime = System.currentTimeMillis()
            if (currentTime >= nextPrintTime) {
                println("job2 sleep ${i++} ...")
                nextPrintTime = currentTime + 500L
            }
        }
    }
    delay(700)
    job2.cancel()
}
```
## 19.6 协程Job之间的关系
GlobalScope.launch{}和GlobalScope.async{}新建的协程是没有父协程的，而在协程
中使用launch{}和async{}一般都是子协程。对于父子协程需要注意下面三种关系：

父协程手动调用cancel()或者异常结束，会立即取消它的所有子协程。

父协程必须等待所有子协程完成（处于完成或者取消状态）才能完成。

子协程抛出未捕获的异常时，默认情况下会取消其父协程。

## 19.7 协程的核心api
### yield
挂起当前协程，让CoroutineDispatcher维护的线程运行其他协程。当其他协程执行完成或也
让出执行权时，协程恢复运行。






