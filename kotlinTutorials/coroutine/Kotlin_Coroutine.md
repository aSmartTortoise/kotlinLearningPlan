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

子协程抛出未捕获的异常时，默认情况下会取消其父协程，而抛出CancellationException是正常的协程
取消，不会取消父协程。

## 19.7 协程的核心api
### yield
挂起函数，挂起当前协程，让CoroutineDispatcher维护的线程运行其他协程。当其他协程执行完成或也
让出执行权时，协程恢复运行。
### join
挂起函数，Job的实例函数，会挂起所在的协程直到该Job结束为止。

该函数会检测所在协程的作业是否已取消，如果该函数在调用时候或者挂起所在协程的时候，所在协程的Job
已取消，则该函数抛出CancellationException。
## 19.8 协程的创建、启动、线程调度分析
### 19.8.1 协程的创建、启动
常见的协程创建的方式是通过CoroutineScope#launch扩展函数创建，如下：

Builders.common.kt
```K
public fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    // 1 调用newCoroutineContext函数，继承CoroutineScope中的CoroutineContext，并
    // 添加默认的CoroutineDispatcher为Dispatchers.Default
    val newContext = newCoroutineContext(context)
    val coroutine = if (start.isLazy)
        LazyStandaloneCoroutine(newContext, block) else
        // 2 构造StandaloneCoroutine。
        StandaloneCoroutine(newContext, active = true)
    // 3 调用AbstractCoroutine#start函数启动协程。
    coroutine.start(start, coroutine, block)
    // 4 返回StandaloneCoroutine，是一个Job。
    return coroutine
}
```
CoroutineContext.kt
```K
@ExperimentalCoroutinesApi
public actual fun CoroutineScope.newCoroutineContext(context: CoroutineContext): CoroutineContext {
    // 1 继承CoroutineScope的CoroutineContext，添加指定的context，和 CoroutineDispatcher(Dispatchers.Default)
    val combined = foldCopies(coroutineContext, context, true)
    val debug = if (DEBUG) combined + CoroutineId(COROUTINE_ID.incrementAndGet()) else combined
    return if (combined !== Dispatchers.Default && combined[ContinuationInterceptor] == null)
        debug + Dispatchers.Default else debug
}
```
Builders.common.kt
```K
private open class StandaloneCoroutine(
    parentContext: CoroutineContext,
    active: Boolean
) : AbstractCoroutine<Unit>(parentContext, initParentJob = true, active = active) {
    override fun handleJobException(exception: Throwable): Boolean {
        handleCoroutineException(context, exception)
        return true
    }
}
```
AbstractCoroutine.kt
```K
public abstract class AbstractCoroutine<in T>(
    parentContext: CoroutineContext,
    initParentJob: Boolean,
    active: Boolean
) : JobSupport(active), Job, Continuation<T>, CoroutineScope {
    ...
    public fun <R> start(start: CoroutineStart, receiver: R, block: suspend R.() -> T) {
        // 1 调用CoroutineStart#invoke函数。
        start(block, receiver, this)
    }
}
```
StandaloneCoroutine的类层级关系如下。
StandaloneCoroutine -> AbstractCoroutine

AbstractCoroutine 继承JobSupport，实现了Job, Continuation, CoroutineScope接口。

```K
public enum class CoroutineStart {
    ...
    public operator fun <R, T> invoke(block: suspend R.() -> T, receiver: R, completion: Continuation<T>): Unit =
        when (this) {
        // 1 调用函数类型扩展函数startCoroutineCancellable
            DEFAULT -> block.startCoroutineCancellable(receiver, completion)
            ATOMIC -> block.startCoroutine(receiver, completion)
            UNDISPATCHED -> block.startCoroutineUndispatched(receiver, completion)
            LAZY -> Unit // will start lazily
        }
    ...
}
```

```K
Cacellable.kt
internal fun <R, T> (suspend (R) -> T).startCoroutineCancellable(
    receiver: R, completion: Continuation<T>,
    onCancellation: ((cause: Throwable) -> Unit)? = null
) =
// 1 函数中的参数receiver和completion 均为StandaloneCoroutine对象
    runSafely(completion) {
    // 2 最后调用了Continuation#resumeCancellableWith启动协程。
        createCoroutineUnintercepted(receiver, completion).intercepted().resumeCancellableWith(Result.success(Unit), onCancellation)
    }
```
https://github.com/JetBrains/kotlin/blob/master/libraries/stdlib/jvm/src/kotlin/coroutines/intrinsics/IntrinsicsJvm.kt

