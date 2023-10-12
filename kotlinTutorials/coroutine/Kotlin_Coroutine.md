# 19 协程
2 https://johnnyshieh.me/posts/kotlin-coroutine-introduction/

1 [Kotlin Coroutines(协程) 完全解析（一），协程简介](https://www.jianshu.com/p/2659bbe0df16)

3 [Kotlin Coroutine github](https://github.com/Kotlin/kotlinx.coroutines)

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
协程调度器，决定协程所在的线程，coroutines-core库中内置了三种协程调度器，分别是
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