IntrinsicsJvm.kt
```K
/**
 * Creates unintercepted coroutine with receiver type [R] and result type [T].
 * This function creates a new, fresh instance of suspendable computation every time it is invoked.
 *
 * To start executing the created coroutine, invoke `resume(Unit)` on the returned [Continuation] instance.
 * The [completion] continuation is invoked when coroutine completes with result or exception.
 *
 * This function returns unintercepted continuation.
 * Invocation of `resume(Unit)` starts coroutine immediately in the invoker's call stack without going through the
 * [ContinuationInterceptor] that might be present in the completion's [CoroutineContext].
 * It is the invoker's responsibility to ensure that a proper invocation context is established.
 * Note that [completion] of this function may get invoked in an arbitrary context.
 *
 * [Continuation.intercepted] can be used to acquire the intercepted continuation.
 * Invocation of `resume(Unit)` on intercepted continuation guarantees that execution of
 * both the coroutine and [completion] happens in the invocation context established by
 * [ContinuationInterceptor].
 *
 * Repeated invocation of any resume function on the resulting continuation corrupts the
 * state machine of the coroutine and may result in arbitrary behaviour or exception.
 */
@SinceKotlin("1.3")
public actual fun <R, T> (suspend R.() -> T).createCoroutineUnintercepted(
    receiver: R,
    completion: Continuation<T>
): Continuation<Unit> {
    val probeCompletion = probeCoroutineCreated(completion)
    return if (this is BaseContinuationImpl)
        create(receiver, probeCompletion)
    else {
        createCoroutineFromSuspendFunction(probeCompletion) {
            (this as Function2<R, Continuation<T>, Any?>).invoke(receiver, it)
        }
    }
}
```
函数创建了一个协程，返回Continuation，通过这个返回的Continuation调用resume(Unit)启动该协程，
### 19.8.2 协程的线程调度
```K
Cacellable.kt
internal fun <R, T> (suspend (R) -> T).startCoroutineCancellable(
    receiver: R, completion: Continuation<T>,
    onCancellation: ((cause: Throwable) -> Unit)? = null
) =
// 1 函数中的参数receiver和completion 均为StandaloneCoroutine对象
    runSafely(completion) {
        // 2 调用Continuation#intercepted扩展函数。
        // 3 调用ContinuationImpl#intercepted函数。
        // 4 调用CoroutineDispatcher#interceptContinuation返回DispatchedContinuation
        // 5 调用DispatchedContinuation#resumeCancellableWith函数启动协程。
        createCoroutineUnintercepted(receiver, completion).intercepted().resumeCancellableWith(Result.success(Unit), onCancellation)
    }
```
createCoroutineUnintercepted(receiver, completion)函数返回的是一个Continuation实际上是一个
SuspendLambda，而这个SuspendLambda是由Kotlin编译器编译的类，该类的invokeSuspend方法封装了
协程 block的运算逻辑。

ContinuationImpl.kt
```K
internal abstract class BaseContinuationImpl(
    public val completion: Continuation<Any?>?
) : Continuation<Any?>, CoroutineStackFrame, Serializable {

}
...
internal abstract class ContinuationImpl(
    completion: Continuation<Any?>?,
    private val _context: CoroutineContext?
) : BaseContinuationImpl(completion) {
    ...
}
...
internal abstract class SuspendLambda(
    public override val arity: Int,
    completion: Continuation<Any?>?
) : ContinuationImpl(completion), FunctionBase<Any?>, SuspendFunction {
    ...
}
```
SuspendLambda的类层级结构为：

SuspendLambda -> ContinuationImpl -> BaseContinuationImpl -> Continuation

IntrinsicsJvm.kt
```K
public actual fun <T> Continuation<T>.intercepted(): Continuation<T> =
    (this as? ContinuationImpl)?.intercepted() ?: this
```
ContinuationImpl.kt
```K
@Transient
    private var intercepted: Continuation<Any?>? = null

    public fun intercepted(): Continuation<Any?> =
        intercepted
            ?: (context[ContinuationInterceptor]?.interceptContinuation(this) ?: this)
                .also { intercepted = it }
```
CoroutineDispatcher.kt
```K
    public final override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> =
        DispatchedContinuation(this, continuation)
```
DispatchedContinuation.kt
```K
internal class DispatchedContinuation<in T>(
    @JvmField internal val dispatcher: CoroutineDispatcher,
    @JvmField val continuation: Continuation<T>
) : DispatchedTask<T>(MODE_UNINITIALIZED), CoroutineStackFrame, Continuation<T> by continuation {
...
    @Suppress("NOTHING_TO_INLINE")
    internal inline fun resumeCancellableWith(
        result: Result<T>,
        noinline onCancellation: ((cause: Throwable) -> Unit)?
    ) {
        val state = result.toState(onCancellation)
        // 1 这里dispatcher对应于之前分析的Dispatchers.Default，
        // CoroutineDispatcher#isDispatchNeeded默认返回true。
        if (dispatcher.isDispatchNeeded(context)) {
            _state = state
            resumeMode = MODE_CANCELLABLE
            // 2 调用dispatch函数。DispatchedContinuation实现了Runnable
            dispatcher.dispatch(context, this)
        } else {
            executeUnconfined(state, MODE_CANCELLABLE) {
                if (!resumeCancelled(state)) {
                    resumeUndispatchedWith(result)
                }
            }
        }
    }
    ...
public fun <T> Continuation<T>.resumeCancellableWith(
    result: Result<T>,
    onCancellation: ((cause: Throwable) -> Unit)? = null
): Unit = when (this) {
    // 1 调用DispatchedContinuation#resumeCancellableWith函数
    is DispatchedContinuation -> resumeCancellableWith(result, onCancellation)
    else -> resumeWith(result)
}
...
```
Dispatchers.kt
```K
public actual object Dispatchers {
    @JvmStatic
    public actual val Default: CoroutineDispatcher = DefaultScheduler
    ...
}
```
Dispatcher.kt
```K
internal object DefaultScheduler : SchedulerCoroutineDispatcher(
    CORE_POOL_SIZE, MAX_POOL_SIZE,
    IDLE_WORKER_KEEP_ALIVE_NS, DEFAULT_SCHEDULER_NAME
) {

}
...
internal open class SchedulerCoroutineDispatcher(
    private val corePoolSize: Int = CORE_POOL_SIZE,
    private val maxPoolSize: Int = MAX_POOL_SIZE,
    private val idleWorkerKeepAliveNs: Long = IDLE_WORKER_KEEP_ALIVE_NS,
    private val schedulerName: String = "CoroutineScheduler",
) : ExecutorCoroutineDispatcher() {
    ...
    private var coroutineScheduler = createScheduler()

    private fun createScheduler() =
        CoroutineScheduler(corePoolSize, maxPoolSize, idleWorkerKeepAliveNs, schedulerName)
    // 接上文分析，调用到了这里。
    override fun dispatch(context: CoroutineContext, block: Runnable): Unit = coroutineScheduler.dispatch(block)
    ...
}
```

```K
internal class CoroutineScheduler(
    @JvmField val corePoolSize: Int,
    @JvmField val maxPoolSize: Int,
    @JvmField val idleWorkerKeepAliveNs: Long = IDLE_WORKER_KEEP_ALIVE_NS,
    @JvmField val schedulerName: String = DEFAULT_SCHEDULER_NAME
) : Executor, Closeable {
    ...
    fun dispatch(block: Runnable, taskContext: TaskContext = NonBlockingContext, tailDispatch: Boolean = false) {
        // 1 通过内置的线程池执行block，而这里的block就是DispatchedCoroutiation，也就是要执行run方法。
    }
}
```

```K
internal abstract class DispatchedTask<in T> internal constructor(
    @JvmField public var resumeMode: Int
) : SchedulerTask() {
    ...
    final override fun run() {
        ...
        val taskContext = this.taskContext
        var fatalException: Throwable? = null
        try {
            val delegate = delegate as DispatchedContinuation<T>
            val continuation = delegate.continuation
            withContinuationContext(continuation, delegate.countOrElement) {
                val context = continuation.context
                val state = takeState() // NOTE: Must take state in any case, even if cancelled
                val exception = getExceptionalResult(state)
                val job = if (exception == null && resumeMode.isCancellableMode) context[Job] else null
                if (job != null && !job.isActive) {
                    val cause = job.getCancellationException()
                    cancelCompletedResult(state, cause)
                    continuation.resumeWithStackTrace(cause)
                } else {
                    if (exception != null) {
                        continuation.resumeWithException(exception)
                    } else {
                        // 1 调用Continuation#resume扩展函数。
                        continuation.resume(getSuccessfulResult(state))
                    }
                }
            }
        }
    }
    ...
}
```
Continuation.kt
```K
public inline fun <T> Continuation<T>.resume(value: T): Unit =
    // 1 调用SuspendLambda的实例方法resumeWith
    resumeWith(Result.success(value))
```
ContinuationImpl.kt
```
internal abstract class BaseContinuationImpl(
    public val completion: Continuation<Any?>?
) : Continuation<Any?>, CoroutineStackFrame, Serializable {
    public final override fun resumeWith(result: Result<Any?>) {
        var current = this
        var param = result
        while (true) {
            ...
            with(current) {
                val completion = completion!! // fail fast when trying to resume continuation without completion
                val outcome: Result<Any?> =
                    try {
                        // 1 执行了协程 block中的运算逻辑，协程运行了。并通过try-catch代码块捕获
                        // 运算逻辑中抛出的异常。
                        val outcome = invokeSuspend(param)
                        if (outcome === COROUTINE_SUSPENDED) return
                        Result.success(outcome)
                    } catch (exception: Throwable) {
                        Result.failure(exception)
                    }
                releaseIntercepted() // this state machine instance is terminating
                if (completion is BaseContinuationImpl) {
                    // unrolling recursion via loop
                    current = completion
                    param = outcome
                } else {
                    // top-level completion reached -- invoke and return
                    // 2 调用前文分析的StandaloneCoroutine的实例方法resumeWith（AbstractCoroutine类的方法）
                    // 该函数结束协程。
                    completion.resumeWith(outcome)
                    return
                }
            }
        }
    }
    ...
}
```
### 19.8.3 协程的三层包装
协程其实有三层包装。常用的launch和async返回的Job、Deferred，里面封装了协程状态，提供了取消协程接口，而它们的实例都是
继承自AbstractCoroutine，它是协程的第一层包装。第二层包装是编译器生成的SuspendLambda的子类，封装了协程的真正运算逻辑，
继承自BaseContinuationImpl，其中completion属性就是协程的第一层包装。第三层包装是前面分析协程的线程调度时提到的
DispatchedContinuation，封装了线程调度逻辑，包含了协程的第二层包装。三层包装都实现了Continuation接口，
通过代理模式将协程的各层包装组合在一起，每层负责不同的功能。

![协程的三层包装](C:\Users\wangjie\Desktop\study\Kotlin_grammar\coroutine\imgs\协程的三层包装.webp)

## 19.9 协程的异常处理
### 19.9.1 CoroutineExceptionHandler
是一个接口，继承于CoroutineContext.Element，是协程的CoroutineContext中的一个Element，用于处理未捕获
的异常。

launch函数创建的协程会产生未捕获的异常，在普通Job中，由launch函数创建的子协程，如果出现了异常，
异常会传播到parent协程。root协程上下文的CoroutineExceptionHandler可以处理该异常。

如果在supervisorJob中，由launch构建的子协程，如果出现了未捕获的异常，子协程不会将异常传递给parent协程。
root协程上下文的CoroutineExceptionHandler可以处理该异常

async函数构建的root协程中出现的异常，该异常会反映的返回的Deferred中，通过Deferred#await函数抛出异常，可以
使用try-catch代码块处理该异常，异常不会导致线程的未捕获的异常。

async函数构建的子协程中出现的异常，该异常会反映的返回的Deferred中，通过Deferred#await函数抛出异常，异常会
传播到root协程，如果跟协程是由launch函数创建的，如果不使用root协程的CoroutineExceptionHandler处理，
会导致线程的未捕获异常。

### 19.9.2 coroutineScope
挂起函数，该函数会创建一个协程，该协程中创建的子协程如果发生了异常，异常会传播到root协程中，且该函数
会抛出异常，可以通过对该函数使用try-catch来处理异常。

### 19.9.4 supervisorScope
挂起函数，该函数会创建一个协程，该协程中创建的子协程如果发生了异常，异常不会传播给parent协程，该函数
不会抛出异常，可以通过子协程上下文的CoroutineExceptionHandler处理该异常。

### 19.9.5 协程的异常处理流程分析
在小节协程的创建、启动、线程调度分析中由分析到，协程block代码被编译成了一个SuspendLambda的子类，子类的
invokeSuspend函数体对应协程block中的运算逻辑，而invokeSuspend函数是由resumeWith函数调用的。如下代码：

ContinuationImpl.kt
```
internal abstract class BaseContinuationImpl(
    public val completion: Continuation<Any?>?
) : Continuation<Any?>, CoroutineStackFrame, Serializable {
    public final override fun resumeWith(result: Result<Any?>) {
        var current = this
        var param = result
        while (true) {
            ...
            with(current) {
                val completion = completion!! // fail fast when trying to resume continuation without completion
                val outcome: Result<Any?> =
                    try {
                        // 1 执行了协程 block中的运算逻辑，协程运行了。并通过try-catch代码块捕获
                        // 运算逻辑中抛出的异常。
                        val outcome = invokeSuspend(param)
                        if (outcome === COROUTINE_SUSPENDED) return
                        Result.success(outcome)
                    } catch (exception: Throwable) {
                        // 2 协程运算中所有异常都会在这里被捕获，然后作为一种运算结果
                        Result.failure(exception)
                    }
                releaseIntercepted() // this state machine instance is terminating
                if (completion is BaseContinuationImpl) {
                    // unrolling recursion via loop
                    current = completion
                    param = outcome
                } else {
                    // top-level completion reached -- invoke and return
                    // 3 调用前文分析的StandaloneCoroutine的实例方法resumeWith（AbstractCoroutine类的方法）
                    // 该函数结束协程。
                    completion.resumeWith(outcome)
                    return
                }
            }
        }
    }
    ...
}
```
协程运算过程中所有异常其实都会在第二层包装中被捕获，然后会通过
AbstractCoroutine.resumeWith(Result.failure(exception))进入到第一层包装中，协程的第一层包装不仅维护协程的
状态，还处理协程运算中的异常。

AbstractCoroutine.kt
```K
public abstract class AbstractCoroutine<in T>(
    parentContext: CoroutineContext,
    initParentJob: Boolean,
    active: Boolean
) : JobSupport(active), Job, Continuation<T>, CoroutineScope {
    ...
    public final override fun resumeWith(result: Result<T>) {
        val state = makeCompletingOnce(result.toState())
        if (state === COMPLETING_WAITING_CHILDREN) return
        afterResume(state)
    }
    ...
}
```

```K
public open class JobSupport constructor(active: Boolean) : Job, ChildJob, ParentJob {
    ...
    private fun finalizeFinishingState(state: Finishing, proposedUpdate: Any?): Any? {
        ...
        // 1 proposedException 即前面未捕获的异常
        val proposedException = (proposedUpdate as? CompletedExceptionally)?.cause
        val wasCancelling: Boolean
        val finalException = synchronized(state) {
            wasCancelling = state.isCancelling
            val exceptions = state.sealLocked(proposedException)
            val finalCause = getFinalRootCause(state, exceptions)
            if (finalCause != null) addSuppressedExceptions(finalCause, exceptions)
            finalCause
        }
        val finalState = when {
            // was not cancelled (no exception) -> use proposed update value
            finalException == null -> proposedUpdate
            // small optimization when we can used proposeUpdate object as is on cancellation
            finalException === proposedException -> proposedUpdate
            // cancelled job final state
            else -> CompletedExceptionally(finalException)
        }
        // Now handle the final exception
        if (finalException != null) {
        // 2 如果 finalException 不是 CancellationException，而且父协程且不为 SupervisorJob 和 
        // supervisorScope，cancelParent(finalException) 都返回 true
        // 也就是说一般情况下出现异常，会传递到最根部的协程，由最顶端的协程去处理
            val handled = cancelParent(finalException) || handleJobException(finalException)
            if (handled) (finalState as CompletedExceptionally).makeHandled()
        }
        // Process state updates for the final state before the state of the Job is actually set to the final state
        // to avoid races where outside observer may see the job in the final state, yet exception is not handled yet.
        if (!wasCancelling) onCancelling(finalException)
        onCompletionInternal(finalState)
        // Then CAS to completed state -> it must succeed
        val casSuccess = _state.compareAndSet(state, finalState.boxIncomplete())
        assert { casSuccess }
        // And process all post-completion actions
        completeStateFinalization(state, finalState)
        return finalState
    }
    
    private fun notifyCancelling(list: NodeList, cause: Throwable) {
        // first cancel our own children
        onCancelling(cause)
        // 1 取消子协程，异常传播到子协程
        notifyHandlers<JobCancellingNode>(list, cause)
        // then cancel parent
        // 2 可能取消父协程，异常可能传播到父协程。
        cancelParent(cause) // tentative cancellation -- does not matter if there is no parent
    }
    
    private fun cancelParent(cause: Throwable): Boolean {
        // Is scoped coroutine -- don't propagate, will be rethrown
        // 1 如果是协同作用域则异常会取消父协程。
        if (isScopedCoroutine) return true

        /* CancellationException is considered "normal" and parent usually is not cancelled when child produces it.
         * This allow parent to cancel its children (normally) without being cancelled itself, unless
         * child crashes and produce some other exception during its completion.
         */
         // 2 如果异常时CancellationException，不会取消父协程。
        val isCancellation = cause is CancellationException
        val parent = parentHandle
        // No parent -- ignore CE, report other exceptions.
        if (parent === null || parent === NonDisposableHandle) {
            return isCancellation
        }

        // Notify parent but don't forget to check cancellation
        // 3 parentHandle?.childCancelled(cause) 最后会通过调用 parentJob.childCancelled(cause) 
        // 取消父协程
        return parent.childCancelled(cause) || isCancellation
    }
    ...
    internal fun makeCompletingOnce(proposedUpdate: Any?): Any? {
        loopOnState { state ->
        // 1 调用tryMakeCompleting函数
            val finalState = tryMakeCompleting(state, proposedUpdate)
            when {
                finalState === COMPLETING_ALREADY ->
                    throw IllegalStateException(
                        "Job $this is already complete or completing, " +
                            "but is being completed with $proposedUpdate", proposedUpdate.exceptionOrNull
                    )
                finalState === COMPLETING_RETRY -> return@loopOnState
                else -> return finalState // COMPLETING_WAITING_CHILDREN or final state
            }
        }
    }
    
    private fun tryMakeCompleting(state: Any?, proposedUpdate: Any?): Any? {
        if (state !is Incomplete)
            return COMPLETING_ALREADY
        ...
        if ((state is Empty || state is JobNode) && state !is ChildHandleNode && proposedUpdate !is CompletedExceptionally) {
            if (tryFinalizeSimpleState(state, proposedUpdate)) {
                // Completed successfully on fast path -- return updated state
                return proposedUpdate
            }
            return COMPLETING_RETRY
        }
        // The separate slow-path function to simplify profiling
        // 1 调用tryMakeCompletingSlowPath函数。
        return tryMakeCompletingSlowPath(state, proposedUpdate)
    }
    
    private fun tryMakeCompletingSlowPath(state: Incomplete, proposedUpdate: Any?): Any? {
        val list = getOrPromoteCancellingList(state) ?: return COMPLETING_RETRY
        val finishing = state as? Finishing ?: Finishing(list, false, null)
        var notifyRootCause: Throwable? = null
        synchronized(finishing) {
            if (finishing.isCompleting) return COMPLETING_ALREADY
            finishing.isCompleting = true
            if (finishing !== state) {
                if (!_state.compareAndSet(state, finishing)) return COMPLETING_RETRY
            }
            assert { !finishing.isSealed } // cannot be sealed
            val wasCancelling = finishing.isCancelling
            (proposedUpdate as? CompletedExceptionally)?.let { finishing.addExceptionLocked(it.cause) }
            // 1 该情景下，notifyRootCause 的值为 exception
            notifyRootCause = finishing.rootCause.takeIf { !wasCancelling }
        }
        // 2 调用函数notifyCancelling
        notifyRootCause?.let { notifyCancelling(list, it) }
        // now wait for children
        val child = firstChild(state)
        if (child != null && tryWaitForChild(finishing, child, proposedUpdate))
            return COMPLETING_WAITING_CHILDREN
        // otherwise -- we have not children left (all were already cancelled?)
        // 3 调用函数finalizeFinishingState
        return finalizeFinishingState(finishing, proposedUpdate)
    }
    
    protected open fun handleJobException(exception: Throwable): Boolean = false
    ...
}
```
所以出现异常时，首先会取消自身协程和所有子协程，然后可能会取消父协程。而有些情况下并不会取消父协程，一是当异常
属于 CancellationException 时，二是使用SupervisorJob和supervisorScope时， 子协程出现未捕获异常时
也不会影响父协程，它们的原理是重写 childCancelled() 为
override fun childCancelled(cause: Throwable): Boolean = false。

launch式协程和async式协程都会自动向上传播异常，取消父协程。

协同作用域的子协程出现的异常，不仅会取消父协程，一步步取消到最根部的协程，而且最后还由最根部的协程（Root Coroutine）
处理协程。

对于launch函数创建的协程第一次包装是StandaloneCoroutine，对于async函数创建的协程第一层包装是AbstractCoroutine
Builders.common.kt
```K
private open class StandaloneCoroutine(
    parentContext: CoroutineContext,
    active: Boolean
) : AbstractCoroutine<Unit>(parentContext, initParentJob = true, active = active) {
    override fun handleJobException(exception: Throwable): Boolean {
        // 1 调用handleCoroutineException
        handleCoroutineException(context, exception)
        return true
    }
}
```
CoroutineExceptionHandler.kt
```K
internal actual val platformExceptionHandlers: Collection<CoroutineExceptionHandler> = ServiceLoader.load(
    CoroutineExceptionHandler::class.java,
    CoroutineExceptionHandler::class.java.classLoader
).iterator().asSequence().toList()

internal actual fun propagateExceptionFinalResort(exception: Throwable) {
    // use the thread's handler
    val currentThread = Thread.currentThread()
    currentThread.uncaughtExceptionHandler.uncaughtException(currentThread, exception)
}

public fun handleCoroutineException(context: CoroutineContext, exception: Throwable) {
    try {
       // 1 调用CoroutineExceptionHandler处理异常。
        context[CoroutineExceptionHandler]?.let {
            it.handleException(context, exception)
            return
        }
    } catch (t: Throwable) {
        handleUncaughtCoroutineException(context, handlerException(exception, t))
        return
    }
    // If a handler is not present in the context or an exception was thrown, fallback to the global handler
    // 2 如果没有CoroutineExceptionHandler，
    handleUncaughtCoroutineException(context, exception)
}

internal fun handleUncaughtCoroutineException(context: CoroutineContext, exception: Throwable) {
    // use additional extension handlers
    for (handler in platformExceptionHandlers) {
        try {
            handler.handleException(context, exception)
        } catch (_: ExceptionSuccessfullyProcessed) {
            return
        } catch (t: Throwable) {
            propagateExceptionFinalResort(handlerException(exception, t))
        }
    }

    try {
        exception.addSuppressed(DiagnosticCoroutineContextException(context))
    } catch (e: Throwable) {
        // addSuppressed is never user-defined and cannot normally throw with the only exception being OOM
        // we do ignore that just in case to definitely deliver the exception
    }
    // 触发线程的未捕获的异常
    propagateExceptionFinalResort(exception)
}
```
launch创建的协程出现的异常只是打印异常堆栈信息，如果在 Android 中还会调用uncaughtExceptionPreHandler处理异常。
但是如果使用了 CoroutineExceptionHandler 的话，使用自定义的 CoroutineExceptionHandler 处理异常。

而async创建的协程（root协程）第一层包装是AbstractCoroutine，并没有重写handleJobException函数，使得，
不会打印异常的堆栈信息，也不会通过CoroutineExceptionHandler处理。但是可以通过返回的Deferred调用await函数抛出
异常，然后用try-catch代码块处理异常。


### 19.9.6 小结
当抛出CancellationException或者调用cancel函数只会取消自身协程和子协程，不会取消父协程，也不会
打印异常信息。

抛出非CancellationException时会取消自身协程和子协程，也会取消父协程，一直取消root协程，异常
也由root协程处理。

如果使用了supervisorScope或者SupervisorJob，抛出非CancellationException时会取消自身协程
和子协程，但是不会取消父协程，不会取消其他子协程，异常由自身协程处理。

launch创建的协程出现的异常只是打印异常堆栈信息，如果在 Android 中还会调用uncaughtExceptionPreHandler处理异常。
但是如果使用了 CoroutineExceptionHandler 的话，使用自定义的 CoroutineExceptionHandler 处理异常。

而async创建的协程（root协程）第一层包装是AbstractCoroutine，并没有重写handleJobException函数，使得，
不会打印异常的堆栈信息，也不会通过CoroutineExceptionHandler处理。但是可以通过返回的Deferred调用await函数抛出
异常，然后用try-catch代码块处理异常。







