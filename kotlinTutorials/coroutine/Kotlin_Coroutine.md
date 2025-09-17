# 19 协程
2 https://johnnyshieh.me/posts/kotlin-coroutine-introduction/

1 [Kotlin Coroutines(协程) 完全解析（一），协程简介](https://www.jianshu.com/p/2659bbe0df16)

3 [Kotlin Coroutine github](https://github.com/Kotlin/kotlinx.coroutines)

4 [Kotlin Coroutines(协程) 完全解析（二），深入理解协程的挂起、恢复与调度](https://www.jianshu.com/p/2979732fb6fb)

5 [Kotlin Coroutines(协程) 完全解析（三），封装异步回调、协程间关系及协程的取消](https://www.jianshu.com/p/2857993af646)

6 [Kotlin Coroutines(协程) 完全解析（四），协程的异常处理](https://www.jianshu.com/p/20418eb50b17)

7 [Kotlin Coroutines(协程) 完全解析（五），协程的并发](https://www.jianshu.com/p/3a97d87683d5)

## 19.1 为什么需要协程？
协程可以是异步编程更简洁和可阅读。

异步编程中我们为了解决后一个任务和前一个任务的依赖关系，通常会使用到回调、Rx异步响应式框架。

比如以下的代码中涉及三个函数任务，后两个函数是依赖于第一个函数的返回结果，且三个函数都是耗时的，
运行在子线程中。

```k
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
异步编程中，回调解决两个函数的依赖关系是比较简洁实用的，但是三个以上的函数场景使用回调会出现多层嵌套的问题，而且不方便处理异常。
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
```kotlin
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

协程处理异步编程也非常的简洁，以顺序的方式书写异步代码，异常处理也很方便。
## 19.2 协程的概念
协程的开发人员 Roman Elizarov 是这样描述协程的：

> 协程就像非常轻量级的线程。线程是由系统调度的，线程切换或线程阻塞与恢复的开销都比较大。而协程依赖于线程，协程是由开发者控制，协程挂起时不需要阻塞线程。所以协程也像用户态的线程，非常轻量级，一个线程中可以创建任意个协程。

协程可以简化异步编程，可以顺序地表达程序，协程也提供了一种避免阻塞线程并用更轻量、更可控的操作替代线程阻塞的方法 -- 协程挂起。
### 19.2.1 挂起函数
`suspend` 修饰的函数就是挂起函数。挂起函数可能挂起协程（如果相关调用的结果已经可用，则库决定继续进行而不挂起），挂起函数挂起协程时，不会阻塞线程。挂起函数执行完成后会恢复协程，只能在协程中或其他挂起函数中调用挂起函数。

```K
fun postItem(item: Item) {
    GlobalScope.launch {
        val token = requestToken()
        val post = createPost(token, item)
        processPost(post)
        // 需要异常处理，直接加上 try/catch 语句即可
    }
```
上面的代码中，`GlobalScope.launch`就创建了一个协程。

`launch`函数:

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
`launch`函数定义中出现了一些重要的概念：`CoroutineScope`、`CoroutineContext`, `CoroutineStart`, `Job`等。

### 19.2.2 CoroutineScope
CoroutineScope是个接口，协程作用域。它只有一个成员变量`coroutineContext`，类型为`CoroutineContext`。
### 19.2.3 CoroutineContext
`CoroutineContext`是一个接口，协程的上下文。它是集合，集合中的元素是[Key, Element]键值对。定义了增`plus`、删`minusKey`、查`get`等方法。而`Key`是一个泛型接口，泛型类型为`Element`。`Element`是接口，继承`CoroutineContext`。协程上下文至少包含`Job`元素，以强制结构化并发。

### 19.2.3.1 Key

```kotlin
    public interface Key<E : Element>
```

是一个泛型接口，泛型类型为`Element`。

#### 19.2.3.2 Element

```kotlin
    public interface Element : CoroutineContext {
        /**
         * A key of this coroutine context element.
         */
        public val key: Key<*>

        public override operator fun <E : Element> get(key: Key<E>): E? =
            @Suppress("UNCHECKED_CAST")
            if (this.key == key) this as E else null

        public override fun <R> fold(initial: R, operation: (R, Element) -> R): R =
            operation(initial, this)

        public override fun minusKey(key: Key<*>): CoroutineContext =
            if (this.key == key) EmptyCoroutineContext else this
    }
```

是一个接口，继承了`CoroutineContext`。是协程上下文`CoroutineContext`集合中的键值对元素的值类型。

#### 19.2.3.1 `plus(context: CoroutineContext)`

```kotlin
public interface CoroutineContext {
    ...
    /**
     * Returns a context containing elements from this context and elements from  other [context].
     * The elements from this context with the same key as in the other one are dropped.
     */
    public operator fun plus(context: CoroutineContext): CoroutineContext =
        if (context === EmptyCoroutineContext) this else // fast path -- avoid lambda creation
            context.fold(this) { acc, element ->
                val removed = acc.minusKey(element.key)
                if (removed === EmptyCoroutineContext) element else {
                    // make sure interceptor is always last in the context (and thus is fast to get when present)
                    val interceptor = removed[ContinuationInterceptor]
                    if (interceptor == null) CombinedContext(removed, element) else {
                        val left = removed.minusKey(ContinuationInterceptor)
                        if (left === EmptyCoroutineContext) CombinedContext(element, interceptor) else
                            CombinedContext(CombinedContext(left, element), interceptor)
                    }
                }
            }
    ...
}
```

`plus`方法，把两个`CoroutineContext`合并成一个`CoroutineContext`。

该方法的主要实现步骤如下：

判断`context`是否是`EmptyCoroutineContext`？

+ 是，则返回this。
+ 否。遍历`context`中的`Element`，调用`minusKey`方法移除当前`CoroutineContext`中与`element`相同`key`的元素；判断移除操作后的结果`removed`是否是`EmptyCoroutineContext`？
  + 是，则返回指定的`element`。
  + 否，将`removed`和指定的`element`合并为`CombinedCoroutineContext`。然后判断是否有`ContinuationInterceptor`元素？如果有则提取出来，放到合并操作结果的尾部。

#### 19.2.3.2 EmptyCoroutineContext

```kotlin
public object EmptyCoroutineContext : CoroutineContext, Serializable {
    private const val serialVersionUID: Long = 0
    private fun readResolve(): Any = EmptyCoroutineContext

    public override fun <E : Element> get(key: Key<E>): E? = null
    public override fun <R> fold(initial: R, operation: (R, Element) -> R): R = initial
    public override fun plus(context: CoroutineContext): CoroutineContext = context
    public override fun minusKey(key: Key<*>): CoroutineContext = this
    public override fun hashCode(): Int = 0
    public override fun toString(): String = "EmptyCoroutineContext"
}
```

`EmptyCoroutineContext`是个对象，对象声明，实现了`CoroutineContext`，空集合。

#### 19.2.3.3 CombinedContext

```kotlin
internal class CombinedContext(
    private val left: CoroutineContext,
    private val element: Element
) : CoroutineContext, Serializable {

    override fun <E : Element> get(key: Key<E>): E? {
        var cur = this
        while (true) {
            cur.element[key]?.let { return it }
            val next = cur.left
            if (next is CombinedContext) {
                cur = next
            } else {
                return next[key]
            }
        }
    }

    public override fun <R> fold(initial: R, operation: (R, Element) -> R): R =
        operation(left.fold(initial, operation), element)

    public override fun minusKey(key: Key<*>): CoroutineContext {
        element[key]?.let { return left }
        val newLeft = left.minusKey(key)
        return when {
            newLeft === left -> this
            newLeft === EmptyCoroutineContext -> element
            else -> CombinedContext(newLeft, element)
        }
    }

    private fun size(): Int {
        var cur = this
        var size = 2
        while (true) {
            cur = cur.left as? CombinedContext ?: return size
            size++
        }
    }

    private fun contains(element: Element): Boolean =
        get(element.key) == element

    private fun containsAll(context: CombinedContext): Boolean {
        var cur = context
        while (true) {
            if (!contains(cur.element)) return false
            val next = cur.left
            if (next is CombinedContext) {
                cur = next
            } else {
                return contains(next as Element)
            }
        }
    }

    override fun equals(other: Any?): Boolean =
        this === other || other is CombinedContext && other.size() == size() && other.containsAll(this)

    override fun hashCode(): Int = left.hashCode() + element.hashCode()

    override fun toString(): String =
        "[" + fold("") { acc, element ->
            if (acc.isEmpty()) element.toString() else "$acc, $element"
        } + "]"

    private fun writeReplace(): Any {
        val n = size()
        val elements = arrayOfNulls<CoroutineContext>(n)
        var index = 0
        fold(Unit) { _, element -> elements[index++] = element }
        check(index == n)
        @Suppress("UNCHECKED_CAST")
        return Serialized(elements as Array<CoroutineContext>)
    }

    private class Serialized(val elements: Array<CoroutineContext>) : Serializable {
        companion object {
            private const val serialVersionUID: Long = 0L
        }

        private fun readResolve(): Any = elements.fold(EmptyCoroutineContext, CoroutineContext::plus)
    }
}
```

`CombinedContext`实现了`CoroutineContext`接口。

### 19.2.4 CoroutineStart
是一个枚举类，定义了协程的启动选项。启动类型有`DEFAULT`, `LAZILY`, `ATOMIC`, `UNDISPATCHED`四种。

### 19.2.5 Continuation

kotlin-stdlib-1.8.20\kotlin\coroutines\Continuation.kt

```kotlin
public interface Continuation<in T> {
    /**
     * The context of the coroutine that corresponds to this continuation.
     */
    public val context: CoroutineContext

    /**
     * Resumes the execution of the corresponding coroutine passing a successful or failed [result] as the
     * return value of the last suspension point.
     */
    public fun resumeWith(result: Result<T>)
}
```

`Continuation`是一个接口，代表协程在挂起后协程恢复待执行的部分，定义了`resumeWith`方法，接收`Result`参数，该参数封装了协程的执行结果，结果有成功和失败两个状态。

### 19.2.6 Job

`Job`是一个接口，继承了`CoroutineContext.Element`。它是一个具有声明周期的后台任务，`Job`没有对应任务的结果。

**生命周期中的状态**

有六种状态，分别是`New`, `Active`, `Completing`, `Cancelling`, `Cancelled`, `Completed`。Cancelled也是一种完成状态。

                                          wait children
    +-----+ start  +--------+ complete   +-------------+  finish  +-----------+
    | New | -----> | Active | ---------> | Completing  | -------> | Completed |
    +-----+        +--------+            +-------------+          +-----------+
                     |  cancel / fail       |
                     |     +----------------+
                     |     |
                     V     V
                 +------------+                           finish  +-----------+
                 | Cancelling | --------------------------------> | Cancelled |
                 +------------+                                   +-----------+

当一个活跃协程的函数体执行完毕，或调用了 `CompletableJob.complete` 时，该 `Job` 会转换到**完成中**（completing）的状态。在这个状态下，它会等待其所有子协程都完成后，才会转换到**已完成**（completed）状态。请注意，**完成中**状态纯粹是 `Job` 的一个内部状态。对于外部观察者来说，一个处于“完成中”的 `Job` 仍然是活跃的，尽管它在内部正在等待其子协程结束。

**异常完成与取消原因**

当协程体抛出异常时，这个协程的`Job`被认为是**异常完成**，该`Job`会被取消，对应的异常就是该`Job`的取消原因。

如果协程体抛出的异常属于`CancellationException`，该`Job`被认为是**正常取消**；否则被认为是**失败**。

**Job 取消在父子Job层级中的传递**

`Job`可以被组织成父子层级结构，父`Job`的取消会导致所有的子`Job`取消；子`Job`如果因抛出了非`CancellationException`异常而被取消（失败），也会导致父`Job`被取消，进而导致其他子`Job`被取消。

**并发与同步**

`Job`接口及其派生接口中定义的函数都是线程安全的。

**源码**

```kotlin

public interface Job : CoroutineContext.Element {
    ...
        /**
     * Returns the parent of the current job if the parent-child relationship
     * is established or `null` if the job has no parent or was successfully completed.
     *
     * Accesses to this property are not idempotent, the property becomes `null` as soon
     * as the job is transitioned to its final state, whether it is cancelled or completed,
     * and all job children are completed.
     *
     * For a coroutine, its corresponding job completes as soon as the coroutine itself
     * and all its children are complete.
     *
     * @see [Job] state transitions for additional details.
     */
    @ExperimentalCoroutinesApi
    public val parent: Job?
        /**
     * Returns `true` when this job is active -- it was already started and has not completed nor was cancelled yet.
     * The job that is waiting for its [children] to complete is still considered to be active if it
     * was not cancelled nor failed.
     *
     * See [Job] documentation for more details on job states.
     */
    public val isActive: Boolean

    /**
     * Returns `true` when this job has completed for any reason. A job that was cancelled or failed
     * and has finished its execution is also considered complete. Job becomes complete only after
     * all its [children] complete.
     *
     * See [Job] documentation for more details on job states.
     */
    public val isCompleted: Boolean

    /**
     * Returns `true` if this job was cancelled for any reason, either by explicit invocation of [cancel] or
     * because it had failed or its child or parent was cancelled.
     * In the general case, it does not imply that the
     * job has already [completed][isCompleted], because it may still be finishing whatever it was doing and
     * waiting for its [children] to complete.
     *
     * See [Job] documentation for more details on cancellation and failures.
     */
    public val isCancelled: Boolean
    
    ...
    /**
     * Starts coroutine related to this job (if any) if it was not started yet.
     * The result is `true` if this invocation actually started coroutine or `false`
     * if it was already started or completed.
     */
    public fun start(): Boolean


    /**
     * Cancels this job with an optional cancellation [cause].
     * A cause can be used to specify an error message or to provide other details on
     * the cancellation reason for debugging purposes.
     * See [Job] documentation for full explanation of cancellation machinery.
     */
    public fun cancel(cause: CancellationException? = null)
    
        /**
     * Returns a sequence of this job's children.
     *
     * A job becomes a child of this job when it is constructed with this job in its
     * [CoroutineContext] or using an explicit `parent` parameter.
     *
     * A parent-child relation has the following effect:
     *
     * * Cancellation of parent with [cancel] or its exceptional completion (failure)
     *   immediately cancels all its children.
     * * Parent cannot complete until all its children are complete. Parent waits for all its children to
     *   complete in _completing_ or _cancelling_ state.
     * * Uncaught exception in a child, by default, cancels parent. This applies even to
     *   children created with [async][CoroutineScope.async] and other future-like
     *   coroutine builders, even though their exceptions are caught and are encapsulated in their result.
     *   This default behavior can be overridden with [SupervisorJob].
     */
    public val children: Sequence<Job>
    
    
        /**
     * Attaches child job so that this job becomes its parent and
     * returns a handle that should be used to detach it.
     *
     * A parent-child relation has the following effect:
     * * Cancellation of parent with [cancel] or its exceptional completion (failure)
     *   immediately cancels all its children.
     * * Parent cannot complete until all its children are complete. Parent waits for all its children to
     *   complete in _completing_ or _cancelling_ states.
     *
     * **A child must store the resulting [ChildHandle] and [dispose][DisposableHandle.dispose] the attachment
     * to its parent on its own completion.**
     *
     * Coroutine builders and job factory functions that accept `parent` [CoroutineContext] parameter
     * lookup a [Job] instance in the parent context and use this function to attach themselves as a child.
     * They also store a reference to the resulting [ChildHandle] and dispose a handle when they complete.
     *
     * @suppress This is an internal API. This method is too error prone for public API.
     */
    // ChildJob and ChildHandle are made internal on purpose to further deter 3rd-party impl of Job
    @InternalCoroutinesApi
    public fun attachChild(child: ChildJob): ChildHandle
    
    ...
    /**
     * Suspends the coroutine until this job is complete. This invocation resumes normally (without exception)
     * when the job is complete for any reason and the [Job] of the invoking coroutine is still [active][isActive].
     * This function also [starts][Job.start] the corresponding coroutine if the [Job] was still in _new_ state.
     *
     * Note that the job becomes complete only when all its children are complete.
     *
     * This suspending function is cancellable and **always** checks for a cancellation of the invoking coroutine's Job.
     * If the [Job] of the invoking coroutine is cancelled or completed when this
     * suspending function is invoked or while it is suspended, this function
     * throws [CancellationException].
     *
     * In particular, it means that a parent coroutine invoking `join` on a child coroutine throws
     * [CancellationException] if the child had failed, since a failure of a child coroutine cancels parent by default,
     * unless the child was launched from within [supervisorScope].
     *
     * This function can be used in [select] invocation with [onJoin] clause.
     * Use [isCompleted] to check for a completion of this job without waiting.
     *
     * There is [cancelAndJoin] function that combines an invocation of [cancel] and `join`.
     */
    public suspend fun join()
    ...
}

```

#### 19.2.5.1 获取Job的运行状态

可以通过`isActive`、`isCompleted`、`isCancelled`来获取`Job`的运行状态。

#### 19.2.5.2 获取parent Job

通过成员变量`parent`获取指定`Job`的父`Job`。

+ 如果当前`Job`建立了父子关系，则返回父`Job`。
+ 如果当前`Job`没有父`Job`则返回null。
+ 如果当前`Job`有父`Job`但是已完成或者取消或者失败，则为null。

#### 19.2.5.3 获取 child Job

通过成员变量`children`可以获取当前`Job`的子`Job`序列。

`Job` A在以下情况会称为`Job` B的子`Job`：

+ `Job`A在构造的时候，指定的`CoroutineContext`参数持有`Job` B 的 `CoroutineContext`。
+ 指定了`parent`成员变量。

#### 19.2.5.4 `attachChild(child: ChildJob)`

`attachChild`方法，将指定的`Job`关联到当前`Job`，创建两个`Job`的父子关系，返回一个`ChildHandle`。

#### 19.2.5.4 启动Job

调用`start`方法启动`Job`。

#### 19.2.5.5 取消`Job`

调用`cancel(cause: CancellationException? = null)`可以取消`Job`。

#### 19.2.5.6 `join()`

`join`方法是一个挂起函数，它会挂起当前协程，直到指定的`Job`完成为止。

+ 如果指定的`Job`是`NEW`则会启动这个`Job`，转换为`ACTIVE`。
+ 一个`Job`只有在它的所有子`Job`完成后，才算是完成。所以`join`方法挂起当前协程，直到指定`Job`的所有子`Job`完成为止。
+ 该方法会持续检查当前被挂起协程是否被取消，如果当前协程被取消，该方法会抛出`CancellationException`。
+ 该方法不关心指定`Job`是如何完成的。指定`Job`成功完成、被取消或者失败，当前协程都会正常恢复。该方法不会抛出指定`Job`的异常。

### 19.2.6 ParentJob

是一个接口，继承`Job`。

```kotlin
public interface ParentJob : Job {
    /**
     * Child job is using this method to learn its cancellation cause when the parent cancels it with [ChildJob.parentCancelled].
     * This method is invoked only if the child was not already being cancelled.
     *
     * Note that [CancellationException] is the method's return type: if child is cancelled by its parent,
     * then the original exception is **already** handled by either the parent or the original source of failure.
     *
     * @suppress **This is unstable API and it is subject to change.**
     */
    @InternalCoroutinesApi
    public fun getChildJobCancellationCause(): CancellationException
}
```

父`Job`通知子`Job`取消请求时，子`Job`可以通过这个接口调用方法`getChildJobCancellationCause()`来获取取消请求的信息。

### 19.2.7 ChildJob

是一个接口，继承`Job`。父`Job`通知子`Job`取消时，会调用这个接口的`parentCancelled(parentJob: ParentJob)`方法。

```kotlin
public interface ChildJob : Job {
    /**
     * Parent is cancelling its child by invoking this method.
     * Child finds the cancellation cause using [ParentJob.getChildJobCancellationCause].
     * This method does nothing is the child is already being cancelled.
     *
     * @suppress **This is unstable API and it is subject to change.**
     */
    @InternalCoroutinesApi
    public fun parentCancelled(parentJob: ParentJob)
}
```

### 19.2.8 JobSupport

实现`Job`接口的类，同时实现了`ParentJob`、`ChildJob`接口。构造器函数只有一个参数，`active`属于`Boolean`类型，`true`表明创建的`Job`状态就是`ACTIVIE`，否则就是`NEW`。



### 19.2.9 DisposableHandle

是一个函数式接口，已分配对象的句柄（引用），即可释放资源的句柄，通过调用`dispose`方法可以释放它，指定的对象就不再被引用，系统的垃圾回收器就可以回收对象所占用的内存。

```kotlin
/**
 * A handle to an allocated object that can be disposed to make it eligible for garbage collection.
 */
public fun interface DisposableHandle {
    /**
     * Disposes the corresponding object, making it eligible for garbage collection.
     * Repeated invocation of this function has no effect.
     */
    public fun dispose()
}
```

### 19.2.10 ChildHandle

是一个接口，继承`DisposableHandle`，是一个子`Job`对父`Job`引用的句柄，主要用于子`Job`向父`Job`报告取消状态，从而实现**取消传播机制**。通过成员变量`parent`获取父`Job`，动过调用`childCancelled(cause: Throwable)`用来通知父`Job`。



### 19.2.11 AbstractCoroutine

是个抽象类，继承`JobSupport`、实现`Continuation`、`CoroutineScope`接口。



### 19.2.12 ContinuationInterceptor

是一个接口，继承`CoroutineContext.Element`，用来拦截协程的`Continuation`。



### 19.2.13 CoroutineDispatcher
抽象类，实现`ContinuationInterceptor`接口，协程调度器，也叫做协程的线程调度器，决定协程所在的线程，`kotlinx-coroutines-core`中的对象声明`Dispatchers`内置了四种协程调度器，分别是`Default`, `Main`, `UnConfined`，`IO`。

`CoroutineDispatcher`也是协程启动过程中的拦截器，定义了`interceptContinuation`方法，返回的`Continuation`用来实现拦截功能。

### 19.2.14 ExecutorCoroutineDispatcher

是个抽象类，继承`CoroutineDispatcher`，实现了`Closeable`接口。持有一个`Executor`线程池负责分发任务。

### 19.2.15 SchedulerCoroutineDispatcher

继承`ExecutorCoroutineDispatcher`，构造器中接受四个参数，`corePollSize`、`maxPoolSize`、`idleWorkerKeepAliveNs`、`schedulerName`。对应的`Executor`实例是`CoroutineScheduler`。

### 19.2.16 CoroutineScheduler

实现`Executor、Closeable`接口的线程池，指定了`corePoolSize`、`maxPoolSize`、`idleWorkerKeepAliveNs`、`schedulerName`。

### 19.2.17 DefaultScheduler

对象声明，继承`SchedulerCoroutineDispatcher`，是`Dispatcher.Default`的默认值。

### 19.2.18 MainCoroutineDispatcher

是个抽象类，继承`CoroutineDispather`，指定应用的主线程，主要用于执行基于`UI`的任务。可以通过`Dispatchers.Main`来获取`MainCoroutineDispatcher`的实例。定义的成员变量`immediate`是`MainCoroutineDispathcer`类型。

### 19.2.19 UnConfined

是一个对象声明，继承`CoroutineDispatcher`，不指定线程。

### 19.2.20 DefaultIoScheduler

是一个对象声明，继承`ExecutorCoroutineDispatcher`，是`Dispatchers.IO`的默认值。

### 19.2.21 Dispatchers

`kotlinx-coroutine-core`库中默认的协程调度器配置在对象`Dispatchers`中。

#### 19.2.21.1 Default

默认值为`DefaultScheduler`，内部维护了一个线程池，该线程池核心线程数是设备的cpu数目（最小为2），最大线程数为2的21次方-2个，`keepAliveTime`为60s。

#### 19.2.21.2 IO
默认值为`DefaultIoScheduler`，协程执行的任务适合IO密集型的任务。
#### 19.2.21.3 Main
默认值类型为`MainCoroutineDispatcher`。协程运行在主线程中。
#### 19.2.21.4 Unconfined
默认值为`UnConfined`，没有指定协程所在的线程，默认就是上游的线程。在协程的构建器中不能指定该种类型CoroutineDispatcher。

### 19.2.22 EventLoop

抽象类，继承`CoroutineDispatcher`，有事件循环能力，可以处理事件队列里的任务。

### 19.2.23 GlobleScope

```kotlin
@DelicateCoroutinesApi
public object GlobalScope : CoroutineScope {
    /**
     * Returns [EmptyCoroutineContext].
     */
    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext
}
```

对象声明，实现`CoroutineScipe`，成员变量`coroutineConxtext`是`EmptyCoroutineContext`。

### 19.2.24 BlockingCoroutine

继承`AbstractCoroutine`，在协程构建器`runBlocking`方法中会构造`BlockingCoroutine`对象，并启动协程，同时会阻塞调用`runBlocking`方法所在的线程，直到协程完成为止。

### 19.2.25  Deferred

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\Deferred.kt

```kotlin
public interface Deferred<out T> : Job {

    /**
     * Awaits for completion of this value without blocking a thread and resumes when deferred computation is complete,
     * returning the resulting value or throwing the corresponding exception if the deferred was cancelled.
     *
     * This suspending function is cancellable.
     * If the [Job] of the current coroutine is cancelled or completed while this suspending function is waiting, this function
     * immediately resumes with [CancellationException].
     * There is a **prompt cancellation guarantee**. If the job was cancelled while this function was
     * suspended, it will not resume successfully. See [suspendCancellableCoroutine] documentation for low-level details.
     *
     * This function can be used in [select] invocation with [onAwait] clause.
     * Use [isCompleted] to check for completion of this deferred value without waiting.
     */
    public suspend fun await(): T
    ...
}
```

是一个接口，继承`Job`，它的完成是有结果的。

可以通过协程构建器`CoroutineScope.asyn`方法构建`Deferred`对象，或者通过它的派生类`CompletableDeferredImpl`的构造方法构建`Deferred`对象。

`Deferred`和`Job`一样有相同的生命周期。

可以通过`await`方法获取结果。

#### 19.2.25.1 await

+ 是个挂起函数，挂起当前协程直到`Deferred`完成，该函数返回`Deferred`后台任务的结果。

+ 如果`Deferred`被取消了，该函数会抛出相应的异常。
+ 如果当前协程在调用该方法而挂起，在这期间，当前协程的`Job`被取消，这该函数会立即抛出`CancellationException`返回。

#### 19.2.25.2 getCompletionExceptionOrNull

如果此 `Deferred` **已被取消且已完成**，则返回其取消完成时的**异常结果**；如果它**正常完成**，则返回 **`null`**；如果此 `Deferred` **尚未完成**，则会**抛出 `IllegalStateException`**。

## 19.3 Coroutine builders
创建协程的方法。

### 19.3.2 `runBlocking`

创建一个协程，对应的`Job`为`BlockingCoroutine`并启动，阻塞当前线程，直到协程完成。是为main函数和测试中使用协程设计的。不能在协程体中调用该方法。如果当前线程被中断，则抛出`InterruptedException`，并取消协程的`Job`。

### 19.3.1 `CoroutineScope.launch`

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\Builds.common.kt

```kotlin
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

private open class StandaloneCoroutine(
    parentContext: CoroutineContext,
    active: Boolean
) : AbstractCoroutine<Unit>(parentContext, initParentJob = true, active = active) {
    override fun handleJobException(exception: Throwable): Boolean {
        handleCoroutineException(context, exception)
        return true
    }
}

private class LazyStandaloneCoroutine(
    parentContext: CoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) : StandaloneCoroutine(parentContext, active = false) {
    private val continuation = block.createCoroutineUnintercepted(this, this)

    override fun onStart() {
        continuation.startCoroutineCancellable(this)
    }
}



```

kotlinx-coroutines-core-jvm-1.7.3-sources\jvmMain\CoroutineContext.kt

```kotlin
@ExperimentalCoroutinesApi
public actual fun CoroutineScope.newCoroutineContext(context: CoroutineContext): CoroutineContext {
    val combined = foldCopies(coroutineContext, context, true)
    val debug = if (DEBUG) combined + CoroutineId(COROUTINE_ID.incrementAndGet()) else combined
    return if (combined !== Dispatchers.Default && combined[ContinuationInterceptor] == null)
        debug + Dispatchers.Default else debug
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\kotlinx\coroutines\AbstractCoroutine.kt

```kotlin
@InternalCoroutinesApi
public abstract class AbstractCoroutine<in T>(
    parentContext: CoroutineContext,
    initParentJob: Boolean,
    active: Boolean
) : JobSupport(active), Job, Continuation<T>, CoroutineScope {

    /**
     * Starts this coroutine with the given code [block] and [start] strategy.
     * This function shall be invoked at most once on this coroutine.
     * 
     * * [DEFAULT] uses [startCoroutineCancellable].
     * * [ATOMIC] uses [startCoroutine].
     * * [UNDISPATCHED] uses [startCoroutineUndispatched].
     * * [LAZY] does nothing.
     */
    public fun <R> start(start: CoroutineStart, receiver: R, block: suspend R.() -> T) {
        start(block, receiver, this)
    }
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\CoroutineStart.kt

```kotlin
public enum class CoroutineStart {
    ...
    /**
     * Starts the corresponding block with receiver as a coroutine with this coroutine start strategy.
     *
     * * [DEFAULT] uses [startCoroutineCancellable].
     * * [ATOMIC] uses [startCoroutine].
     * * [UNDISPATCHED] uses [startCoroutineUndispatched].
     * * [LAZY] does nothing.
     *
     * @suppress **This an internal API and should not be used from general code.**
     */
    @InternalCoroutinesApi
    public operator fun <R, T> invoke(block: suspend R.() -> T, receiver: R, completion: Continuation<T>): Unit =
        when (this) {
            DEFAULT -> block.startCoroutineCancellable(receiver, completion)
            ATOMIC -> block.startCoroutine(receiver, completion)
            UNDISPATCHED -> block.startCoroutineUndispatched(receiver, completion)
            LAZY -> Unit // will start lazily
        }
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\intrinsics\Cancellable.kt

```kotlin
/**
 * Use this function to start coroutine in a cancellable way, so that it can be cancelled
 * while waiting to be dispatched.
 */
internal fun <R, T> (suspend (R) -> T).startCoroutineCancellable(
    receiver: R, completion: Continuation<T>,
    onCancellation: ((cause: Throwable) -> Unit)? = null
) =
    runSafely(completion) {
        createCoroutineUnintercepted(receiver, completion).intercepted().resumeCancellableWith(Result.success(Unit), onCancellation)
    }

...
private inline fun runSafely(completion: Continuation<*>, block: () -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        dispatcherFailure(completion, e)
    }
}

private fun dispatcherFailure(completion: Continuation<*>, e: Throwable) {
    /*
     * This method is invoked when we failed to start a coroutine due to the throwing
     * dispatcher implementation or missing Dispatchers.Main.
     * This situation is not recoverable, so we are trying to deliver the exception by all means:
     * 1) Resume the coroutine with an exception, so it won't prevent its parent from completion
     * 2) Rethrow the exception immediately, so it will crash the caller (e.g. when the coroutine had
     *    no parent or it was async/produce over MainScope).
     */
    completion.resumeWith(Result.failure(e))
    throw e
}
```

kotlin-stdlib-1.8.20\kotlin\coroutines\intrinsics\intrinsicsJvm.kt

```kotlin
@SinceKotlin("1.3")
public actual fun <T> (suspend () -> T).createCoroutineUnintercepted(
    completion: Continuation<T>
): Continuation<Unit> {
    val probeCompletion = probeCoroutineCreated(completion)
    return if (this is BaseContinuationImpl)
        create(probeCompletion)
    else
        createCoroutineFromSuspendFunction(probeCompletion) {
            (this as Function1<Continuation<T>, Any?>).invoke(it)
        }
}
```

kotlin-stdlib-1.8.20\kotlin\coroutines\jvm\internal\DebugProbes.kt

```kotlin
@SinceKotlin("1.3")
internal fun <T> probeCoroutineCreated(completion: Continuation<T>): Continuation<T> {
    /** implementation of this function is replaced by debugger */
    return completion
}
```



`CoroutineScope`的扩展方法，该方法的主要实现步骤如下：

+ 创建一个协程并启动，返回`Job`后台任务。
+ 如果函数的接收者对象`CoroutineScope`的`coroutineContext`上下文和参数`context`上下文中没有`ContinuationInterceptor`元素则添加`Dispatchers.Default`，添加后的结果`CoroutineContext`，作为构建`Job`用的`context`。
+ 如果`start`参数是`CoroutineStart.LAZY`，构建的`Job`是`LazyStandaloneCoroutine`，否则是`StandaloneCoroutine`。

### 19.3.3 `withContext`

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\Builds.common.kt

```kotlin
/**
 * Calls the specified suspending block with a given coroutine context, suspends until it completes, and returns
 * the result.
 *
 * The resulting context for the [block] is derived by merging the current [coroutineContext] with the
 * specified [context] using `coroutineContext + context` (see [CoroutineContext.plus]).
 * This suspending function is cancellable. It immediately checks for cancellation of
 * the resulting context and throws [CancellationException] if it is not [active][CoroutineContext.isActive].
 *
 * Calls to [withContext] whose [context] argument provides a [CoroutineDispatcher] that is
 * different from the current one, by necessity, perform additional dispatches: the [block]
 * can not be executed immediately and needs to be dispatched for execution on
 * the passed [CoroutineDispatcher], and then when the [block] completes, the execution
 * has to shift back to the original dispatcher.
 *
 * Note that the result of `withContext` invocation is dispatched into the original context in a cancellable way
 * with a **prompt cancellation guarantee**, which means that if the original [coroutineContext]
 * in which `withContext` was invoked is cancelled by the time its dispatcher starts to execute the code,
 * it discards the result of `withContext` and throws [CancellationException].
 *
 * The cancellation behaviour described above is enabled if and only if the dispatcher is being changed.
 * For example, when using `withContext(NonCancellable) { ... }` there is no change in dispatcher and
 * this call will not be cancelled neither on entry to the block inside `withContext` nor on exit from it.
 */
public suspend fun <T> withContext(
    context: CoroutineContext,
    block: suspend CoroutineScope.() -> T
): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return suspendCoroutineUninterceptedOrReturn sc@ { uCont ->
        // compute new context
        val oldContext = uCont.context
        // Copy CopyableThreadContextElement if necessary
        val newContext = oldContext.newCoroutineContext(context)
        // always check for cancellation of new context
        newContext.ensureActive()
        // FAST PATH #1 -- new context is the same as the old one
        if (newContext === oldContext) {
            val coroutine = ScopeCoroutine(newContext, uCont)
            return@sc coroutine.startUndispatchedOrReturn(coroutine, block)
        }
        // FAST PATH #2 -- the new dispatcher is the same as the old one (something else changed)
        // `equals` is used by design (see equals implementation is wrapper context like ExecutorCoroutineDispatcher)
        if (newContext[ContinuationInterceptor] == oldContext[ContinuationInterceptor]) {
            val coroutine = UndispatchedCoroutine(newContext, uCont)
            // There are changes in the context, so this thread needs to be updated
            withCoroutineContext(coroutine.context, null) {
                return@sc coroutine.startUndispatchedOrReturn(coroutine, block)
            }
        }
        // SLOW PATH -- use new dispatcher
        val coroutine = DispatchedCoroutine(newContext, uCont)
        block.startCoroutineCancellable(coroutine, coroutine)
        coroutine.getResult()
    }
}
```

+ 是一个挂起函数，接收两个参数，`context: CoroutineContext`和`block: suspend R.() -> T`。

+ 不会创建协程，在指定的协程上运行挂起代码块`block`，挂起协程直到代码块`block`运行完成。
+ 代码块最终的上下文是通过`coroutineContext + context`的方式合并而成的。
+ 当 `withContext` 的 `context` 参数提供了一个与当前不同的 `CoroutineDispatcher` 时，代码块无法立即执行，需要被分发到传入的 `CoroutineDispatcher` 上去执行，然后当代码块执行完毕后，执行权必须**切回到原来的调度器**上。
+ 在 `withContext` 的结果准备返回原始调度器时，调用它的那个协程已经被取消，那么 `withContext` 的结果会被丢弃，并抛出 `CancellationException`。这个取消行为**当且仅当调度器发生变更时才会启用**。例如，当使用 `withContext(NonCancellable) { ... }` 时，由于没有发生调度器变更，因此无论是在进入 `withContext` 的代码块时，还是在退出时，这个调用都不会被取消。

### 19.3.4 `CoroutineScope.async`

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\Builds.common.kt

```kotlin
/**
 * Creates a coroutine and returns its future result as an implementation of [Deferred].
 * The running coroutine is cancelled when the resulting deferred is [cancelled][Job.cancel].
 * The resulting coroutine has a key difference compared with similar primitives in other languages
 * and frameworks: it cancels the parent job (or outer scope) on failure to enforce *structured concurrency* paradigm.
 * To change that behaviour, supervising parent ([SupervisorJob] or [supervisorScope]) can be used.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [context] argument.
 * If the context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * The parent job is inherited from a [CoroutineScope] as well, but it can also be overridden
 * with corresponding [context] element.
 *
 * By default, the coroutine is immediately scheduled for execution.
 * Other options can be specified via `start` parameter. See [CoroutineStart] for details.
 * An optional [start] parameter can be set to [CoroutineStart.LAZY] to start coroutine _lazily_. In this case,
 * the resulting [Deferred] is created in _new_ state. It can be explicitly started with [start][Job.start]
 * function and will be started implicitly on the first invocation of [join][Job.join], [await][Deferred.await] or [awaitAll].
 *
 * @param block the coroutine code.
 */
public fun <T> CoroutineScope.async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> {
    val newContext = newCoroutineContext(context)
    val coroutine = if (start.isLazy)
        LazyDeferredCoroutine(newContext, block) else
        DeferredCoroutine<T>(newContext, active = true)
    coroutine.start(start, coroutine, block)
    return coroutine
}

@Suppress("UNCHECKED_CAST")
private open class DeferredCoroutine<T>(
    parentContext: CoroutineContext,
    active: Boolean
) : AbstractCoroutine<T>(parentContext, true, active = active), Deferred<T> {
    override fun getCompleted(): T = getCompletedInternal() as T
    override suspend fun await(): T = awaitInternal() as T
    override val onAwait: SelectClause1<T> get() = onAwaitInternal as SelectClause1<T>
}

private class LazyDeferredCoroutine<T>(
    parentContext: CoroutineContext,
    block: suspend CoroutineScope.() -> T
) : DeferredCoroutine<T>(parentContext, active = false) {
    private val continuation = block.createCoroutineUnintercepted(this, this)

    override fun onStart() {
        continuation.startCoroutineCancellable(this)
    }
}
```

+ 创建一个协程并启动，返回`Deferred`后台任务。
+ 如果函数的接收者对象`CoroutineScope`的`coroutineContext`上下文和参数`context`上下文中没有`ContinuationInterceptor`元素，则添加`Dispatchers.Default`，添加后的结果`CoroutineContext`，作为构建`Deferred`用的`context`。
+ 如果`start`参数是`CoroutineStart.LAZY`，返回的是`LazyDeferredCoroutine`，否则返回`DeferredCoroutine`。

## 19.4 挂起函数的工作原理

[Kotlin Coroutines(协程) 完全解析（二），深入理解协程的挂起、恢复与调度](https://www.jianshu.com/p/2979732fb6fb)

协程的内部使用了Kotlin编译器的一些编译技术，当调用挂起函数时，都有一个隐式的对象传入，该对象是`Continuation`，它封装了协程挂起后协程恢复待执行的部分。

### 19.4.1 Continuation

kotlin-stdlib-1.8.20\kotlin\coroutines\Continuation.kt

```kotlin
public interface Continuation<in T> {
    /**
     * The context of the coroutine that corresponds to this continuation.
     */
    public val context: CoroutineContext

    /**
     * Resumes the execution of the corresponding coroutine passing a successful or failed [result] as the
     * return value of the last suspension point.
     */
    public fun resumeWith(result: Result<T>)
}
```

`Continuation`是一个接口，代表协程在挂起后协程恢复待执行的部分，定义了`resumeWith`方法，接收`Result`参数，该参数封装了协程的执行结果，结果有成功和失败两个状态。

协程中的代码经Kotlin编译器编译后，挂起函数和挂起lambda表达式都有一个隐式的`Continuation`参数传入。

```kotlin
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Token

class Item

class Post

suspend fun requestToken(): Token {
    println("requestToken")
    return Token()
}

suspend fun createPost(token: Token, item: Item): Post {
    println("createPost")
    return Post()
}

fun processPost(post: Post) {
    println("postItem")
}

@OptIn(DelicateCoroutinesApi::class)
fun postItem(item: Item) {
    GlobalScope.launch {
        val token = requestToken()
        val post = createPost(token, item)
        processPost(post)
    }

}
```

上面`postItem`函数，启动了一个协程，内部的实现是使用状态机来处理不同的挂起点，大致的CPS(Continuation Passing Style)代码为：

```koltin
// 编译后生成的内部类大致如下
final class postItem$1 extends SuspendLambda ... {
    public final Object invokeSuspend(Object result) {
        ...
        switch (this.label) {
            case 0:
                this.label = 1;
                token = requestToken(this)
                break;
            case 1:
                this.label = 2;
                Token token = result;
                post = createPost(token, this.item, this)
                break;
            case 2:
                Post post = result;
                processPost(post)
                break;
        }
    }
}
```

上面代码中的挂起点和初始挂起点对应的`Continuation`都会转化成一种状态，协程恢复就是跳转到下一个状态中。挂起函数将执行过程划分为多个`Continuation`片段，并且利用状态机的方式保证各个片段顺序执行。

![coroutine-suspend-continuation.webp](C:\Users\wangjie\Desktop\study\Kotlin\coroutine\imgs\coroutine-suspend-continuation.webp)



挂起函数不一定会挂起协程，当挂起函数的结果可用，协程库可以决定不挂起协程而继续执行。挂起函数可能会挂起
协程，但是不会阻塞线程。

协程的挂起是通过`suspend`函数实现，协程的恢复是通过`Continuation.resumeWith`实现。

### 19.4.2 协程恢复后，协程运行在哪个线程

协程从挂起点恢复后所在的线程由挂起函数所在的线程决定。



## 19.5 协程的创建、启动、挂起、恢复、调度分析

### 19.5.1 协程的创建、启动

以`CoroutineScope#launch`扩展函数为例，分析协程的创建和启动流程，如下：

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\Builds.common.kt

```kotlin
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

private open class StandaloneCoroutine(
    parentContext: CoroutineContext,
    active: Boolean
) : AbstractCoroutine<Unit>(parentContext, initParentJob = true, active = active) {
    override fun handleJobException(exception: Throwable): Boolean {
        handleCoroutineException(context, exception)
        return true
    }
}

private class LazyStandaloneCoroutine(
    parentContext: CoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) : StandaloneCoroutine(parentContext, active = false) {
    // 得到了编译器生成的继承`SuspendLambda`的类的对象。
    private val continuation = block.createCoroutineUnintercepted(this, this)
	
    // 如果当前为NEW，调用了start方法，则该方法被调用，
    override fun onStart() {
        continuation.startCoroutineCancellable(this)
    }
}



```

kotlinx-coroutines-core-jvm-1.7.3-sources\jvmMain\CoroutineContext.kt

```kotlin
@ExperimentalCoroutinesApi
public actual fun CoroutineScope.newCoroutineContext(context: CoroutineContext): CoroutineContext {
    val combined = foldCopies(coroutineContext, context, true)
    val debug = if (DEBUG) combined + CoroutineId(COROUTINE_ID.incrementAndGet()) else combined
    return if (combined !== Dispatchers.Default && combined[ContinuationInterceptor] == null)
        debug + Dispatchers.Default else debug
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\kotlinx\coroutines\AbstractCoroutine.kt

```kotlin
@InternalCoroutinesApi
public abstract class AbstractCoroutine<in T>(
    parentContext: CoroutineContext,
    initParentJob: Boolean,
    active: Boolean
) : JobSupport(active), Job, Continuation<T>, CoroutineScope {

    /**
     * Starts this coroutine with the given code [block] and [start] strategy.
     * This function shall be invoked at most once on this coroutine.
     * 
     * * [DEFAULT] uses [startCoroutineCancellable].
     * * [ATOMIC] uses [startCoroutine].
     * * [UNDISPATCHED] uses [startCoroutineUndispatched].
     * * [LAZY] does nothing.
     */
    public fun <R> start(start: CoroutineStart, receiver: R, block: suspend R.() -> T) {
        start(block, receiver, this)
    }
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\CoroutineStart.kt

```kotlin
public enum class CoroutineStart {
    ...
    /**
     * Starts the corresponding block with receiver as a coroutine with this coroutine start strategy.
     *
     * * [DEFAULT] uses [startCoroutineCancellable].
     * * [ATOMIC] uses [startCoroutine].
     * * [UNDISPATCHED] uses [startCoroutineUndispatched].
     * * [LAZY] does nothing.
     *
     * @suppress **This an internal API and should not be used from general code.**
     */
    @InternalCoroutinesApi
    public operator fun <R, T> invoke(block: suspend R.() -> T, receiver: R, completion: Continuation<T>): Unit =
        when (this) {
            DEFAULT -> block.startCoroutineCancellable(receiver, completion)
            ATOMIC -> block.startCoroutine(receiver, completion)
            UNDISPATCHED -> block.startCoroutineUndispatched(receiver, completion)
            LAZY -> Unit // will start lazily
        }
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\intrinsics\Cancellable.kt

```kotlin
/**
 * Use this function to start coroutine in a cancellable way, so that it can be cancelled
 * while waiting to be dispatched.
 */
internal fun <R, T> (suspend (R) -> T).startCoroutineCancellable(
    receiver: R, completion: Continuation<T>,
    onCancellation: ((cause: Throwable) -> Unit)? = null
) =
    runSafely(completion) {
        createCoroutineUnintercepted(receiver, completion).intercepted().resumeCancellableWith(Result.success(Unit), onCancellation)
    }

/**
 * Similar to [startCoroutineCancellable], but for already created coroutine.
 * [fatalCompletion] is used only when interception machinery throws an exception
 */
internal fun Continuation<Unit>.startCoroutineCancellable(fatalCompletion: Continuation<*>) =
    runSafely(fatalCompletion) {
        // 获取协程上下文中的`CoroutineInterceptor`元素，即拦截器，默认是`DispatchedContination`，它也是一个`Continuation`，然后调用扩展函数`Continuation.resumeCancellableWith`。
        intercepted().resumeCancellableWith(Result.success(Unit))
    }

...
private inline fun runSafely(completion: Continuation<*>, block: () -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        dispatcherFailure(completion, e)
    }
}

private fun dispatcherFailure(completion: Continuation<*>, e: Throwable) {
    /*
     * This method is invoked when we failed to start a coroutine due to the throwing
     * dispatcher implementation or missing Dispatchers.Main.
     * This situation is not recoverable, so we are trying to deliver the exception by all means:
     * 1) Resume the coroutine with an exception, so it won't prevent its parent from completion
     * 2) Rethrow the exception immediately, so it will crash the caller (e.g. when the coroutine had
     *    no parent or it was async/produce over MainScope).
     */
    completion.resumeWith(Result.failure(e))
    throw e
}
```

kotlin-stdlib-1.8.20\kotlin\coroutines\intrinsics\intrinsicsJvm.kt

```kotlin
@SinceKotlin("1.3")
public actual fun <T> (suspend () -> T).createCoroutineUnintercepted(
    completion: Continuation<T>
): Continuation<Unit> {
    val probeCompletion = probeCoroutineCreated(completion)
    return if (this is BaseContinuationImpl)
        create(probeCompletion)
    else
        createCoroutineFromSuspendFunction(probeCompletion) {
            (this as Function1<Continuation<T>, Any?>).invoke(it)
        }
}
```

kotlin-stdlib-1.8.20\kotlin\coroutines\jvm\internal\DebugProbes.kt

```kotlin
@SinceKotlin("1.3")
internal fun <T> probeCoroutineCreated(completion: Continuation<T>): Continuation<T> {
    /** implementation of this function is replaced by debugger */
    return completion
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\JobSupport.kt

```kotlin
@Deprecated(level = DeprecationLevel.ERROR, message = "This is internal API and may be removed in the future releases")
public open class JobSupport constructor(active: Boolean) : Job, ChildJob, ParentJob {
    ...
    private val _state = atomic<Any?>(if (active) EMPTY_ACTIVE else EMPTY_NEW)
    ...
    /**
     * Returns current state of this job.
     * If final state of the job is [Incomplete], then it is boxed into [IncompleteStateBox]
     * and should be [unboxed][unboxState] before returning to user code.
     */
    internal val state: Any? get() {
        _state.loop { state -> // helper loop on state (complete in-progress atomic operations)
            if (state !is OpDescriptor) return state
            state.perform(this)
        }
    }
    
    /**
     * @suppress **This is unstable API and it is subject to change.**
     */
    private inline fun loopOnState(block: (Any?) -> Unit): Nothing {
        while (true) {
            block(state)
        }
    }
    ...
    // 启动Job，如果Job是LazyStandaloneCoroutine，且在这之前为启动，是NEW状态，则，state = EMPTY(isActive = false)
    public final override fun start(): Boolean {
        loopOnState { state ->
            when (startInternal(state)) {
                FALSE -> return false
                TRUE -> return true
            }
        }
    }
    
        // returns: RETRY/FALSE/TRUE:
    //   FALSE when not new,
    //   TRUE  when started
    //   RETRY when need to retry
    private fun startInternal(state: Any?): Int {
        when (state) {
            // 如果是Empty(isActive = false)则设置为Empty(isActive = true)，然后调用onStart方法。
            is Empty -> { // EMPTY_X state -- no completion handlers
                if (state.isActive) return FALSE // already active
                if (!_state.compareAndSet(state, EMPTY_ACTIVE)) return RETRY
                onStart()
                return TRUE
            }
            is InactiveNodeList -> { // LIST state -- inactive with a list of completion handlers
                if (!_state.compareAndSet(state, state.list)) return RETRY
                onStart()
                return TRUE
            }
            else -> return FALSE // not a new state
        }
    }
}
```

kotlin-stdlib-1.8.20\kotlin\coroutines\jvm\internal\CoroutineImpl.kt

```kotlin
@SinceKotlin("1.3")
// State machines for named suspend functions extend from this class
internal abstract class ContinuationImpl(
  completion: Continuation<Any?>?,
    private val _context: CoroutineContext?
) : BaseContinuationImpl(completion) {
    constructor(completion: Continuation<Any?>?) : this(completion, completion?.context)

    public override val context: CoroutineContext
        get() = _context!!

    @Transient
    private var intercepted: Continuation<Any?>? = null
	
    // 由协程上下文中的`CoroutineInterceptor`元素，获取拦截当前`Continuation`的拦截器，该拦截器也是一个`Continuation`
    // ，默认是`DispatchedContinuation`。
    public fun intercepted(): Continuation<Any?> =
        intercepted
            ?: (context[ContinuationInterceptor]?.interceptContinuation(this) ?: this)
                .also { intercepted = it }

    protected override fun releaseIntercepted() {
        val intercepted = intercepted
        if (intercepted != null && intercepted !== this) {
            context[ContinuationInterceptor]!!.releaseInterceptedContinuation(intercepted)
        }
        this.intercepted = CompletedContinuation // just in case
    }
}


@SinceKotlin("1.3")
// Suspension lambdas inherit from this class
internal abstract class SuspendLambda(
    public override val arity: Int,
    completion: Continuation<Any?>?
) : ContinuationImpl(completion), FunctionBase<Any?>, SuspendFunction {
    constructor(arity: Int) : this(arity, null)

    public override fun toString(): String =
        if (completion == null)
            Reflection.renderLambdaToString(this) // this is lambda
        else
            super.toString() // this is continuation
}

```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\CoroutineDispatcher.kt

```kotlin
public abstract class CoroutineDispatcher :
    AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
	...

    /**
     * Returns a continuation that wraps the provided [continuation], thus intercepting all resumptions.
     *
     * This method should generally be exception-safe. An exception thrown from this method
     * may leave the coroutines that use this dispatcher in the inconsistent and hard to debug state.
     */
    public final override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> =
        DispatchedContinuation(this, continuation)
    ...    
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\internal\DispatchedContinuation.kt

```kotlin
PublishedApi
internal class DispatchedContinuation<in T>(
    @JvmField internal val dispatcher: CoroutineDispatcher,
    // Used by the IDEA debugger via reflection and must be kept binary-compatible, see KTIJ-24102
    @JvmField val continuation: Continuation<T>
) : DispatchedTask<T>(MODE_UNINITIALIZED), CoroutineStackFrame, Continuation<T> by continuation {
    ...
    override fun takeState(): Any? {
        val state = _state
        assert { state !== UNDEFINED } // fail-fast if repeatedly invoked
        _state = UNDEFINED
        return state
    }
    ...
    
    // We inline it to save an entry on the stack in cases where it shows (unconfined dispatcher)
    // It is used only in Continuation<T>.resumeCancellableWith
    @Suppress("NOTHING_TO_INLINE")
    internal inline fun resumeCancellableWith(
        result: Result<T>,
        noinline onCancellation: ((cause: Throwable) -> Unit)?
    ) {
        // 如果是协程的启动，则result为`Result(Unit)`
        // dispatcher.isDispatchNeeded(context) 默认为true。然后由协程调度器执行dispatch方法向线程池提交任务。
        val state = result.toState(onCancellation)
        if (dispatcher.isDispatchNeeded(context)) {
            _state = state
            resumeMode = MODE_CANCELLABLE
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
}
/**
 * It is not inline to save bytecode (it is pretty big and used in many places)
 * and we leave it public so that its name is not mangled in use stack traces if it shows there.
 * It may appear in stack traces when coroutines are started/resumed with unconfined dispatcher.
 * @suppress **This an internal API and should not be used from general code.**
 */
@InternalCoroutinesApi
public fun <T> Continuation<T>.resumeCancellableWith(
    result: Result<T>,
    onCancellation: ((cause: Throwable) -> Unit)? = null
): Unit = when (this) {
    is DispatchedContinuation -> resumeCancellableWith(result, onCancellation)
    else -> resumeWith(result)
}

```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\internal\DispatchedTask.kt

```kotlin
@PublishedApi
internal abstract class DispatchedTask<in T> internal constructor(
    // Used by the IDEA debugger via reflection and must be kept binary-compatible, see KTIJ-24102
    @JvmField public var resumeMode: Int
) : SchedulerTask() {
    
    // 线程池中的线程会执行该任务。
    final override fun run() {
        assert { resumeMode != MODE_UNINITIALIZED } // should have been set before dispatching
        val taskContext = this.taskContext
        var fatalException: Throwable? = null
        try {
            val delegate = delegate as DispatchedContinuation<T>
            val continuation = delegate.continuation
            withContinuationContext(continuation, delegate.countOrElement) {
                val context = continuation.context
                // 如果是协程的启动这state为`Result(Unit)`
                val state = takeState() // NOTE: Must take state in any case, even if cancelled
                val exception = getExceptionalResult(state)
                /*
                 * Check whether continuation was originally resumed with an exception.
                 * If so, it dominates cancellation, otherwise the original exception
                 * will be silently lost.
                 */
                // 协程启动过程，exception为null，resumeMode为MODE_CANCELLABLE，这样获取上下文中的Job 元素。
                // 这这之前Job isActive为true。最后执行`Continuation.resume方法。`
                val job = if (exception == null && resumeMode.isCancellableMode) context[Job] else null
                if (job != null && !job.isActive) {
                    val cause = job.getCancellationException()
                    cancelCompletedResult(state, cause)
                    continuation.resumeWithStackTrace(cause)
                } else {
                    if (exception != null) {
                        continuation.resumeWithException(exception)
                    } else {
                        continuation.resume(getSuccessfulResult(state))
                    }
                }
            }
        } catch (e: Throwable) {
            // This instead of runCatching to have nicer stacktrace and debug experience
            fatalException = e
        } finally {
            val result = runCatching { taskContext.afterTask() }
            handleFatalException(fatalException, result.exceptionOrNull())
        }
    }
 ...   
}
```

**协程的创建与启动**

+ `start`参数为`CoroutineStart.LAZY`，则构建`LazyStandaloneCoroutine`，它是一个`Job`，对应的状态是`NEW`。

  + 如果该`Job`调用`start`方法，

    > 先由属于挂起Lambda表达式的`block`，调用
    >
    > `(suspend () -> T).createCoroutineUnintercepted(completion: Continuation<T>): Continuation<Unit>`扩展方法生成一个`Continuation`，它属于`SuspendLambda`类型，且该类是编译器生成的。接着由协程上下文中的`CoroutineInterceptor`元素执行拦截操作；默认的拦截器是`CoroutineDispatcher`类型，拦截器的拦截操作获取一个`Continuation`，默认是一个`DispatchedContinuation`，然后执行`resumeCancellableWith`方法；`DispatchedContination`也实现了`Runnable`；然后由`CoroutineDispatcher`协程调度器向线程池中提交该任务，或者由指定的线程执行该任务；任务执行过程中会通过`DispatchedConroutine`持有的`Continuation`调用`resumeWith`方法，而这个`Continuation`就是编译器生成的继承`SuspendLmbda`的类的对象；然后调用`invokeSuspend`方法，协程体中的代码就执行了，运行在指定的线程上。

  + `DispatchedContinuation`定义的`resumeCancellableWith`方法实现了了拦截的逻辑。

  + 协程上下文中的`CoroutineInterceptor`元素属于`CoroutineDispatcher`类型。
  + `SuspendLambda -> ContinuationImpl -> BaseContinuationImpl -> Continuation`类之间的层级关系。

+ `start`参数不为`CoroutineStart.LAZY`，则构建`StandaloneCoroutine`，它是一个`Job`，默认的状态是`ACTIVE`，同时会启动协程。

  + 启动的流程为

    > 先由属于挂起Lambda表达式的`block`，调用
    >
    > `(suspend () -> T).createCoroutineUnintercepted(completion: Continuation<T>): Continuation<Unit>`扩展方法生成一个`Continuation`，它属于`SuspendLambda`类型，且该类是编译器生成的。接着由协程上下文中的`CoroutineInterceptor`元素执行拦截操作；默认的拦截器是`CoroutineDispatcher`类型，拦截器的拦截操作获取一个`Continuation`，默认是一个`DispatchedContinuation`，然后执行`resumeCancellableWith`方法；`DispatchedContination`也实现了`Runnable`；然后由`CoroutineDispatcher`协程调度器向线程池中提交该任务，或者由指定的线程执行该任务；任务执行过程中会通过`DispatchedConroutine`持有的`Continuation`调用`resumeWith`方法，而这个`Continuation`就是编译器生成的继承`SuspendLmbda`的类的对象；然后调用`invokeSuspend`方法，协程体中的代码就执行了，运行在指定的线程上。



### 19.5.2 协程的线程调度

协程的调度指的是协程运行在哪个线程上。

### 19.5.3 协程的挂起与恢复







### 19.5.4 协程的三层包装

协程的创建和启动有三层`Continuation`包装。

常用的`launch`和`async`返回的`Job`、`Deferred`，里面提供了设置获取协程状态，提供了取消协程接口，而它们的实例都
继承`AbstractCoroutine`，它是协程的第一层包装。

第二层包装是编译器生成的`SuspendLambda`的子类，封装了协程的真正运算逻辑（协程体的代码），继承`BaseContinuationImpl`，其中持有的`completion`属性指向第一层包装。

第三层包装是负责协程的调度的`DispatchedContinuation`，封装了协程调度逻辑，其持有的`continuation`属性指向包第二层包装。

三层包装都实现了Continuation接口，通过代理模式将协程的各层包装组合在一起，每层负责不同的功能。

![协程的三层包装](C:\Users\wangjie\Desktop\study\Kotlin\coroutine\imgs\协程的三层包装.webp)

## 19.6 将回调风格的函数转换为挂起函数

在异步编程中，接口回调是常见实现异步任务结果通知的方式。`Kotlin-stdlib`库提供了将回调风格的api转换为挂起函数的顶层函数组。这样通过调用挂起函数，顺序编写代码实现异步，简化异步编程。

以`OkHttp`网络库为例，在执行异步请求使，会调用`Call.enqueue(responseCallback: Callback)`方法。该方法会将请求先添加的异步任务队列中，然后从队列中取出任务，提交给线程池执行，异步任务执行完成后，通过接口`Callback`回调的方式将任务结果通知给上层组件。

### 19.6.1 `suspendCoroutine(crossinline block: (Continuation<T>) -> Unit): T`

kotlin-stdlib-1.8.20\kotlin\coroutines\Continuation.kt

```kotlin
/**
 * Obtains the current continuation instance inside suspend functions and suspends
 * the currently running coroutine.
 *
 * In this function both [Continuation.resume] and [Continuation.resumeWithException] can be used either synchronously in
 * the same stack-frame where the suspension function is run or asynchronously later in the same thread or
 * from a different thread of execution. Subsequent invocation of any resume function will produce an [IllegalStateException].
 */
@SinceKotlin("1.3")
@InlineOnly
public suspend inline fun <T> suspendCoroutine(crossinline block: (Continuation<T>) -> Unit): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return suspendCoroutineUninterceptedOrReturn { c: Continuation<T> ->
        val safe = SafeContinuation(c.intercepted())
        block(safe)
        safe.getOrThrow()
    }
}

```

+ 顶层函数，也是挂起函数。该函数可以将传统回调风格函数转换为挂起函数，从而简化异步编程。
+ 该函数会挂起当前协程，并获取协程的第二次封装的`Continuation`。
+ 在`block`参数被调用使，需要调用`Continuation.resume`或`Continuation.resumeWithException`，一次。
  + 如果不调用，会导致协程一直挂起，引起内存泄漏。
  + 如果调用多次，会抛出`IllegaleStateException`异常。
+ 如果协程在挂起期间被取消，它不会有任何反应，即不可取消。



### 19.6.2 `suspendCancellableCoroutine(crossinline block: (CancellableContinuation<T>) -> Unit): T`

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\CancellableContinuation.kt

```kotlin
public suspend inline fun <T> suspendCancellableCoroutine(
    crossinline block: (CancellableContinuation<T>) -> Unit
): T =
    suspendCoroutineUninterceptedOrReturn { uCont ->
        val cancellable = CancellableContinuationImpl(uCont.intercepted(), resumeMode = MODE_CANCELLABLE)
        /*
         * For non-atomic cancellation we setup parent-child relationship immediately
         * in case when `block` blocks the current thread (e.g. Rx2 with trampoline scheduler), but
         * properly supports cancellation.
         */
        cancellable.initCancellability()
        block(cancellable)
        cancellable.getResult()
    }

```

+ 顶层的挂起函数。该函数可以将传统回调风格函数转换为挂起函数，从而简化异步编程。
+ 该函数会挂起当前协程，并获取协程的第二次封装的`Continuation`。
+ 在`block`参数被调用使，需要调用`Continuation.resume`或`Continuation.resumeWithException`，一次。
  + 如果不调用，会导致协程一直挂起，引起内存泄漏。
  + 如果调用多次，会抛出`IllegaleStateException`异常。
+ 如果协程在挂起期间被取消，它会抛出`CancellableException`异常。



### 19.6.3 `delay(timeMillis: Long)`

[深究Kotlin协程delay函数源码实现](https://www.jianshu.com/p/1d9dcd331b9c)

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\Delay.kt

```kotlin
/**
 * Delays coroutine for a given time without blocking a thread and resumes it after a specified time.
 * If the given [timeMillis] is non-positive, this function returns immediately.
 *
 * This suspending function is cancellable.
 * If the [Job] of the current coroutine is cancelled or completed while this suspending function is waiting, this function
 * immediately resumes with [CancellationException].
 * There is a **prompt cancellation guarantee**. If the job was cancelled while this function was
 * suspended, it will not resume successfully. See [suspendCancellableCoroutine] documentation for low-level details.
 *
 * If you want to delay forever (until cancellation), consider using [awaitCancellation] instead.
 *
 * Note that delay can be used in [select] invocation with [onTimeout][SelectBuilder.onTimeout] clause.
 *
 * Implementation note: how exactly time is tracked is an implementation detail of [CoroutineDispatcher] in the context.
 * @param timeMillis time in milliseconds.
 */
public suspend fun delay(timeMillis: Long) {
    if (timeMillis <= 0) return // don't delay
    return suspendCancellableCoroutine sc@ { cont: CancellableContinuation<Unit> ->
        // if timeMillis == Long.MAX_VALUE then just wait forever like awaitCancellation, don't schedule.
        if (timeMillis < Long.MAX_VALUE) {
            // 由协程上下文获取扩展成员变量`delay`，默认是`DefaultExecutor`，并执行`scheduleResumeAfterDelay`方法
            cont.context.delay.scheduleResumeAfterDelay(timeMillis, cont)
        }
    }
}
...
internal val CoroutineContext.delay: Delay get() = get(ContinuationInterceptor) as? Delay ?: DefaultDelay
```

kotlinx-coroutines-core-jvm-1.7.3-sources\jvmMain\DefaultDelay.kt

```kotlin
private val defaultMainDelayOptIn = systemProp("kotlinx.coroutines.main.delay", false)

@PublishedApi
internal actual val DefaultDelay: Delay = initializeDefaultDelay()

private fun initializeDefaultDelay(): Delay {
    // Opt-out flag
    if (!defaultMainDelayOptIn) return DefaultExecutor
    val main = Dispatchers.Main
    /*
     * When we already are working with UI and Main threads, it makes
     * no sense to create a separate thread with timer that cannot be controller
     * by the UI runtime.
     */
    return if (main.isMissing() || main !is Delay) DefaultExecutor else main
}

...

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
internal actual object DefaultExecutor : EventLoopImplBase(), Runnable {
    const val THREAD_NAME = "kotlinx.coroutines.DefaultExecutor"

    init {
        incrementUseCount() // this event loop is never completed
    }
    ...
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\EventLoop.common.kt

```kotlin
internal abstract class EventLoopImplBase: EventLoopImplPlatform(), Delay {
    
    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        val timeNanos = delayToNanos(timeMillis)
        if (timeNanos < MAX_DELAY_NS) {
            val now = nanoTime()
            // 构建延迟任务`DelayedResumeTask`
            DelayedResumeTask(now + timeNanos, continuation).also { task ->
                /*
                 * Order is important here: first we schedule the heap and only then
                 * publish it to continuation. Otherwise, `DelayedResumeTask` would
                 * have to know how to be disposed of even when it wasn't scheduled yet.
                 */
                 // 将任务添加到延迟任务队列的尾部。                                                  
                schedule(now, task)
                continuation.disposeOnCancellation(task)
            }
        }
    }
    ...
    fun schedule(now: Long, delayedTask: DelayedTask) {
        when (scheduleImpl(now, delayedTask)) {
            SCHEDULE_OK -> if (shouldUnpark(delayedTask)) unpark()
            SCHEDULE_COMPLETED -> reschedule(now, delayedTask)
            SCHEDULE_DISPOSED -> {} // do nothing -- task was already disposed
            else -> error("unexpected result")
        }
    }

    private fun shouldUnpark(task: DelayedTask): Boolean = _delayed.value?.peek() === task

    private fun scheduleImpl(now: Long, delayedTask: DelayedTask): Int {
        if (isCompleted) return SCHEDULE_COMPLETED
        val delayedQueue = _delayed.value ?: run {
            _delayed.compareAndSet(null, DelayedTaskQueue(now))
            _delayed.value!!
        }
        return delayedTask.scheduleTask(now, delayedQueue, this)
    }
    ...
    private inner class DelayedResumeTask(
        nanoTime: Long,
        private val cont: CancellableContinuation<Unit>
    ) : DelayedTask(nanoTime) {
        override fun run() { with(cont) { resumeUndispatched(Unit) } }
        override fun toString(): String = super.toString() + cont.toString()
    }
    ...
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\EventLoop.kt

```kotlin
internal actual abstract class EventLoopImplPlatform: EventLoop() {
    protected abstract val thread: Thread

    protected actual fun unpark() {
        // 获取`thread`，使`thread`成员变量初始化，从而启动线程
        val thread = thread // atomic read
        if (Thread.currentThread() !== thread)
            unpark(thread)
    }

    protected actual open fun reschedule(now: Long, delayedTask: EventLoopImplBase.DelayedTask) {
        DefaultExecutor.schedule(now, delayedTask)
    }
}
```



```kotlin
internal actual object DefaultExecutor : EventLoopImplBase(), Runnable {
    ...
    @Suppress("ObjectPropertyName")
    @Volatile
    private var _thread: Thread? = null

    override val thread: Thread
        get() = _thread ?: createThreadSync()
    ...
    override fun run() {
        ThreadLocalEventLoop.setEventLoop(this)
        registerTimeLoopThread()
        try {
            var shutdownNanos = Long.MAX_VALUE
            if (!notifyStartup()) return
            while (true) {
                Thread.interrupted() // just reset interruption flag
                // 在延迟时间到来之前
                var parkNanos = processNextEvent()
                if (parkNanos == Long.MAX_VALUE) {
                    // nothing to do, initialize shutdown timeout
                    val now = nanoTime()
                    if (shutdownNanos == Long.MAX_VALUE) shutdownNanos = now + KEEP_ALIVE_NANOS
                    val tillShutdown = shutdownNanos - now
                    if (tillShutdown <= 0) return // shut thread down
                    parkNanos = parkNanos.coerceAtMost(tillShutdown)
                } else
                    shutdownNanos = Long.MAX_VALUE
                if (parkNanos > 0) {
                    // check if shutdown was requested and bail out in this case
                    if (isShutdownRequested) return
                    parkNanos(this, parkNanos)
                }
            }
        } finally {
            _thread = null // this thread is dead
            acknowledgeShutdownIfNeeded()
            unregisterTimeLoopThread()
            // recheck if queues are empty after _thread reference was set to null (!!!)
            if (!isEmpty) thread // recreate thread if it is needed
        }
    }
    
    @Synchronized
    private fun createThreadSync(): Thread {
        // 初始化成员变量`thread`并启动线程
        return _thread ?: Thread(this, THREAD_NAME).apply {
            _thread = this
            isDaemon = true
            start()
        }
    }
    
}
```





kotlin-stdlib-1.8.20\kotlin\until\Standard.kt

```kotlin
/**
 * Calls the specified function [block] with the given [receiver] as its receiver and returns its result.
 *
 * For detailed usage information see the documentation for [scope functions](https://kotlinlang.org/docs/reference/scope-functions.html#with).
 */
@kotlin.internal.InlineOnly
public inline fun <T, R> with(receiver: T, block: T.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return receiver.block()
}
```

该函数是挂起函数，也是顶层函数，内部调用了`suspendCancellableCoroutine(crossinline block: (CancellableContinuation<T>) -> Unit
): T`方法。

+ 挂起当前协程，获取协程的第二次封装`Continuation`。
+ 由当前协程的第二层封装`Continuation`调用拦截器方法得到第三次封装的`Continuation`，即默认的`DispatchedContinuation`，然后由`DispatchedContinuation`构造`CancellableContinuationImpl`对象。
+ 由协程上下文获取扩展成员变量`delay`，默认是`DefaultExecutor`，并执行`scheduleResumeAfterDelay`方法。
  + 该方法会构造`DelayedResumeTask`延迟任务，并将任务添加到延迟任务队列`_delayed`的尾部；然后初始化`DefaultExecotor`的成员变量`thread`，并启动这个线程，`DefaultExcutor`的`run`方法被调用。
+ 在延迟时间到来之间协程一直挂起。
+ `DefaultExecutor`定时检查延迟队列中的任务是否可以执行，如果延迟时间到，任务可以执行，从延迟任务队列中取出任务，放到待执行的任务队列`queue`中，从待执行的任务队列中取出任务执行`run`方法，然后恢复协程。



## 19.7 协程之间父子关系的建立

协程之间父子关系的建立需要先熟悉几个接口和类的定义。

### 19.7.1 CompletionHandler

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\CompleionHandler.common.kt

```kotlin
public typealias CompletionHandler = (cause: Throwable?) -> Unit
```

`CompletionHandler`是函数类型`(cause: Throwable?) -> Unit`的别名。

### 19.7.2 CompletionHandlerBase

kotlinx-coroutines-core-jvm-1.7.3-sources\cocurrentMain\CompletionHandler.kt

```kotlin
internal actual abstract class CompletionHandlerBase actual constructor() : LockFreeLinkedListNode(), CompletionHandler {
    actual abstract override fun invoke(cause: Throwable?)
}

internal actual inline val CompletionHandlerBase.asHandler: CompletionHandler get() = this
```

`CompletionHandlerBase`是个抽象类，继承`LockFreeLinkedListNode`和`CompletionHandler`。

### 19.7.3 JobNode、Incomplete、JobCancellingNode、ChildHandleNode

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\JobSupport.kt

```kotlin
internal interface Incomplete {
    val isActive: Boolean
    val list: NodeList? // is null only for Empty and JobNode incomplete state objects
}

internal abstract class JobNode : CompletionHandlerBase(), DisposableHandle, Incomplete {
    /**
     * Initialized by [JobSupport.makeNode].
     */
    lateinit var job: JobSupport
    override val isActive: Boolean get() = true
    override val list: NodeList? get() = null
    override fun dispose() = job.removeNode(this)
    override fun toString() = "$classSimpleName@$hexAddress[job@${job.hexAddress}]"
}

...
internal abstract class JobCancellingNode : JobNode()

private class InvokeOnCancelling(
    private val handler: CompletionHandler
) : JobCancellingNode()  {
    // delegate handler shall be invoked at most once, so here is an additional flag
    private val _invoked = atomic(0) // todo: replace with atomic boolean after migration to recent atomicFu
    override fun invoke(cause: Throwable?) {
        if (_invoked.compareAndSet(0, 1)) handler.invoke(cause)
    }
}

internal class ChildHandleNode(
    @JvmField val childJob: ChildJob
) : JobCancellingNode(), ChildHandle {
    override val parent: Job get() = job
    override fun invoke(cause: Throwable?) = childJob.parentCancelled(job)
    override fun childCancelled(cause: Throwable): Boolean = job.childCancelled(cause)
}

```

`ChildHandleNode`持有子`Job`的引用`childJob`，也持有父`Job`的引用`parent`。该类属于`Incomplete`、`JobNode`、`DisposableHandle`、`ChildHandlerBase`。



### 19.7.4 协程之间父子关系的建立

以协程的构建器方法`CoroutineScope.launch`为例介绍协程之间父子关系的建立。

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\Builds.common.kt

```kotlin
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

...
private open class StandaloneCoroutine(
    parentContext: CoroutineContext,
    active: Boolean
) : AbstractCoroutine<Unit>(parentContext, initParentJob = true, active = active) {
    override fun handleJobException(exception: Throwable): Boolean {
        handleCoroutineException(context, exception)
        return true
    }
}

private class LazyStandaloneCoroutine(
    parentContext: CoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) : StandaloneCoroutine(parentContext, active = false) {
    private val continuation = block.createCoroutineUnintercepted(this, this)

    override fun onStart() {
        continuation.startCoroutineCancellable(this)
    }
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\kotlinx\coroutines\AbstractCoroutine.kt

```kotlin
@InternalCoroutinesApi
public abstract class AbstractCoroutine<in T>(
    parentContext: CoroutineContext,
    initParentJob: Boolean,
    active: Boolean
) : JobSupport(active), Job, Continuation<T>, CoroutineScope {
    
    init {
        /*
         * Setup parent-child relationship between the parent in the context and the current coroutine.
         * It may cause this coroutine to become _cancelling_ if the parent is already cancelled.
         * It is dangerous to install parent-child relationship here if the coroutine class
         * operates its state from within onCancelled or onCancelling
         * (with exceptions for rx integrations that can't have any parent)
         */
        if (initParentJob) initParentJob(parentContext[Job])
    }
    ...

    /**
     * Starts this coroutine with the given code [block] and [start] strategy.
     * This function shall be invoked at most once on this coroutine.
     * 
     * * [DEFAULT] uses [startCoroutineCancellable].
     * * [ATOMIC] uses [startCoroutine].
     * * [UNDISPATCHED] uses [startCoroutineUndispatched].
     * * [LAZY] does nothing.
     */
    public fun <R> start(start: CoroutineStart, receiver: R, block: suspend R.() -> T) {
        start(block, receiver, this)
    }
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\JobSupport.kt

```kotlin
@Deprecated(level = DeprecationLevel.ERROR, message = "This is internal API and may be removed in the future releases")
public open class JobSupport constructor(active: Boolean) : Job, ChildJob, ParentJob {
    final override val key: CoroutineContext.Key<*> get() = Job
    ...
    
    private val _state = atomic<Any?>(if (active) EMPTY_ACTIVE else EMPTY_NEW)

    private val _parentHandle = atomic<ChildHandle?>(null)
    internal var parentHandle: ChildHandle?
        get() = _parentHandle.value
        set(value) { _parentHandle.value = value }

    override val parent: Job?
        get() = parentHandle?.parent
    
    /**
     * Initializes parent job.
     * It shall be invoked at most once after construction after all other initialization.
     */
    protected fun initParentJob(parent: Job?) {
        assert { parentHandle == null }
        if (parent == null) {
            parentHandle = NonDisposableHandle
            return
        }
        parent.start() // make sure the parent is started
        @Suppress("DEPRECATION")
        // 建立协程之间的父子关系。
        val handle = parent.attachChild(this)
        parentHandle = handle
        // now check our state _after_ registering (see tryFinalizeSimpleState order of actions)
        if (isCompleted) {
            handle.dispose()
            parentHandle = NonDisposableHandle // release it just in case, to aid GC
        }
    }
    ...
    @Suppress("OverridingDeprecatedMember")
    public final override fun invokeOnCompletion(handler: CompletionHandler): DisposableHandle =
        invokeOnCompletion(onCancelling = false, invokeImmediately = true, handler = handler)

    public final override fun invokeOnCompletion(
        onCancelling: Boolean,
        invokeImmediately: Boolean,
        handler: CompletionHandler
    ): DisposableHandle {
        // Create node upfront -- for common cases it just initializes JobNode.job field,
        // for user-defined handlers it allocates a JobNode object that we might not need, but this is Ok.
        // 完成ChildHandleNode持有的父`Job`引用`parent`的赋值。
        val node: JobNode = makeNode(handler, onCancelling)
        loopOnState { state ->
            when (state) {
                is Empty -> { // EMPTY_X state -- no completion handlers
                    if (state.isActive) {
                        // 设置父`Job`的`state`，记录`ChildHandleNode`。
                        // try move to SINGLE state
                        if (_state.compareAndSet(state, node)) return node
                    } else
                        promoteEmptyToNodeList(state) // that way we can add listener for non-active coroutine
                }
                is Incomplete -> {
                    val list = state.list
                    if (list == null) { // SINGLE/SINGLE+
                        promoteSingleToNodeList(state as JobNode)
                    } else {
                        var rootCause: Throwable? = null
                        var handle: DisposableHandle = NonDisposableHandle
                        if (onCancelling && state is Finishing) {
                            synchronized(state) {
                                // check if we are installing cancellation handler on job that is being cancelled
                                rootCause = state.rootCause // != null if cancelling job
                                // We add node to the list in two cases --- either the job is not being cancelled
                                // or we are adding a child to a coroutine that is not completing yet
                                if (rootCause == null || handler.isHandlerOf<ChildHandleNode>() && !state.isCompleting) {
                                    // Note: add node the list while holding lock on state (make sure it cannot change)
                                    if (!addLastAtomic(state, list, node)) return@loopOnState // retry
                                    // just return node if we don't have to invoke handler (not cancelling yet)
                                    if (rootCause == null) return node
                                    // otherwise handler is invoked immediately out of the synchronized section & handle returned
                                    handle = node
                                }
                            }
                        }
                        if (rootCause != null) {
                            // Note: attachChild uses invokeImmediately, so it gets invoked when adding to cancelled job
                            if (invokeImmediately) handler.invokeIt(rootCause)
                            return handle
                        } else {
                            if (addLastAtomic(state, list, node)) return node
                        }
                    }
                }
                else -> { // is complete
                    // :KLUDGE: We have to invoke a handler in platform-specific way via `invokeIt` extension,
                    // because we play type tricks on Kotlin/JS and handler is not necessarily a function there
                    if (invokeImmediately) handler.invokeIt((state as? CompletedExceptionally)?.cause)
                    return NonDisposableHandle
                }
            }
        }
    }

    private fun makeNode(handler: CompletionHandler, onCancelling: Boolean): JobNode {
        val node = if (onCancelling) {
            (handler as? JobCancellingNode)
                ?: InvokeOnCancelling(handler)
        } else {
            (handler as? JobNode)
                ?.also { assert { it !is JobCancellingNode } }
                ?: InvokeOnCompletion(handler)
        }
        node.job = this
        return node
    }
    ...
    @Suppress("OverridingDeprecatedMember")
    public final override fun attachChild(child: ChildJob): ChildHandle {
        /*
         * Note: This function attaches a special ChildHandleNode node object. This node object
         * is handled in a special way on completion on the coroutine (we wait for all of them) and
         * is handled specially by invokeOnCompletion itself -- it adds this node to the list even
         * if the job is already cancelling. For cancelling state child is attached under state lock.
         * It's required to properly wait all children before completion and provide linearizable hierarchy view:
         * If child is attached when the job is already being cancelled, such child will receive immediate notification on
         * cancellation, but parent *will* wait for that child before completion and will handle its exception.
         */
        // 先构建`ChildHandleNode`对象，该对象会持有子`Job`的引用`childJob`。
        return invokeOnCompletion(onCancelling = true, handler = ChildHandleNode(child).asHandler) as ChildHandle
    }
    
    
    
}
```



协程的构建器方法`CoroutineScope.launch`，会构建继承`AbstractCoroutine`类的对象，在构造过程中，会调用`initParentJob -> JobSupport.attachChild`方法，构建协程之间的父子关系。

**Job.attachChild(childJob)**

该方法的主要实现步骤：

+ 根据父`Job`和子`Job`，构建`ChildHandleNode`对象，该对象会持有父`Job`、子`Job`的引用。
+ 设置父`Job`的`state`为`ChildHandleNode`。
+ 设置子`Job`的`parentHandle`，持有`ChildHandleNode`的引用，设置子`Job`的`parent`。



## 19.8 协程的取消

要掌握协程的取消，需要先熟悉几个接口和类

### 19.8.1 CancellationException

jdk11\java\base\java\util\concurrent\CancellationException.java

```java
public class CancellationException extends IllegalStateException {
    private static final long serialVersionUID = -9202173006928992231L;

    /**
     * Constructs a {@code CancellationException} with no detail message.
     */
    public CancellationException() {}

    /**
     * Constructs a {@code CancellationException} with the specified detail
     * message.
     *
     * @param message the detail message
     */
    public CancellationException(String message) {
        super(message);
    }
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\jvmMain\Exceptions.kt

```kotlin
public actual typealias CancellationException = java.util.concurrent.CancellationException

internal actual class JobCancellationException public actual constructor(
    message: String,
    cause: Throwable?,
    @JvmField @Transient internal actual val job: Job
) : CancellationException(message), CopyableThrowable<JobCancellationException> {

    init {
        if (cause != null) initCause(cause)
    }

    override fun fillInStackTrace(): Throwable {
        if (DEBUG) {
            return super.fillInStackTrace()
        }
        // Prevent Android <= 6.0 bug, #1866
        stackTrace = emptyArray()
        /*
         * In non-debug mode we don't want to have a stacktrace on every cancellation/close,
         * parent job reference is enough. Stacktrace of JCE is not needed most of the time (e.g., it is not logged)
         * and hurts performance.
         */
        return this
    }

    override fun createCopy(): JobCancellationException? {
        if (DEBUG) {
            return JobCancellationException(message!!, this, job)
        }

        /*
         * In non-debug mode we don't copy JCE for speed as it does not have the stack trace anyway.
         */
        return null
    }

    ...
}
```

### 19.8.2 取消协程的实现

调试代码

```kotlin
fun main(args: Array<String>): Unit = runBlocking {
    val jobParent = launch {
        val jobChild = launch {
            delay(600_1000)
        }
        delay(600_000)
    }

    launch {
        if (jobParent.isActive) {
            jobParent.cancel()
        }
    }

}
```



kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\JobSupport.kt

```kotlin
@Deprecated(level = DeprecationLevel.ERROR, message = "This is internal API and may be removed in the future releases")
public open class JobSupport constructor(active: Boolean) : Job, ChildJob, ParentJob {
    ...
    // 通过遍历-递归调用的方式将取消的请求传递给子协程，子协程处理取消请求；
    private fun notifyCancelling(list: NodeList, cause: Throwable) {
        // first cancel our own children
        onCancelling(cause)
        notifyHandlers<JobCancellingNode>(list, cause)
        // then cancel parent
        cancelParent(cause) // tentative cancellation -- does not matter if there is no parent
    }

    /**
     * The method that is invoked when the job is cancelled to possibly propagate cancellation to the parent.
     * Returns `true` if the parent is responsible for handling the exception, `false` otherwise.
     *
     * Invariant: never returns `false` for instances of [CancellationException], otherwise such exception
     * may leak to the [CoroutineExceptionHandler].
     */
    private fun cancelParent(cause: Throwable): Boolean {
        // 如果当前协程是作用域内协程，则不向父协程传递取消请求。
        // Is scoped coroutine -- don't propagate, will be rethrown
        if (isScopedCoroutine) return true

        /* CancellationException is considered "normal" and parent usually is not cancelled when child produces it.
         * This allow parent to cancel its children (normally) without being cancelled itself, unless
         * child crashes and produce some other exception during its completion.
         */
        val isCancellation = cause is CancellationException
        val parent = parentHandle
        // No parent -- ignore CE, report other exceptions.
        if (parent === null || parent === NonDisposableHandle) {
            return isCancellation
        }

        // 将取消请求传递给父协程。
        // Notify parent but don't forget to check cancellation
        return parent.childCancelled(cause) || isCancellation
    }

    private fun NodeList.notifyCompletion(cause: Throwable?) =
        notifyHandlers<JobNode>(this, cause)

    private inline fun <reified T: JobNode> notifyHandlers(list: NodeList, cause: Throwable?) {
        var exception: Throwable? = null
        list.forEach<T> { node ->
            try {
                node.invoke(cause)
            } catch (ex: Throwable) {
                exception?.apply { addSuppressedThrowable(ex) } ?: run {
                    exception =  CompletionHandlerException("Exception in completion handler $node for $this", ex)
                }
            }
        }
        exception?.let { handleOnCompletionException(it) }
    }
    ...
    public override fun cancel(cause: CancellationException?) {
        // 如果cause为null，则构造`JobCancellationException`对象，该异常属于`CancellationException`
        cancelInternal(cause ?: defaultCancellationException())
    }
    
    protected open fun cancellationExceptionMessage(): String = "Job was cancelled"
    
    ...
    // It is overridden in channel-linked implementation
    public open fun cancelInternal(cause: Throwable) {
        cancelImpl(cause)
    }

    // 子协程接收到父协程传递过来的取消请求时，会调用该函数。然后子协程自身取消自己。
    // Parent is cancelling child
    public final override fun parentCancelled(parentJob: ParentJob) {
        cancelImpl(parentJob)
    }
    
    // 父协程接收到子协程传递过来的取消请求时，会调用该函数。如果`cause`属于`CancellationException`则
    //放弃这次请求；否则父协程自身处理取消请求。
    public open fun childCancelled(cause: Throwable): Boolean {
        if (cause is CancellationException) return true
        return cancelImpl(cause) && handlesException
    }

    ...
    // cause is Throwable or ParentJob when cancelChild was invoked
    // returns true is exception was handled, false otherwise
    internal fun cancelImpl(cause: Any?): Boolean {
        var finalState: Any? = COMPLETING_ALREADY
        if (onCancelComplete) {
            // make sure it is completing, if cancelMakeCompleting returns state it means it had make it
            // completing and had recorded exception
            finalState = cancelMakeCompleting(cause)
            if (finalState === COMPLETING_WAITING_CHILDREN) return true
        }
        if (finalState === COMPLETING_ALREADY) {
            finalState = makeCancelling(cause)
        }
        return when {
            finalState === COMPLETING_ALREADY -> true
            finalState === COMPLETING_WAITING_CHILDREN -> true
            finalState === TOO_LATE_TO_CANCEL -> false
            else -> {
                afterCompletion(finalState)
                true
            }
        }
    }
    ...
    
    @Suppress("NOTHING_TO_INLINE") // Save a stack frame
    internal inline fun defaultCancellationException(message: String? = null, cause: Throwable? = null) =
        JobCancellationException(message ?: cancellationExceptionMessage(), cause, this)
       
    override fun getChildJobCancellationCause(): CancellationException {
        // determine root cancellation cause of this job (why is it cancelling its children?)
        val state = this.state
        val rootCause = when (state) {
            is Finishing -> state.rootCause
            is CompletedExceptionally -> state.cause
            is Incomplete -> error("Cannot be cancelling child in this state: $state")
            else -> null // create exception with the below code on normal completion
        }
        return (rootCause as? CancellationException) ?: JobCancellationException("Parent job is ${stateString(state)}", rootCause, this)
    }

    // cause is Throwable or ParentJob when cancelChild was invoked
    // 如果参数cause属于`Throwable`，则说明是当前协程发起取消请求；如果属于`ParentJob`，则说明是
    // 当前协程作为子协程，处理父协程传递过来的取消请求。
    private fun createCauseException(cause: Any?): Throwable = when (cause) {
        is Throwable? -> cause ?: defaultCancellationException()
        else -> (cause as ParentJob).getChildJobCancellationCause()
    }

    // transitions to Cancelling state
    // cause is Throwable or ParentJob when cancelChild was invoked
    // It contains a loop and never returns COMPLETING_RETRY, can return
    // COMPLETING_ALREADY -- if already completing or successfully made cancelling, added exception
    // COMPLETING_WAITING_CHILDREN -- if started waiting for children, added exception
    // TOO_LATE_TO_CANCEL -- too late to cancel, did not add exception
    // final state -- when completed, for call to afterCompletion
    private fun makeCancelling(cause: Any?): Any? {
        var causeExceptionCache: Throwable? = null // lazily init result of createCauseException(cause)
        loopOnState { state ->
            when (state) {
                is Finishing -> { // already finishing -- collect exceptions
                    val notifyRootCause = synchronized(state) {
                        if (state.isSealed) return TOO_LATE_TO_CANCEL // already sealed -- cannot add exception nor mark cancelled
                        // add exception, do nothing is parent is cancelling child that is already being cancelled
                        val wasCancelling = state.isCancelling // will notify if was not cancelling
                        // Materialize missing exception if it is the first exception (otherwise -- don't)
                        if (cause != null || !wasCancelling) {
                            val causeException = causeExceptionCache ?: createCauseException(cause).also { causeExceptionCache = it }
                            state.addExceptionLocked(causeException)
                        }
                        // take cause for notification if was not in cancelling state before
                        state.rootCause.takeIf { !wasCancelling }
                    }
                    notifyRootCause?.let { notifyCancelling(state.list, it) }
                    return COMPLETING_ALREADY
                }
                is Incomplete -> {//如果当前协程状态属于`InComplete`，
                    // Not yet finishing -- try to make it cancelling
                    val causeException = causeExceptionCache ?: createCauseException(cause).also { causeExceptionCache = it }
                    if (state.isActive) {
                        // active state becomes cancelling
                        if (tryMakeCancelling(state, causeException)) return COMPLETING_ALREADY
                    } else {
                        // non active state starts completing
                        val finalState = tryMakeCompleting(state, CompletedExceptionally(causeException))
                        when {
                            finalState === COMPLETING_ALREADY -> error("Cannot happen in $state")
                            finalState === COMPLETING_RETRY -> return@loopOnState
                            else -> return finalState
                        }
                    }
                }
                else -> return TOO_LATE_TO_CANCEL // already complete
            }
        }
    }

    // Performs promotion of incomplete coroutine state to NodeList for the purpose of
    // converting coroutine state to Cancelling, returns null when need to retry
    private fun getOrPromoteCancellingList(state: Incomplete): NodeList? = state.list ?:
        when (state) {
            is Empty -> NodeList() // we can allocate new empty list that'll get integrated into Cancelling state
            is JobNode -> {
                // SINGLE/SINGLE+ must be promoted to NodeList first, because otherwise we cannot
                // correctly capture a reference to it
                promoteSingleToNodeList(state)
                null // retry
            }
            else -> error("State should have list: $state")
        }

    // try make new Cancelling state on the condition that we're still in the expected state
    private fun tryMakeCancelling(state: Incomplete, rootCause: Throwable): Boolean {
        assert { state !is Finishing } // only for non-finishing states
        assert { state.isActive } // only for active states
        // get state's list or else promote to list to correctly operate on child lists
        val list = getOrPromoteCancellingList(state) ?: return false
        // 设置当前协程的状态为`Finishing`
        // Create cancelling state (with rootCause!)
        val cancelling = Finishing(list, false, rootCause)
        if (!_state.compareAndSet(state, cancelling)) return false
        // Notify listeners
        notifyCancelling(list, rootCause)
        return true
    }
    
    ...
    /**
     * This function is invoked once as soon as this job is being cancelled for any reason or completes,
     * similarly to [invokeOnCompletion] with `onCancelling` set to `true`.
     *
     * The meaning of [cause] parameter:
     * * Cause is `null` when the job has completed normally.
     * * Cause is an instance of [CancellationException] when the job was cancelled _normally_.
     *   **It should not be treated as an error**. In particular, it should not be reported to error logs.
     * * Otherwise, the job had been cancelled or failed with exception.
     *
     * The specified [cause] is not the final cancellation cause of this job.
     * A job may produce other exceptions while it is failing and the final cause might be different.
     *
     * @suppress **This is unstable API and it is subject to change.*
     */
    protected open fun onCancelling(cause: Throwable?) {}

    /**
     * Returns `true` for scoped coroutines.
     * Scoped coroutine is a coroutine that is executed sequentially within the enclosing scope without any concurrency.
     * Scoped coroutines always handle any exception happened within -- they just rethrow it to the enclosing scope.
     * Examples of scoped coroutines are `coroutineScope`, `withTimeout` and `runBlocking`.
     */
    protected open val isScopedCoroutine: Boolean get() = false

    /**
     * Returns `true` for jobs that handle their exceptions or integrate them into the job's result via [onCompletionInternal].
     * A valid implementation of this getter should recursively check parent as well before returning `false`.
     *
     * The only instance of the [Job] that does not handle its exceptions is [JobImpl] and its subclass [SupervisorJobImpl].
     * @suppress **This is unstable API and it is subject to change.*
     */
    internal open val handlesException: Boolean get() = true
    
    ...
    // Completing & Cancelling states,
    // All updates are guarded by synchronized(this), reads are volatile
    @Suppress("UNCHECKED_CAST")
    private class Finishing(
        override val list: NodeList,
        isCompleting: Boolean,
        rootCause: Throwable?
    ) : SynchronizedObject(), Incomplete {
        private val _isCompleting = atomic(isCompleting)
        var isCompleting: Boolean
            get() = _isCompleting.value
            set(value) { _isCompleting.value = value }

        private val _rootCause = atomic(rootCause)
        var rootCause: Throwable? // NOTE: rootCause is kept even when SEALED
            get() = _rootCause.value
            set(value) { _rootCause.value = value }

        private val _exceptionsHolder = atomic<Any?>(null)
        private var exceptionsHolder: Any? // Contains null | Throwable | ArrayList | SEALED
            get() = _exceptionsHolder.value
            set(value) { _exceptionsHolder.value = value }

        // Note: cannot be modified when sealed
        val isSealed: Boolean get() = exceptionsHolder === SEALED
        val isCancelling: Boolean get() = rootCause != null
        override val isActive: Boolean get() = rootCause == null // !isCancelling
        ...
    }

}

...
internal class ChildHandleNode(
    @JvmField val childJob: ChildJob
) : JobCancellingNode(), ChildHandle {
    override val parent: Job get() = job
    // 子协程接收到父协程传递过来的取消请求时，会调用该函数。
    override fun invoke(cause: Throwable?) = childJob.parentCancelled(job)
    // 父协程接收到子协程的取消请求会调用这个函数。
    override fun childCancelled(cause: Throwable): Boolean = job.childCancelled(cause)
}
    
    

```

协程通过调用`Job.cancel(cause: CancellationException?)`函数发起取消的请求，取消协程的主要实现步骤如下：

1. 通过函数链调用`cancel -> cancelInternal -> cancelImpl -> makeCancelling -> tryMakeCancelling`，设置协程的状态为`cancelling`（协程的`Job`成员变量`state`为`Finishing`）。
   + 如果取消协程的参数`cause`为`null`，则构造`JobCancellationException`作为取消过程中的`cause`参数。
2. 将请求传递给所有的子协程。然后通过遍历子协程的链表，通过调用`ChildHandleNode.invoke(cause: Throwable?)`函数，父协程将取消的请求传递给所有的子协程，子协程再通过递归调用的方式处理取消协程的请求。
3. 执行条件判断：当前协程是否是作用域内的协程；当前协程的`parentHandle`是否为`null`或者是否为`NonDisposableHandle`；取消协程的`cause`是否属于`CancellationException`。
   + 这三个条件如果至少一个为真，则不将取消请求传递给父协程；否则将取消请求传递给父协程。
   + 对于取消协程的场景，`cause`默认属于`CancellationException`，所以取消协程，不会取消父协程。



### 19.8.3 协程取消实际代码调试

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
    println("job1 cancel.")
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
    println("job2 cancel.")
    job2.cancel()
}
```

以上代码实际运行情况

```shell
job1 sleep 1 times
job1 sleep 2 times
job1 cancel.
job2 sleep 1 ...
job2 sleep 2 ...
job2 cancel.
job2 sleep 3 ...
job2 sleep 4 ...
job2 sleep 5 ...
```

协程的取消，`Job#cancel`只是将协程的状态修改为`Canceling`状态，并不能取消协程的运算逻辑，协程库中很多挂起函数都会检测协程状态，比如`delay`这个顶层的挂起函数就会检查协程的取消状态，所以`job1`在取消后，协程的运算也结束了，但是`job2`在运算过程中不检查协程的取消状态，所以在`job2`取消后，协程的运算并未取消。如果想及时取消协程的运算，最好使用`isActive`判断协程状态,
作为结束协程运算逻辑的条件。



## 19.9 协程必须等待所有的子协程完成才能完成

调试代码

```kotlin
fun main(args: Array<String>): Unit = runBlocking {
    val parentJob = launch {
        val childJob = async {
            delay(30_000L)
            // 执行完这次打印之后会调用`AbstractCoroutine#resumeWith`函数。
            println("childJob end.")
        }
        // 执行完这次打印之后会调用`AbstractCoroutine#resumeWith`函数。
        println("parentJob end.")
    }
    // 执行完这次打印之后会调用`AbstractCoroutine#resumeWith`函数。
    println("runBlocking end.")

}
```

如上代码，在每一次执行完打印代码后都会执行一次对应协程第一层封装的`AbstractCoroutine#resumeWith`函数。先执行最外层`runBlocking-BlockingCoroutine`协程的，在执行`parentJob-StandaloneCoroutine`协程的，最后执行`childJob-DeferredCoroutine`协程的。

kotlinx-coroutines-core-jvm-1.7.3-sources\kotlinx\coroutines\AbstractCoroutine.kt

```kotlin
@InternalCoroutinesApi
public abstract class AbstractCoroutine<in T>(
    parentContext: CoroutineContext,
    initParentJob: Boolean,
    active: Boolean
) : JobSupport(active), Job, Continuation<T>, CoroutineScope {
	...
    /**
     * Completes execution of this with coroutine with the specified result.
     */
    // 协程第二次封装的`Continuation#resumeWith`函数最后会调用第一层封装的`AbstractCoroutine#resumeWith`函数。
    public final override fun resumeWith(result: Result<T>) {
        val state = makeCompletingOnce(result.toState())
        if (state === COMPLETING_WAITING_CHILDREN) return
        afterResume(state)
    }

    protected open fun afterResume(state: Any?): Unit = afterCompletion(state)
    
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\JobSupport.kt

```kotlin
@Deprecated(level = DeprecationLevel.ERROR, message = "This is internal API and may be removed in the future releases")
public open class JobSupport constructor(active: Boolean) : Job, ChildJob, ParentJob {
    ...
    private val _state = atomic<Any?>(if (active) EMPTY_ACTIVE else EMPTY_NEW)

    private val _parentHandle = atomic<ChildHandle?>(null)
    internal var parentHandle: ChildHandle?
        get() = _parentHandle.value
        set(value) { _parentHandle.value = value }

    override val parent: Job?
        get() = parentHandle?.parent
    ...
    // ------------ state query ------------
    /**
     * Returns current state of this job.
     * If final state of the job is [Incomplete], then it is boxed into [IncompleteStateBox]
     * and should be [unboxed][unboxState] before returning to user code.
     */
    internal val state: Any? get() {
        _state.loop { state -> // helper loop on state (complete in-progress atomic operations)
            if (state !is OpDescriptor) return state
            state.perform(this)
        }
    }

    /**
     * @suppress **This is unstable API and it is subject to change.**
     */
    private inline fun loopOnState(block: (Any?) -> Unit): Nothing {
        while (true) {
            block(state)
        }
    }
    ...
        // ------------ state update ------------

    // Finalizes Finishing -> Completed (terminal state) transition.
    // ## IMPORTANT INVARIANT: Only one thread can be concurrently invoking this method.
    // Returns final state that was created and updated to
    private fun finalizeFinishingState(state: Finishing, proposedUpdate: Any?): Any? {
        /*
         * Note: proposed state can be Incomplete, e.g.
         * async {
         *     something.invokeOnCompletion {} // <- returns handle which implements Incomplete under the hood
         * }
         */
        assert { this.state === state } // consistency check -- it cannot change
        assert { !state.isSealed } // consistency check -- cannot be sealed yet
        assert { state.isCompleting } // consistency check -- must be marked as completing
        val proposedException = (proposedUpdate as? CompletedExceptionally)?.cause
        // Create the final exception and seal the state so that no more exceptions can be added
        val wasCancelling: Boolean
        val finalException = synchronized(state) {
            wasCancelling = state.isCancelling
            val exceptions = state.sealLocked(proposedException)
            val finalCause = getFinalRootCause(state, exceptions)
            if (finalCause != null) addSuppressedExceptions(finalCause, exceptions)
            finalCause
        }
        // Create the final state object
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
    ...
    // suppressed == true when any exceptions were suppressed while building the final completion cause
    private fun completeStateFinalization(state: Incomplete, update: Any?) {
        /*
         * Now the job in THE FINAL state. We need to properly handle the resulting state.
         * Order of various invocations here is important.
         *
         * 1) Unregister from parent job.
         */
        parentHandle?.let {
            it.dispose() // volatile read parentHandle _after_ state was updated
            parentHandle = NonDisposableHandle // release it just in case, to aid GC
        }
        val cause = (update as? CompletedExceptionally)?.cause
        /*
         * 2) Invoke completion handlers: .join(), callbacks etc.
         *    It's important to invoke them only AFTER exception handling and everything else, see #208
         */
        if (state is JobNode) { // SINGLE/SINGLE+ state -- one completion handler (common case)
            try {
                state.invoke(cause)
            } catch (ex: Throwable) {
                handleOnCompletionException(CompletionHandlerException("Exception in completion handler $state for $this", ex))
            }
        } else {
            state.list?.notifyCompletion(cause)
        }
    }
    ...
    private fun NodeList.notifyCompletion(cause: Throwable?) =
        notifyHandlers<JobNode>(this, cause)

    private inline fun <reified T: JobNode> notifyHandlers(list: NodeList, cause: Throwable?) {
        var exception: Throwable? = null
        list.forEach<T> { node ->
            try {
                node.invoke(cause)
            } catch (ex: Throwable) {
                exception?.apply { addSuppressedThrowable(ex) } ?: run {
                    exception =  CompletionHandlerException("Exception in completion handler $node for $this", ex)
                }
            }
        }
        exception?.let { handleOnCompletionException(it) }
    }
    ...
    @Suppress("OverridingDeprecatedMember")
    public final override fun invokeOnCompletion(handler: CompletionHandler): DisposableHandle =
        invokeOnCompletion(onCancelling = false, invokeImmediately = true, handler = handler)

    public final override fun invokeOnCompletion(
        onCancelling: Boolean,
        invokeImmediately: Boolean,
        handler: CompletionHandler
    ): DisposableHandle {
        // Create node upfront -- for common cases it just initializes JobNode.job field,
        // for user-defined handlers it allocates a JobNode object that we might not need, but this is Ok.
        val node: JobNode = makeNode(handler, onCancelling)
        loopOnState { state ->
            when (state) {
                is Empty -> { // EMPTY_X state -- no completion handlers
                    if (state.isActive) {
                        // try move to SINGLE state
                        if (_state.compareAndSet(state, node)) return node
                    } else
                        promoteEmptyToNodeList(state) // that way we can add listener for non-active coroutine
                }
                is Incomplete -> {
                    val list = state.list
                    if (list == null) { // SINGLE/SINGLE+
                        promoteSingleToNodeList(state as JobNode)
                    } else {
                        var rootCause: Throwable? = null
                        var handle: DisposableHandle = NonDisposableHandle
                        if (onCancelling && state is Finishing) {
                            synchronized(state) {
                                // check if we are installing cancellation handler on job that is being cancelled
                                rootCause = state.rootCause // != null if cancelling job
                                // We add node to the list in two cases --- either the job is not being cancelled
                                // or we are adding a child to a coroutine that is not completing yet
                                if (rootCause == null || handler.isHandlerOf<ChildHandleNode>() && !state.isCompleting) {
                                    // Note: add node the list while holding lock on state (make sure it cannot change)
                                    if (!addLastAtomic(state, list, node)) return@loopOnState // retry
                                    // just return node if we don't have to invoke handler (not cancelling yet)
                                    if (rootCause == null) return node
                                    // otherwise handler is invoked immediately out of the synchronized section & handle returned
                                    handle = node
                                }
                            }
                        }
                        if (rootCause != null) {
                            // Note: attachChild uses invokeImmediately, so it gets invoked when adding to cancelled job
                            if (invokeImmediately) handler.invokeIt(rootCause)
                            return handle
                        } else {
                            if (addLastAtomic(state, list, node)) return node
                        }
                    }
                }
                else -> { // is complete
                    // :KLUDGE: We have to invoke a handler in platform-specific way via `invokeIt` extension,
                    // because we play type tricks on Kotlin/JS and handler is not necessarily a function there
                    if (invokeImmediately) handler.invokeIt((state as? CompletedExceptionally)?.cause)
                    return NonDisposableHandle
                }
            }
        }
    }

    private fun makeNode(handler: CompletionHandler, onCancelling: Boolean): JobNode {
        val node = if (onCancelling) {
            (handler as? JobCancellingNode)
                ?: InvokeOnCancelling(handler)
        } else {
            (handler as? JobNode)
                ?.also { assert { it !is JobCancellingNode } }
                ?: InvokeOnCompletion(handler)
        }
        node.job = this
        return node
    }
    ...
    // Performs promotion of incomplete coroutine state to NodeList for the purpose of
    // converting coroutine state to Cancelling, returns null when need to retry
    private fun getOrPromoteCancellingList(state: Incomplete): NodeList? = state.list ?:
        when (state) {
            is Empty -> NodeList() // we can allocate new empty list that'll get integrated into Cancelling state
            is JobNode -> {
                // SINGLE/SINGLE+ must be promoted to NodeList first, because otherwise we cannot
                // correctly capture a reference to it
                promoteSingleToNodeList(state)
                null // retry
            }
            else -> error("State should have list: $state")
        }
    ...
    /**
     * Completes this job. Used by [AbstractCoroutine.resume].
     * It throws [IllegalStateException] on repeated invocation (when this job is already completing).
     * Returns:
     * * [COMPLETING_WAITING_CHILDREN] if started waiting for children.
     * * Final state otherwise (caller should do [afterCompletion])
     */
    internal fun makeCompletingOnce(proposedUpdate: Any?): Any? {
        loopOnState { state ->
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
    
    // Returns one of COMPLETING symbols or final state:
    // COMPLETING_ALREADY -- when already complete or completing
    // COMPLETING_RETRY -- when need to retry due to interference
    // COMPLETING_WAITING_CHILDREN -- when made completing and is waiting for children
    // final state -- when completed, for call to afterCompletion
    private fun tryMakeCompleting(state: Any?, proposedUpdate: Any?): Any? {
        if (state !is Incomplete)
            return COMPLETING_ALREADY
        /*
         * FAST PATH -- no children to wait for && simple state (no list) && not cancelling => can complete immediately
         * Cancellation (failures) always have to go through Finishing state to serialize exception handling.
         * Otherwise, there can be a race between (completed state -> handled exception and newly attached child/join)
         * which may miss unhandled exception.
         */
        if ((state is Empty || state is JobNode) && state !is ChildHandleNode && proposedUpdate !is CompletedExceptionally) {
            if (tryFinalizeSimpleState(state, proposedUpdate)) {
                // Completed successfully on fast path -- return updated state
                return proposedUpdate
            }
            return COMPLETING_RETRY
        }
        // The separate slow-path function to simplify profiling
        return tryMakeCompletingSlowPath(state, proposedUpdate)
    }

    // Returns one of COMPLETING symbols or final state:
    // COMPLETING_ALREADY -- when already complete or completing
    // COMPLETING_RETRY -- when need to retry due to interference
    // COMPLETING_WAITING_CHILDREN -- when made completing and is waiting for children
    // final state -- when completed, for call to afterCompletion
    private fun tryMakeCompletingSlowPath(state: Incomplete, proposedUpdate: Any?): Any? {
        // get state's list or else promote to list to correctly operate on child lists
        val list = getOrPromoteCancellingList(state) ?: return COMPLETING_RETRY
        // promote to Finishing state if we are not in it yet
        // This promotion has to be atomic w.r.t to state change, so that a coroutine that is not active yet
        // atomically transition to finishing & completing state
        // 构造`Finishing`，isCompleting为true。然后设置当前线程的`state`为该`Finishing`。
        val finishing = state as? Finishing ?: Finishing(list, false, null)
        // must synchronize updates to finishing state
        var notifyRootCause: Throwable? = null
        synchronized(finishing) {
            // check if this state is already completing
            if (finishing.isCompleting) return COMPLETING_ALREADY
            // mark as completing
            finishing.isCompleting = true
            // if we need to promote to finishing then atomically do it here.
            // We do it as early is possible while still holding the lock. This ensures that we cancelImpl asap
            // (if somebody else is faster) and we synchronize all the threads on this finishing lock asap.
            if (finishing !== state) {
                if (!_state.compareAndSet(state, finishing)) return COMPLETING_RETRY
            }
            // ## IMPORTANT INVARIANT: Only one thread (that had set isCompleting) can go past this point
            assert { !finishing.isSealed } // cannot be sealed
            // add new proposed exception to the finishing state
            val wasCancelling = finishing.isCancelling
            (proposedUpdate as? CompletedExceptionally)?.let { finishing.addExceptionLocked(it.cause) }
            // If it just becomes cancelling --> must process cancelling notifications
            notifyRootCause = finishing.rootCause.takeIf { !wasCancelling }
        }
        // process cancelling notification here -- it cancels all the children _before_ we start to to wait them (sic!!!)
        notifyRootCause?.let { notifyCancelling(list, it) }
        // 根据`state`获取一个`ChildHandleNode`句柄，在协程`Job`构造的过程中建立了协程之间parent-child关系，
        // 并构造了持有父协程、子协程引用的`ChildHandleNode`句柄，且将该`ChildHandleNode`添加到了协程`Job`的
        // `state.list`的链表上。
        // 如果有`ChildHandleNode`，则`tryWaitForChild`返回true，函数return，结果为`COMPLETING_WAITING_CHILDREN`
        // 需要等待子协程的完成。否则说明子协程完成了，调用`finalizeFinishingState`函数。
        // now wait for children
        val child = firstChild(state)
        if (child != null && tryWaitForChild(finishing, child, proposedUpdate))
            return COMPLETING_WAITING_CHILDREN
        // otherwise -- we have not children left (all were already cancelled?)
        return finalizeFinishingState(finishing, proposedUpdate)
    }
    ...
    private fun firstChild(state: Incomplete) =
        state as? ChildHandleNode ?: state.list?.nextChild()

    // return false when there is no more incomplete children to wait
    // ## IMPORTANT INVARIANT: Only one thread can be concurrently invoking this method.
    private tailrec fun tryWaitForChild(state: Finishing, child: ChildHandleNode, proposedUpdate: Any?): Boolean {
        val handle = child.childJob.invokeOnCompletion(
            invokeImmediately = false,
            handler = ChildCompletion(this, state, child, proposedUpdate).asHandler
        )
        if (handle !== NonDisposableHandle) return true // child is not complete and we've started waiting for it
        val nextChild = child.nextChild() ?: return false
        return tryWaitForChild(state, nextChild, proposedUpdate)
    }

    // ## IMPORTANT INVARIANT: Only one thread can be concurrently invoking this method.
    private fun continueCompleting(state: Finishing, lastChild: ChildHandleNode, proposedUpdate: Any?) {
        assert { this.state === state } // consistency check -- it cannot change while we are waiting for children
        // figure out if we need to wait for next child
        val waitChild = lastChild.nextChild()
        // try wait for next child
        if (waitChild != null && tryWaitForChild(state, waitChild, proposedUpdate)) return // waiting for next child
        // no more children to wait -- try update state
        val finalState = finalizeFinishingState(state, proposedUpdate)
        afterCompletion(finalState)
    }

    private fun LockFreeLinkedListNode.nextChild(): ChildHandleNode? {
        var cur = this
        while (cur.isRemoved) cur = cur.prevNode // rollback to prev non-removed (or list head)
        while (true) {
            cur = cur.nextNode
            if (cur.isRemoved) continue
            if (cur is ChildHandleNode) return cur
            if (cur is NodeList) return null // checked all -- no more children
        }
    }
    ...
    /**
     * This function is invoked once as soon as this job is being cancelled for any reason or completes,
     * similarly to [invokeOnCompletion] with `onCancelling` set to `true`.
     *
     * The meaning of [cause] parameter:
     * * Cause is `null` when the job has completed normally.
     * * Cause is an instance of [CancellationException] when the job was cancelled _normally_.
     *   **It should not be treated as an error**. In particular, it should not be reported to error logs.
     * * Otherwise, the job had been cancelled or failed with exception.
     *
     * The specified [cause] is not the final cancellation cause of this job.
     * A job may produce other exceptions while it is failing and the final cause might be different.
     *
     * @suppress **This is unstable API and it is subject to change.*
     */
    protected open fun onCancelling(cause: Throwable?) {}
    ...
    /**
     * Override for completion actions that need to update some external object depending on job's state,
     * right before all the waiters for coroutine's completion are notified.
     *
     * @param state the final state.
     *
     * @suppress **This is unstable API and it is subject to change.**
     */
    protected open fun onCompletionInternal(state: Any?) {}

    /**
     * Override for the very last action on job's completion to resume the rest of the code in
     * scoped coroutines. It is called when this job is externally completed in an unknown
     * context and thus should resume with a default mode.
     *
     * @suppress **This is unstable API and it is subject to change.**
     */
    protected open fun afterCompletion(state: Any?) {}
    
    ...
    // Completing & Cancelling states,
    // All updates are guarded by synchronized(this), reads are volatile
    @Suppress("UNCHECKED_CAST")
    private class Finishing(
        override val list: NodeList,
        isCompleting: Boolean,
        rootCause: Throwable?
    ) : SynchronizedObject(), Incomplete {
        private val _isCompleting = atomic(isCompleting)
        var isCompleting: Boolean
            get() = _isCompleting.value
            set(value) { _isCompleting.value = value }

        private val _rootCause = atomic(rootCause)
        var rootCause: Throwable? // NOTE: rootCause is kept even when SEALED
            get() = _rootCause.value
            set(value) { _rootCause.value = value }

        private val _exceptionsHolder = atomic<Any?>(null)
        private var exceptionsHolder: Any? // Contains null | Throwable | ArrayList | SEALED
            get() = _exceptionsHolder.value
            set(value) { _exceptionsHolder.value = value }

        // Note: cannot be modified when sealed
        val isSealed: Boolean get() = exceptionsHolder === SEALED
        val isCancelling: Boolean get() = rootCause != null
        override val isActive: Boolean get() = rootCause == null // !isCancelling       
        ...
    }
    
    private val Incomplete.isCancelling: Boolean
        get() = this is Finishing && isCancelling

    // 该处理器会被注册到指定协程内，添加到协程`Job`的`state.list`的链表上。
    // Used by parent that is waiting for child completion
    private class ChildCompletion(
        private val parent: JobSupport,
        private val state: Finishing,
        private val child: ChildHandleNode,
        private val proposedUpdate: Any?
    ) : JobNode() {
        override fun invoke(cause: Throwable?) {
            parent.continueCompleting(state, child, proposedUpdate)
        }
    }

    
   
    
}

...
// --------------- helper classes & constants for job implementation

private val COMPLETING_ALREADY = Symbol("COMPLETING_ALREADY")
@JvmField
internal val COMPLETING_WAITING_CHILDREN = Symbol("COMPLETING_WAITING_CHILDREN")
private val COMPLETING_RETRY = Symbol("COMPLETING_RETRY")
private val TOO_LATE_TO_CANCEL = Symbol("TOO_LATE_TO_CANCEL")
...
internal interface Incomplete {
    val isActive: Boolean
    val list: NodeList? // is null only for Empty and JobNode incomplete state objects
}

internal abstract class JobNode : CompletionHandlerBase(), DisposableHandle, Incomplete {
    /**
     * Initialized by [JobSupport.makeNode].
     */
    lateinit var job: JobSupport
    override val isActive: Boolean get() = true
    override val list: NodeList? get() = null
    override fun dispose() = job.removeNode(this)
    override fun toString() = "$classSimpleName@$hexAddress[job@${job.hexAddress}]"
}

internal class NodeList : LockFreeLinkedListHead(), Incomplete {
    override val isActive: Boolean get() = true
    override val list: NodeList get() = this

    fun getString(state: String) = buildString {
        append("List{")
        append(state)
        append("}[")
        var first = true
        this@NodeList.forEach<JobNode> { node ->
            if (first) first = false else append(", ")
            append(node)
        }
        append("]")
    }

    override fun toString(): String =
        if (DEBUG) getString("Active") else super.toString()
}
...
/**
 * Marker for node that shall be invoked on in _cancelling_ state.
 * **Note: may be invoked multiple times.**
 */
internal abstract class JobCancellingNode : JobNode()
...
internal class ChildHandleNode(
    @JvmField val childJob: ChildJob
) : JobCancellingNode(), ChildHandle {
    override val parent: Job get() = job
    override fun invoke(cause: Throwable?) = childJob.parentCancelled(job)
    override fun childCancelled(cause: Throwable): Boolean = job.childCancelled(cause)
}

```

协程完成流程的主要实现步骤如下：

1. 协程第二次封装的`Continuation#resumeWith`函数最后会调用第一层封装的`AbstractCoroutine#resumeWith`函数。通过`AbstractCoroutine#resumeWith`函数执行协程的完成流程。`result`参数为协程体的返回值，比如`Unit`。

2. 该函数会调用`makeCompletingOnce`函数，如果函数的结果为`COMPLETING_WAITING_CHILDREN`说明需要等待子协程的完成，否则说明子协程完成了，且当前协程也完成了。

3. `makeCompletingOnce`函数会通过函数链调用`tryMakeCompleting -> tryMakeCompletingSlowPath`继续执行协程的完成流程。

4. `tryMakeCompletingSlowPath`函数主要实现步骤如下：

   1. 设置协程的状态为`Completing`（通过设置`_state`为`Finishing`实现）。

   2. 获取当前协程已注册的第一个子协程处理器`ChildHandleNode`，该处理器持有当前协程和子协程的引用。判断子协程处理器是否存在。

      + 存在，说明需要等待子协程的完成，则调用`tryWaitForChild`继续处理，函数返回`COMPLETING_WAITING_CHILDREN`。`tryWaitForChild`函数的主要实现步骤如下：

        ​	由当先协程的`Job`、`ChildHandleNode`、当前协程`Finishing(completing = true)`状态、协程的结果构造子协程完成处理器`ChildCompletion`对象，将`ChildCompletion`添加到对应子协程`Job`的`state.list`链表上。`ChildCompletion`是协程完成的处理器，它是`ChildHandleNode`的包装，持有父协程的引用。

      + 不存在，说明子协程完成了或者没有子协程，不等待，则调用`finalizeFinishingState`继续处理，该函数的主要实现步骤如下：

        1. 设置协程的状态为已完成（通过设置`state`为协程的结果）。
        2. 通知当前协程注册的完成处理器当前协程完成了。这个处理器属于`ChildCompletion`，然后通过持有的父协程引用调用`continueCompleting`函数通知父协程当前协程完成了。
        3. 父协程寻找当前协程对应的`ChildHandleNode`的下一个`ChildHandleNode`，也就是同级的协程对应的`ChildHandleNode`。判断`ChildHandleNode`是否不为`null`。如果不为`null`就需要等待，否则不等待完成协程的完成流程。

**小结**

协程需要等待子协程的完成才能完成，子协程完成后，会通过`ChildCompletion`完成处理器通知父协程，然后父协程会继续等待另一个子协程的完成，直到所有的子协程完成。协程在执行完成流程时，状态会转换为`Completing`，然后等待子协程的完成，当所有的子协程完成后，协程状态转换为`Completed`。

## 19.10 协程运行过程中抛出未捕获的异常

相似的分析见小节[19.12.6 协程的异常处理流程分析]

协程的运算在第二次包装的`Continuation`中。

kotlin-stdlib-1.8.20\kotlin\coroutines\jvm\internal\CoroutineImpl.kt

```kotlin
@SinceKotlin("1.3")
internal abstract class BaseContinuationImpl(
    // This is `public val` so that it is private on JVM and cannot be modified by untrusted code, yet
    // it has a public getter (since even untrusted code is allowed to inspect its call stack).
    public val completion: Continuation<Any?>?
) : Continuation<Any?>, CoroutineStackFrame, Serializable {
    // This implementation is final. This fact is used to unroll resumeWith recursion.
    public final override fun resumeWith(result: Result<Any?>) {
        // This loop unrolls recursion in current.resumeWith(param) to make saner and shorter stack traces on resume
        var current = this
        var param = result
        while (true) {
            // Invoke "resume" debug probe on every resumed continuation, so that a debugging library infrastructure
            // can precisely track what part of suspended callstack was already resumed
            probeCoroutineResumed(current)
            with(current) {
                val completion = completion!! // fail fast when trying to resume continuation without completion
                val outcome: Result<Any?> =
                    try {
                        // 调用`invokeSuspend`函数，执行协程的运算。
                        val outcome = invokeSuspend(param)
                        if (outcome === COROUTINE_SUSPENDED) return
                        Result.success(outcome)
                    } catch (exception: Throwable) {
                        // 协程运行过程中抛出未捕获的异常，会在这里捕获到，异常作为失败结果，
                        Result.failure(exception)
                    }
                releaseIntercepted() // this state machine instance is terminating
                if (completion is BaseContinuationImpl) {
                    // unrolling recursion via loop
                    current = completion
                    param = outcome
                } else {
                    // top-level completion reached -- invoke and return
                    // 调用第二层封装的`Continuation#resumeWith`函数开始协程的完成流程。
                    completion.resumeWith(outcome)
                    return
                }
            }
        }
    }
    ...
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\CompletionState.kt

```kotlin
internal fun <T> Result<T>.toState(
    onCancellation: ((cause: Throwable) -> Unit)? = null
): Any? = fold(
    onSuccess = { if (onCancellation != null) CompletedWithCancellation(it, onCancellation) else it },
    onFailure = { CompletedExceptionally(it) }
)
...
/**
 * Class for an internal state of a job that was cancelled (completed exceptionally).
 *
 * @param cause the exceptional completion cause. It's either original exceptional cause
 *        or artificial [CancellationException] if no cause was provided
 */
internal open class CompletedExceptionally(
    @JvmField val cause: Throwable,
    handled: Boolean = false
) {
    private val _handled = atomic(handled)
    val handled: Boolean get() = _handled.value
    fun makeHandled(): Boolean = _handled.compareAndSet(false, true)
    override fun toString(): String = "$classSimpleName[$cause]"
}
```

kotlin-stdlib-1.8.20\kotlin\utli\Result.kt

```kotlin
@SinceKotlin("1.3")
@JvmInline
public value class Result<out T> @PublishedApi internal constructor(
    @PublishedApi
    internal val value: Any?
) : Serializable {
    // discovery

    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] returns `false`.
     */
    public val isSuccess: Boolean get() = value !is Failure

    /**
     * Returns `true` if this instance represents a failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    public val isFailure: Boolean get() = value is Failure
    ...
    public fun exceptionOrNull(): Throwable? =
        when (value) {
            is Failure -> value.exception
            else -> null
        }
    ...
    /**
     * Companion object for [Result] class that contains its constructor functions
     * [success] and [failure].
     */
    public companion object {
        /**
         * Returns an instance that encapsulates the given [value] as successful value.
         */
        @Suppress("INAPPLICABLE_JVM_NAME")
        @InlineOnly
        @JvmName("success")
        public inline fun <T> success(value: T): Result<T> =
            Result(value)

        /**
         * Returns an instance that encapsulates the given [Throwable] [exception] as failure.
         */
        @Suppress("INAPPLICABLE_JVM_NAME")
        @InlineOnly
        @JvmName("failure")
        public inline fun <T> failure(exception: Throwable): Result<T> =
            Result(createFailure(exception))
    }
    
    internal class Failure(
        @JvmField
        val exception: Throwable
    ) : Serializable {
        override fun equals(other: Any?): Boolean = other is Failure && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Failure($exception)"
    }
}

/**
 * Creates an instance of internal marker [Result.Failure] class to
 * make sure that this class is not exposed in ABI.
 */
@PublishedApi
@SinceKotlin("1.3")
internal fun createFailure(exception: Throwable): Any =
    Result.Failure(exception)
...
@InlineOnly
@SinceKotlin("1.3")
public inline fun <R, T> Result<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (exception: Throwable) -> R
): R {
    contract {
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }
    return when (val exception = exceptionOrNull()) {
        null -> onSuccess(value as T)
        else -> onFailure(exception)
    }
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\kotlinx\coroutines\AbstractCoroutine.kt

```kotlin
@InternalCoroutinesApi
public abstract class AbstractCoroutine<in T>(
    parentContext: CoroutineContext,
    initParentJob: Boolean,
    active: Boolean
) : JobSupport(active), Job, Continuation<T>, CoroutineScope {
	...
    /**
     * Completes execution of this with coroutine with the specified result.
     */
    // 协程第二次封装的`Continuation#resumeWith`函数最后会调用第一层封装的`AbstractCoroutine#resumeWith`函数。
    public final override fun resumeWith(result: Result<T>) {
        // 如果协程运行过程中出现了未捕获的异常，则`result`为`Result.Failure`，触发了协程异常完成的流程，
        // 会由异常构造`ComletedExceptionally`对象
        val state = makeCompletingOnce(result.toState())
        if (state === COMPLETING_WAITING_CHILDREN) return
        afterResume(state)
    }

    protected open fun afterResume(state: Any?): Unit = afterCompletion(state)
    
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\JobSupport.kt

```kotlin
@Deprecated(level = DeprecationLevel.ERROR, message = "This is internal API and may be removed in the future releases")
public open class JobSupport constructor(active: Boolean) : Job, ChildJob, ParentJob {
    ...
    private val _state = atomic<Any?>(if (active) EMPTY_ACTIVE else EMPTY_NEW)

    private val _parentHandle = atomic<ChildHandle?>(null)
    internal var parentHandle: ChildHandle?
        get() = _parentHandle.value
        set(value) { _parentHandle.value = value }

    override val parent: Job?
        get() = parentHandle?.parent
    ...
    // ------------ state query ------------
    /**
     * Returns current state of this job.
     * If final state of the job is [Incomplete], then it is boxed into [IncompleteStateBox]
     * and should be [unboxed][unboxState] before returning to user code.
     */
    internal val state: Any? get() {
        _state.loop { state -> // helper loop on state (complete in-progress atomic operations)
            if (state !is OpDescriptor) return state
            state.perform(this)
        }
    }

    /**
     * @suppress **This is unstable API and it is subject to change.**
     */
    private inline fun loopOnState(block: (Any?) -> Unit): Nothing {
        while (true) {
            block(state)
        }
    }
    ...
        // ------------ state update ------------

    // Finalizes Finishing -> Completed (terminal state) transition.
    // ## IMPORTANT INVARIANT: Only one thread can be concurrently invoking this method.
    // Returns final state that was created and updated to
    private fun finalizeFinishingState(state: Finishing, proposedUpdate: Any?): Any? {
        /*
         * Note: proposed state can be Incomplete, e.g.
         * async {
         *     something.invokeOnCompletion {} // <- returns handle which implements Incomplete under the hood
         * }
         */
        assert { this.state === state } // consistency check -- it cannot change
        assert { !state.isSealed } // consistency check -- cannot be sealed yet
        assert { state.isCompleting } // consistency check -- must be marked as completing
        val proposedException = (proposedUpdate as? CompletedExceptionally)?.cause
        // Create the final exception and seal the state so that no more exceptions can be added
        val wasCancelling: Boolean
        val finalException = synchronized(state) {
            wasCancelling = state.isCancelling
            val exceptions = state.sealLocked(proposedException)
            val finalCause = getFinalRootCause(state, exceptions)
            if (finalCause != null) addSuppressedExceptions(finalCause, exceptions)
            finalCause
        }
        // Create the final state object
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
    ...
    // suppressed == true when any exceptions were suppressed while building the final completion cause
    private fun completeStateFinalization(state: Incomplete, update: Any?) {
        /*
         * Now the job in THE FINAL state. We need to properly handle the resulting state.
         * Order of various invocations here is important.
         *
         * 1) Unregister from parent job.
         */
        parentHandle?.let {
            it.dispose() // volatile read parentHandle _after_ state was updated
            parentHandle = NonDisposableHandle // release it just in case, to aid GC
        }
        val cause = (update as? CompletedExceptionally)?.cause
        /*
         * 2) Invoke completion handlers: .join(), callbacks etc.
         *    It's important to invoke them only AFTER exception handling and everything else, see #208
         */
        if (state is JobNode) { // SINGLE/SINGLE+ state -- one completion handler (common case)
            try {
                state.invoke(cause)
            } catch (ex: Throwable) {
                handleOnCompletionException(CompletionHandlerException("Exception in completion handler $state for $this", ex))
            }
        } else {
            state.list?.notifyCompletion(cause)
        }
    }

    // 通知子协程取消，通知父协程取消
    private fun notifyCancelling(list: NodeList, cause: Throwable) {
        // first cancel our own children
        onCancelling(cause)
        notifyHandlers<JobCancellingNode>(list, cause)
        // then cancel parent
        cancelParent(cause) // tentative cancellation -- does not matter if there is no parent
    }

    /**
     * The method that is invoked when the job is cancelled to possibly propagate cancellation to the parent.
     * Returns `true` if the parent is responsible for handling the exception, `false` otherwise.
     *
     * Invariant: never returns `false` for instances of [CancellationException], otherwise such exception
     * may leak to the [CoroutineExceptionHandler].
     */
    private fun cancelParent(cause: Throwable): Boolean {
        // Is scoped coroutine -- don't propagate, will be rethrown
        if (isScopedCoroutine) return true

        /* CancellationException is considered "normal" and parent usually is not cancelled when child produces it.
         * This allow parent to cancel its children (normally) without being cancelled itself, unless
         * child crashes and produce some other exception during its completion.
         */
        // 异常完成的场景，`cause`不属于`CancellationException`
        val isCancellation = cause is CancellationException
        val parent = parentHandle
        // No parent -- ignore CE, report other exceptions.
        if (parent === null || parent === NonDisposableHandle) {
            return isCancellation
        }

        // Notify parent but don't forget to check cancellation
        return parent.childCancelled(cause) || isCancellation
    }
    
    private fun NodeList.notifyCompletion(cause: Throwable?) =
        notifyHandlers<JobNode>(this, cause)

    private inline fun <reified T: JobNode> notifyHandlers(list: NodeList, cause: Throwable?) {
        var exception: Throwable? = null
        list.forEach<T> { node ->
            try {
                node.invoke(cause)
            } catch (ex: Throwable) {
                exception?.apply { addSuppressedThrowable(ex) } ?: run {
                    exception =  CompletionHandlerException("Exception in completion handler $node for $this", ex)
                }
            }
        }
        exception?.let { handleOnCompletionException(it) }
    }
    ...
    @Suppress("OverridingDeprecatedMember")
    public final override fun invokeOnCompletion(handler: CompletionHandler): DisposableHandle =
        invokeOnCompletion(onCancelling = false, invokeImmediately = true, handler = handler)

    public final override fun invokeOnCompletion(
        onCancelling: Boolean,
        invokeImmediately: Boolean,
        handler: CompletionHandler
    ): DisposableHandle {
        // Create node upfront -- for common cases it just initializes JobNode.job field,
        // for user-defined handlers it allocates a JobNode object that we might not need, but this is Ok.
        val node: JobNode = makeNode(handler, onCancelling)
        loopOnState { state ->
            when (state) {
                is Empty -> { // EMPTY_X state -- no completion handlers
                    if (state.isActive) {
                        // try move to SINGLE state
                        if (_state.compareAndSet(state, node)) return node
                    } else
                        promoteEmptyToNodeList(state) // that way we can add listener for non-active coroutine
                }
                is Incomplete -> {
                    val list = state.list
                    if (list == null) { // SINGLE/SINGLE+
                        promoteSingleToNodeList(state as JobNode)
                    } else {
                        var rootCause: Throwable? = null
                        var handle: DisposableHandle = NonDisposableHandle
                        if (onCancelling && state is Finishing) {
                            synchronized(state) {
                                // check if we are installing cancellation handler on job that is being cancelled
                                rootCause = state.rootCause // != null if cancelling job
                                // We add node to the list in two cases --- either the job is not being cancelled
                                // or we are adding a child to a coroutine that is not completing yet
                                if (rootCause == null || handler.isHandlerOf<ChildHandleNode>() && !state.isCompleting) {
                                    // Note: add node the list while holding lock on state (make sure it cannot change)
                                    if (!addLastAtomic(state, list, node)) return@loopOnState // retry
                                    // just return node if we don't have to invoke handler (not cancelling yet)
                                    if (rootCause == null) return node
                                    // otherwise handler is invoked immediately out of the synchronized section & handle returned
                                    handle = node
                                }
                            }
                        }
                        if (rootCause != null) {
                            // Note: attachChild uses invokeImmediately, so it gets invoked when adding to cancelled job
                            if (invokeImmediately) handler.invokeIt(rootCause)
                            return handle
                        } else {
                            if (addLastAtomic(state, list, node)) return node
                        }
                    }
                }
                else -> { // is complete
                    // :KLUDGE: We have to invoke a handler in platform-specific way via `invokeIt` extension,
                    // because we play type tricks on Kotlin/JS and handler is not necessarily a function there
                    if (invokeImmediately) handler.invokeIt((state as? CompletedExceptionally)?.cause)
                    return NonDisposableHandle
                }
            }
        }
    }

    private fun makeNode(handler: CompletionHandler, onCancelling: Boolean): JobNode {
        val node = if (onCancelling) {
            (handler as? JobCancellingNode)
                ?: InvokeOnCancelling(handler)
        } else {
            (handler as? JobNode)
                ?.also { assert { it !is JobCancellingNode } }
                ?: InvokeOnCompletion(handler)
        }
        node.job = this
        return node
    }
    ...
    // Parent is cancelling child
    public final override fun parentCancelled(parentJob: ParentJob) {
        cancelImpl(parentJob)
    }

    /**
     * Child was cancelled with a cause.
     * In this method parent decides whether it cancels itself (e.g. on a critical failure) and whether it handles the exception of the child.
     * It is overridden in supervisor implementations to completely ignore any child cancellation.
     * Returns `true` if exception is handled, `false` otherwise (then caller is responsible for handling an exception)
     *
     * Invariant: never returns `false` for instances of [CancellationException], otherwise such exception
     * may leak to the [CoroutineExceptionHandler].
     */
    // 异常完成的场景，父协程会收到异常协程的取消请求，并调用该函数，且`cause`不属于`CancellationException`，执行取消的流程
    public open fun childCancelled(cause: Throwable): Boolean {
        if (cause is CancellationException) return true
        return cancelImpl(cause) && handlesException
    }

    ...
    // Performs promotion of incomplete coroutine state to NodeList for the purpose of
    // converting coroutine state to Cancelling, returns null when need to retry
    private fun getOrPromoteCancellingList(state: Incomplete): NodeList? = state.list ?:
        when (state) {
            is Empty -> NodeList() // we can allocate new empty list that'll get integrated into Cancelling state
            is JobNode -> {
                // SINGLE/SINGLE+ must be promoted to NodeList first, because otherwise we cannot
                // correctly capture a reference to it
                promoteSingleToNodeList(state)
                null // retry
            }
            else -> error("State should have list: $state")
        }
    ...
    /**
     * Completes this job. Used by [AbstractCoroutine.resume].
     * It throws [IllegalStateException] on repeated invocation (when this job is already completing).
     * Returns:
     * * [COMPLETING_WAITING_CHILDREN] if started waiting for children.
     * * Final state otherwise (caller should do [afterCompletion])
     */
    // 异常完成的场景，`proposedUpdate`属于`CompletedExceptionally`
    internal fun makeCompletingOnce(proposedUpdate: Any?): Any? {
        loopOnState { state ->
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
    
    // Returns one of COMPLETING symbols or final state:
    // COMPLETING_ALREADY -- when already complete or completing
    // COMPLETING_RETRY -- when need to retry due to interference
    // COMPLETING_WAITING_CHILDREN -- when made completing and is waiting for children
    // final state -- when completed, for call to afterCompletion
    private fun tryMakeCompleting(state: Any?, proposedUpdate: Any?): Any? {
        if (state !is Incomplete)
            return COMPLETING_ALREADY
        /*
         * FAST PATH -- no children to wait for && simple state (no list) && not cancelling => can complete immediately
         * Cancellation (failures) always have to go through Finishing state to serialize exception handling.
         * Otherwise, there can be a race between (completed state -> handled exception and newly attached child/join)
         * which may miss unhandled exception.
         */
        if ((state is Empty || state is JobNode) && state !is ChildHandleNode && proposedUpdate !is CompletedExceptionally) {
            if (tryFinalizeSimpleState(state, proposedUpdate)) {
                // Completed successfully on fast path -- return updated state
                return proposedUpdate
            }
            return COMPLETING_RETRY
        }
        // The separate slow-path function to simplify profiling
        return tryMakeCompletingSlowPath(state, proposedUpdate)
    }

    // Returns one of COMPLETING symbols or final state:
    // COMPLETING_ALREADY -- when already complete or completing
    // COMPLETING_RETRY -- when need to retry due to interference
    // COMPLETING_WAITING_CHILDREN -- when made completing and is waiting for children
    // final state -- when completed, for call to afterCompletion
    private fun tryMakeCompletingSlowPath(state: Incomplete, proposedUpdate: Any?): Any? {
        // get state's list or else promote to list to correctly operate on child lists
        val list = getOrPromoteCancellingList(state) ?: return COMPLETING_RETRY
        // promote to Finishing state if we are not in it yet
        // This promotion has to be atomic w.r.t to state change, so that a coroutine that is not active yet
        // atomically transition to finishing & completing state
        // 构造`Finishing`，isCompleting为true。然后设置当前线程的`state`为该`Finishing`。
        val finishing = state as? Finishing ?: Finishing(list, false, null)
        // must synchronize updates to finishing state
        var notifyRootCause: Throwable? = null
        synchronized(finishing) {
            // check if this state is already completing
            if (finishing.isCompleting) return COMPLETING_ALREADY
            // mark as completing
            finishing.isCompleting = true
            // if we need to promote to finishing then atomically do it here.
            // We do it as early is possible while still holding the lock. This ensures that we cancelImpl asap
            // (if somebody else is faster) and we synchronize all the threads on this finishing lock asap.
            if (finishing !== state) {
                if (!_state.compareAndSet(state, finishing)) return COMPLETING_RETRY
            }
            // ## IMPORTANT INVARIANT: Only one thread (that had set isCompleting) can go past this point
            assert { !finishing.isSealed } // cannot be sealed
            // add new proposed exception to the finishing state
            val wasCancelling = finishing.isCancelling
            // 异常完成的场景，协程的状态转换为`Completing`，同时为`Cancelling`。
            (proposedUpdate as? CompletedExceptionally)?.let { finishing.addExceptionLocked(it.cause) }
            // If it just becomes cancelling --> must process cancelling notifications
            notifyRootCause = finishing.rootCause.takeIf { !wasCancelling }
        }
        // 异常完成的场景会先取消自己，通知子协程取消，通知父协程取消。然后选择性等待子协程的完成。
        // process cancelling notification here -- it cancels all the children _before_ we start to to wait them (sic!!!)
        notifyRootCause?.let { notifyCancelling(list, it) }
        // 根据`state`获取一个`ChildHandleNode`句柄，在协程`Job`构造的过程中建立了协程之间parent-child关系，
        // 并构造了持有父协程、子协程引用的`ChildHandleNode`句柄，且将该`ChildHandleNode`添加到了协程`Job`的
        // `state.list`的链表上。
        // 如果有`ChildHandleNode`，则`tryWaitForChild`返回true，函数return，结果为`COMPLETING_WAITING_CHILDREN`
        // 需要等待子协程的完成。否则说明子协程完成了，调用`finalizeFinishingState`函数。
        // now wait for children
        val child = firstChild(state)
        if (child != null && tryWaitForChild(finishing, child, proposedUpdate))
            return COMPLETING_WAITING_CHILDREN
        // otherwise -- we have not children left (all were already cancelled?)
        return finalizeFinishingState(finishing, proposedUpdate)
    }
    ...
    private fun firstChild(state: Incomplete) =
        state as? ChildHandleNode ?: state.list?.nextChild()

    // return false when there is no more incomplete children to wait
    // ## IMPORTANT INVARIANT: Only one thread can be concurrently invoking this method.
    private tailrec fun tryWaitForChild(state: Finishing, child: ChildHandleNode, proposedUpdate: Any?): Boolean {
        val handle = child.childJob.invokeOnCompletion(
            invokeImmediately = false,
            handler = ChildCompletion(this, state, child, proposedUpdate).asHandler
        )
        if (handle !== NonDisposableHandle) return true // child is not complete and we've started waiting for it
        val nextChild = child.nextChild() ?: return false
        return tryWaitForChild(state, nextChild, proposedUpdate)
    }

    // ## IMPORTANT INVARIANT: Only one thread can be concurrently invoking this method.
    private fun continueCompleting(state: Finishing, lastChild: ChildHandleNode, proposedUpdate: Any?) {
        assert { this.state === state } // consistency check -- it cannot change while we are waiting for children
        // figure out if we need to wait for next child
        val waitChild = lastChild.nextChild()
        // try wait for next child
        if (waitChild != null && tryWaitForChild(state, waitChild, proposedUpdate)) return // waiting for next child
        // no more children to wait -- try update state
        val finalState = finalizeFinishingState(state, proposedUpdate)
        afterCompletion(finalState)
    }

    private fun LockFreeLinkedListNode.nextChild(): ChildHandleNode? {
        var cur = this
        while (cur.isRemoved) cur = cur.prevNode // rollback to prev non-removed (or list head)
        while (true) {
            cur = cur.nextNode
            if (cur.isRemoved) continue
            if (cur is ChildHandleNode) return cur
            if (cur is NodeList) return null // checked all -- no more children
        }
    }
    ...
    /**
     * This function is invoked once as soon as this job is being cancelled for any reason or completes,
     * similarly to [invokeOnCompletion] with `onCancelling` set to `true`.
     *
     * The meaning of [cause] parameter:
     * * Cause is `null` when the job has completed normally.
     * * Cause is an instance of [CancellationException] when the job was cancelled _normally_.
     *   **It should not be treated as an error**. In particular, it should not be reported to error logs.
     * * Otherwise, the job had been cancelled or failed with exception.
     *
     * The specified [cause] is not the final cancellation cause of this job.
     * A job may produce other exceptions while it is failing and the final cause might be different.
     *
     * @suppress **This is unstable API and it is subject to change.*
     */
    protected open fun onCancelling(cause: Throwable?) {}
    ...
    /**
     * Override for completion actions that need to update some external object depending on job's state,
     * right before all the waiters for coroutine's completion are notified.
     *
     * @param state the final state.
     *
     * @suppress **This is unstable API and it is subject to change.**
     */
    protected open fun onCompletionInternal(state: Any?) {}

    /**
     * Override for the very last action on job's completion to resume the rest of the code in
     * scoped coroutines. It is called when this job is externally completed in an unknown
     * context and thus should resume with a default mode.
     *
     * @suppress **This is unstable API and it is subject to change.**
     */
    protected open fun afterCompletion(state: Any?) {}
    
    ...
    // Completing & Cancelling states,
    // All updates are guarded by synchronized(this), reads are volatile
    @Suppress("UNCHECKED_CAST")
    private class Finishing(
        override val list: NodeList,
        isCompleting: Boolean,
        rootCause: Throwable?
    ) : SynchronizedObject(), Incomplete {
        private val _isCompleting = atomic(isCompleting)
        var isCompleting: Boolean
            get() = _isCompleting.value
            set(value) { _isCompleting.value = value }

        private val _rootCause = atomic(rootCause)
        var rootCause: Throwable? // NOTE: rootCause is kept even when SEALED
            get() = _rootCause.value
            set(value) { _rootCause.value = value }

        private val _exceptionsHolder = atomic<Any?>(null)
        private var exceptionsHolder: Any? // Contains null | Throwable | ArrayList | SEALED
            get() = _exceptionsHolder.value
            set(value) { _exceptionsHolder.value = value }

        // Note: cannot be modified when sealed
        val isSealed: Boolean get() = exceptionsHolder === SEALED
        val isCancelling: Boolean get() = rootCause != null
        override val isActive: Boolean get() = rootCause == null // !isCancelling       
        ...
    }
    
    private val Incomplete.isCancelling: Boolean
        get() = this is Finishing && isCancelling

    // 该处理器会被注册到指定协程内，添加到协程`Job`的`state.list`的链表上。
    // Used by parent that is waiting for child completion
    private class ChildCompletion(
        private val parent: JobSupport,
        private val state: Finishing,
        private val child: ChildHandleNode,
        private val proposedUpdate: Any?
    ) : JobNode() {
        override fun invoke(cause: Throwable?) {
            parent.continueCompleting(state, child, proposedUpdate)
        }
    }

    
   
    
}

...
// --------------- helper classes & constants for job implementation

private val COMPLETING_ALREADY = Symbol("COMPLETING_ALREADY")
@JvmField
internal val COMPLETING_WAITING_CHILDREN = Symbol("COMPLETING_WAITING_CHILDREN")
private val COMPLETING_RETRY = Symbol("COMPLETING_RETRY")
private val TOO_LATE_TO_CANCEL = Symbol("TOO_LATE_TO_CANCEL")
...
internal interface Incomplete {
    val isActive: Boolean
    val list: NodeList? // is null only for Empty and JobNode incomplete state objects
}

internal abstract class JobNode : CompletionHandlerBase(), DisposableHandle, Incomplete {
    /**
     * Initialized by [JobSupport.makeNode].
     */
    lateinit var job: JobSupport
    override val isActive: Boolean get() = true
    override val list: NodeList? get() = null
    override fun dispose() = job.removeNode(this)
    override fun toString() = "$classSimpleName@$hexAddress[job@${job.hexAddress}]"
}

internal class NodeList : LockFreeLinkedListHead(), Incomplete {
    override val isActive: Boolean get() = true
    override val list: NodeList get() = this

    fun getString(state: String) = buildString {
        append("List{")
        append(state)
        append("}[")
        var first = true
        this@NodeList.forEach<JobNode> { node ->
            if (first) first = false else append(", ")
            append(node)
        }
        append("]")
    }

    override fun toString(): String =
        if (DEBUG) getString("Active") else super.toString()
}
...
/**
 * Marker for node that shall be invoked on in _cancelling_ state.
 * **Note: may be invoked multiple times.**
 */
internal abstract class JobCancellingNode : JobNode()
...
internal class ChildHandleNode(
    @JvmField val childJob: ChildJob
) : JobCancellingNode(), ChildHandle {
    override val parent: Job get() = job
    override fun invoke(cause: Throwable?) = childJob.parentCancelled(job)
    // 异常完成的场景，会调用该函数，通知父协程取消
    override fun childCancelled(cause: Throwable): Boolean = job.childCancelled(cause)
}

```

协程运行过程中出现了未捕获的异常，会在协程第二次包装的`Continuation#resumeWith`函数中捕获拦截到，然后协程的结果是`Resulet.Fail`，`cause`为捕获的异常，然后调用第一层包装的`AbstractCoroutine#resumeWith`函数执行协程协程完成的流程。

协程的状态先转换为`Completing`，然后同时设置为`Canceleing`状态，取消了自己，然后将取消请求传递给所有的子协程，传递给父协程。接着等待子协程的异常取消完成，直到所有的子协程取消完成后，当前协程也完成了。



## 19.11 协程之间的关系

- 父协程手动调用`cancel()`，会取消它的所有子协程。
- 父协程必须等待所有子协程完成（处于完成或者取消状态）才能完成。
- 子协程抛出未捕获的异常时，会触发异常完成的流程，会取消它的子协程，而且默认情况下会取消其父协程。

## 19.7 协程的核心api
### yield
挂起函数，挂起当前协程，让CoroutineDispatcher维护的线程运行其他协程。当其他协程执行完成或也
让出执行权时，协程恢复运行。
### join
挂起函数，Job的实例函数，会挂起所在的协程直到该Job结束为止。

该函数会检测所在协程的作业是否已取消，如果该函数在调用时候或者挂起所在协程的时候，所在协程的Job
已取消，则该函数抛出CancellationException。



## 19.12 协程的异常处理
### 19.12.1 CoroutineExceptionHandler

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\CoroutineExceptionHandler.kt

```kotlin
public interface CoroutineExceptionHandler : CoroutineContext.Element {
    /**
     * Key for [CoroutineExceptionHandler] instance in the coroutine context.
     */
    public companion object Key : CoroutineContext.Key<CoroutineExceptionHandler>

    /**
     * Handles uncaught [exception] in the given [context]. It is invoked
     * if coroutine has an uncaught exception.
     */
    public fun handleException(context: CoroutineContext, exception: Throwable)
}
```

是一个接口，继承`CoroutineContext.Element`，是协程上下文中的一个元素，对应的`Key`为`CoroutineExceptionHandler`，用于处理未捕获的异常。

launch函数创建的协程会产生未捕获的异常，在普通Job中，由launch函数创建的子协程，如果出现了异常，
异常会传播到parent协程。root协程上下文的CoroutineExceptionHandler可以处理该异常。

如果在supervisorJob中，由launch构建的子协程，如果出现了未捕获的异常，子协程不会将异常传递给parent协程。
root协程上下文的CoroutineExceptionHandler可以处理该异常

async函数构建的root协程中出现的异常，该异常会反映的返回的Deferred中，通过Deferred#await函数抛出异常，可以
使用try-catch代码块处理该异常，异常不会导致线程的未捕获的异常。

async函数构建的子协程中出现的异常，该异常会反映的返回的Deferred中，通过Deferred#await函数抛出异常，异常会
传播到root协程，如果跟协程是由launch函数创建的，如果不使用root协程的CoroutineExceptionHandler处理，
会导致线程的未捕获异常。

### 19.12.2 ScopeCoroutine

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\internal\Scopes.kt

```kotlin
/**
 * This is a coroutine instance that is created by [coroutineScope] builder.
 */
internal open class ScopeCoroutine<in T>(
    context: CoroutineContext,
    @JvmField val uCont: Continuation<T> // unintercepted continuation
) : AbstractCoroutine<T>(context, true, true), CoroutineStackFrame {

    final override val callerFrame: CoroutineStackFrame? get() = uCont as? CoroutineStackFrame
    final override fun getStackTraceElement(): StackTraceElement? = null

    final override val isScopedCoroutine: Boolean get() = true

    override fun afterCompletion(state: Any?) {
        // Resume in a cancellable way by default when resuming from another context
        uCont.intercepted().resumeCancellableWith(recoverResult(state, uCont))
    }

    override fun afterResume(state: Any?) {
        // Resume direct because scope is already in the correct context
        uCont.resumeWith(recoverResult(state, uCont))
    }
}

```

继承`AbstractCoroutine`，对应的协程是作用域内的协程。当前协程的取消不会传递给父协程。

### 19.12.3 SupervisorCoroutine

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\Supervisor.kt

```kotlin
private class SupervisorCoroutine<in T>(
    context: CoroutineContext,
    uCont: Continuation<T>
) : ScopeCoroutine<T>(context, uCont) {
    override fun childCancelled(cause: Throwable): Boolean = false
}
```

继承`ScopeCoroutine`，对应的协程也是作用域内的协程。子协程取消请求以及子协程中抛出未捕获的异常都不会传递到该协程。

### 19.12.4 `coroutineScope(block: suspend CoroutineScope.() -> R): R`

#### 19.12.4.1 源码分析

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\CoroutineScope.kt

```kotlin
public suspend fun <R> coroutineScope(block: suspend CoroutineScope.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return suspendCoroutineUninterceptedOrReturn { uCont ->
        // 获取当前协程第二次包装的`Continuation`，构造`ScopeCoroutine`并启动
        val coroutine = ScopeCoroutine(uCont.context, uCont)
        coroutine.startUndispatchedOrReturn(coroutine, block)
    }
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\Instrinsics\Undispatched.kt

```kotlin
internal fun <T, R> ScopeCoroutine<T>.startUndispatchedOrReturn(receiver: R, block: suspend R.() -> T): Any? {
    return undispatchedResult({ true }) {
        // 启动协程，不走协程调度器拦截，直接执行协程运算逻辑
        block.startCoroutineUninterceptedOrReturn(receiver, this)
    }
}
...
private inline fun <T> ScopeCoroutine<T>.undispatchedResult(
    shouldThrow: (Throwable) -> Boolean,
    startBlock: () -> Any?
): Any? {
    val result = try {
        startBlock()
    } catch (e: Throwable) {
        // 如果协程中出现未捕获的异常，即协程异常退出，协程的结果是`CompletedExceptionally`。
        CompletedExceptionally(e)
    }
    /*
     * We're trying to complete our undispatched block here and have three code-paths:
     * (1) Coroutine is suspended.
     * Otherwise, coroutine had returned result, so we are completing our block (and its job).
     * (2) If we can't complete it or started waiting for children, we suspend.
     * (3) If we have successfully completed the coroutine state machine here,
     *     then we take the actual final state of the coroutine from makeCompletingOnce and return it.
     *
     * shouldThrow parameter is a special code path for timeout coroutine:
     * If timeout is exceeded, but withTimeout() block was not suspended, we would like to return block value,
     * not a timeout exception.
     */
    if (result === COROUTINE_SUSPENDED) return COROUTINE_SUSPENDED // (1)
    val state = makeCompletingOnce(result)
    if (state === COMPLETING_WAITING_CHILDREN) return COROUTINE_SUSPENDED // (2)
    return if (state is CompletedExceptionally) { // (3)
        // 如果异常退出，则函数抛出指定的异常。
        when {
            shouldThrow(state.cause) -> throw recoverStackTrace(state.cause, uCont)
            result is CompletedExceptionally -> throw recoverStackTrace(result.cause, uCont)
            else -> result
        }
    } else {
        state.unboxState()
    }
}
```

kotlin-stdlib-1.8.20\kotlin\coroutines\intrinsics\intrinsicsJvm.kt

```kotlin
@SinceKotlin("1.3")
@InlineOnly
public actual inline fun <R, T> (suspend R.() -> T).startCoroutineUninterceptedOrReturn(
    receiver: R,
    completion: Continuation<T>
): Any? = (this as Function2<R, Continuation<T>, Any?>).invoke(receiver, completion)
// 调用invoke函数。
```



#### 19.12.4.2 实际代码反编译成Java代码

调试代码如下：

coroutineScope_function_study.kt

```kotlin
package com.kotlin.coroutine

import kotlinx.coroutines.coroutineScope

/**
 *  调试`coroutineScope`顶层挂起函数的实现原理。需要结合反编译为Java代码理解
 */


/**
 *  协程运行过程中出现未捕获的异常，则`coroutineScope`函数会抛出指定的异常。
 */
suspend fun coroutineScopeTest() {
    coroutineScope {
        println("test.")
    }
}
```

反编译为Java代码：

```java
public final class CoroutineScope_function_studyKt {
   @Nullable
   public static final Object coroutineScopeTest(@NotNull Continuation $completion) {
      Object var10000 = CoroutineScopeKt.coroutineScope((Function2)(new Function2((Continuation)null) {
         int label;

         @Nullable
         public final Object invokeSuspend(@NotNull Object var1) {
            Object var3 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(var1);
                  String var2 = "test.";
                  System.out.println(var2);
                  return Unit.INSTANCE;
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
         }

         @NotNull
         public final Continuation create(@Nullable Object value, @NotNull Continuation completion) {
            Intrinsics.checkNotNullParameter(completion, "completion");
            Function2 var3 = new <anonymous constructor>(completion);
            return var3;
         }

         public final Object invoke(Object var1, Object var2) {
            return ((<undefinedtype>)this.create(var1, (Continuation)var2)).invokeSuspend(Unit.INSTANCE);
         }
      }), $completion);
      return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
   }
}
```

+ `coroutineScope`函数是一个顶层的挂起函数，该函数会创建一个协程，对应的`Job`是`ScopeCoroutine`，协程创建完成后不执行协程调度器的拦截操作而直接启动，执行协程的运算逻辑。

+ 创建的协程是作用域内协程，所以对应的协程出现异常，不会传递给父协程。协程运行过程中出现未捕获的异常，会抛出指定的异常。
+ 该函数需要等所有的子协程完成，且自己完成，才会返回。
+ 如果父协程因该函数挂起，且在挂起期间，取消了父协程，则该函数会抛出`CancellationException`。

### 19.12.5 `supervisorScope(block: suspend CoroutineScope.() -> R): R`

#### 19.12.5.1 源码分析

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\Supervisor.kt

```kotlin
public suspend fun <R> supervisorScope(block: suspend CoroutineScope.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return suspendCoroutineUninterceptedOrReturn { uCont ->
        val coroutine = SupervisorCoroutine(uCont.context, uCont)
        coroutine.startUndispatchedOrReturn(coroutine, block)
    }
}
```



#### 19.12.5.2 实际代码反编译成Java代码

调试代码如下：

supervisorScope_function_study.kt

```kotlin
package com.kotlin.coroutine

import kotlinx.coroutines.supervisorScope

suspend fun supervisorScopeFunctionTest() {
    supervisorScope {
        println("SupervisorCoroutine end.")
    }
}
```

反编译成Java代码：

```kotlin
public final class SupervisorScope_function_studyKt {
   @Nullable
   public static final Object supervisorScopeFunctionTest(@NotNull Continuation $completion) {
      Object var10000 = SupervisorKt.supervisorScope((Function2)(new Function2((Continuation)null) {
         int label;

         @Nullable
         public final Object invokeSuspend(@NotNull Object var1) {
            Object var3 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(var1);
                  String var2 = "SupervisorCoroutine end.";
                  System.out.println(var2);
                  return Unit.INSTANCE;
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
         }

         @NotNull
         public final Continuation create(@Nullable Object value, @NotNull Continuation completion) {
            Intrinsics.checkNotNullParameter(completion, "completion");
            Function2 var3 = new <anonymous constructor>(completion);
            return var3;
         }
		
          //调用`invoke`函数，回先调用`create`函数生成`Continuation`函数，然后调用`invokeSuspend`函数启动协程。
         public final Object invoke(Object var1, Object var2) {
            return ((<undefinedtype>)this.create(var1, (Continuation)var2)).invokeSuspend(Unit.INSTANCE);
         }
      }), $completion);
      return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
   }
}
```

+ 和`coroutineScope`函数一样，`supervisorScope`也是一个顶层挂起函数。该函数会创建一个协程，对应的`Job`是`SupervisorCoroutine`，协程创建完成后不执行协程调度器的拦截操作而直接启动，执行协程的运算逻辑。

+ 创建的协程是作用域内协程，所以对应的协程出现异常，不会传递给父协程。协程运行过程中出现未捕获的异常，会抛出指定的异常。
+ 该函数需要等所有的子协程完成，且自己完成，才会返回。
+ 如果父协程因该函数挂起，且在挂起期间，取消了父协程，则该函数会抛出`CancellationException`。
+ 子协程的取消以及抛出了未捕获的异常都不会传递到该协程。



### 19.12.6 协程的异常处理流程分析

相似的分析见小节[19.10 协程运行过程抛出未捕获的异常]

这里的异常处理流程指的是协程运行过程中出现了未捕获的异常，即异常完成的场景。已知协程的运算逻辑封装在第二层包装`BaseContinuationImpl#invokeSuspend`函数中，在调用`resumeWith`函数过程中会调用该函数。

kotlin-stdlib-1.8.20\kotlin\coroutines\jvm\internal\CoroutineImpl.kt

```kotlin
@SinceKotlin("1.3")
internal abstract class BaseContinuationImpl(
    // This is `public val` so that it is private on JVM and cannot be modified by untrusted code, yet
    // it has a public getter (since even untrusted code is allowed to inspect its call stack).
    public val completion: Continuation<Any?>?
) : Continuation<Any?>, CoroutineStackFrame, Serializable {
    // This implementation is final. This fact is used to unroll resumeWith recursion.
    public final override fun resumeWith(result: Result<Any?>) {
        // This loop unrolls recursion in current.resumeWith(param) to make saner and shorter stack traces on resume
        var current = this
        var param = result
        while (true) {
            // Invoke "resume" debug probe on every resumed continuation, so that a debugging library infrastructure
            // can precisely track what part of suspended callstack was already resumed
            probeCoroutineResumed(current)
            with(current) {
                val completion = completion!! // fail fast when trying to resume continuation without completion
                val outcome: Result<Any?> =
                    try {
                        // 调用`invokeSuspend`函数，执行协程的运算。
                        val outcome = invokeSuspend(param)
                        if (outcome === COROUTINE_SUSPENDED) return
                        Result.success(outcome)
                    } catch (exception: Throwable) {
                        // 协程运行过程中抛出未捕获的异常，会在这里捕获到，异常作为失败结果，
                        Result.failure(exception)
                    }
                releaseIntercepted() // this state machine instance is terminating
                if (completion is BaseContinuationImpl) {
                    // unrolling recursion via loop
                    current = completion
                    param = outcome
                } else {
                    // top-level completion reached -- invoke and return
                    // 调用第二层封装的`Continuation#resumeWith`函数开始协程的完成流程。
                    completion.resumeWith(outcome)
                    return
                }
            }
        }
    }
    ...
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\CompletionState.kt

```kotlin
internal fun <T> Result<T>.toState(
    onCancellation: ((cause: Throwable) -> Unit)? = null
): Any? = fold(
    onSuccess = { if (onCancellation != null) CompletedWithCancellation(it, onCancellation) else it },
    onFailure = { CompletedExceptionally(it) }
)
...
/**
 * Class for an internal state of a job that was cancelled (completed exceptionally).
 *
 * @param cause the exceptional completion cause. It's either original exceptional cause
 *        or artificial [CancellationException] if no cause was provided
 */
internal open class CompletedExceptionally(
    @JvmField val cause: Throwable,
    handled: Boolean = false
) {
    private val _handled = atomic(handled)
    val handled: Boolean get() = _handled.value
    fun makeHandled(): Boolean = _handled.compareAndSet(false, true)
    override fun toString(): String = "$classSimpleName[$cause]"
}
```

kotlin-stdlib-1.8.20\kotlin\utli\Result.kt

```kotlin
@SinceKotlin("1.3")
@JvmInline
public value class Result<out T> @PublishedApi internal constructor(
    @PublishedApi
    internal val value: Any?
) : Serializable {
    // discovery

    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] returns `false`.
     */
    public val isSuccess: Boolean get() = value !is Failure

    /**
     * Returns `true` if this instance represents a failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    public val isFailure: Boolean get() = value is Failure
    ...
    public fun exceptionOrNull(): Throwable? =
        when (value) {
            is Failure -> value.exception
            else -> null
        }
    ...
    /**
     * Companion object for [Result] class that contains its constructor functions
     * [success] and [failure].
     */
    public companion object {
        /**
         * Returns an instance that encapsulates the given [value] as successful value.
         */
        @Suppress("INAPPLICABLE_JVM_NAME")
        @InlineOnly
        @JvmName("success")
        public inline fun <T> success(value: T): Result<T> =
            Result(value)

        /**
         * Returns an instance that encapsulates the given [Throwable] [exception] as failure.
         */
        @Suppress("INAPPLICABLE_JVM_NAME")
        @InlineOnly
        @JvmName("failure")
        public inline fun <T> failure(exception: Throwable): Result<T> =
            Result(createFailure(exception))
    }
    
    internal class Failure(
        @JvmField
        val exception: Throwable
    ) : Serializable {
        override fun equals(other: Any?): Boolean = other is Failure && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Failure($exception)"
    }
}

/**
 * Creates an instance of internal marker [Result.Failure] class to
 * make sure that this class is not exposed in ABI.
 */
@PublishedApi
@SinceKotlin("1.3")
internal fun createFailure(exception: Throwable): Any =
    Result.Failure(exception)
...
@InlineOnly
@SinceKotlin("1.3")
public inline fun <R, T> Result<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (exception: Throwable) -> R
): R {
    contract {
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }
    return when (val exception = exceptionOrNull()) {
        null -> onSuccess(value as T)
        else -> onFailure(exception)
    }
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\kotlinx\coroutines\AbstractCoroutine.kt

```kotlin
@InternalCoroutinesApi
public abstract class AbstractCoroutine<in T>(
    parentContext: CoroutineContext,
    initParentJob: Boolean,
    active: Boolean
) : JobSupport(active), Job, Continuation<T>, CoroutineScope {
	...
    /**
     * Completes execution of this with coroutine with the specified result.
     */
    // 协程第二次封装的`Continuation#resumeWith`函数最后会调用第一层封装的`AbstractCoroutine#resumeWith`函数。
    public final override fun resumeWith(result: Result<T>) {
        // 如果协程运行过程中出现了未捕获的异常，则`result`为`Result.Failure`，触发了协程异常完成的流程，
        // 会由异常构造`ComletedExceptionally`对象
        val state = makeCompletingOnce(result.toState())
        if (state === COMPLETING_WAITING_CHILDREN) return
        afterResume(state)
    }

    protected open fun afterResume(state: Any?): Unit = afterCompletion(state)
    
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\JobSupport.kt

```kotlin
@Deprecated(level = DeprecationLevel.ERROR, message = "This is internal API and may be removed in the future releases")
public open class JobSupport constructor(active: Boolean) : Job, ChildJob, ParentJob {
    ...
    private val _state = atomic<Any?>(if (active) EMPTY_ACTIVE else EMPTY_NEW)

    private val _parentHandle = atomic<ChildHandle?>(null)
    internal var parentHandle: ChildHandle?
        get() = _parentHandle.value
        set(value) { _parentHandle.value = value }

    override val parent: Job?
        get() = parentHandle?.parent
    ...
    // ------------ state query ------------
    /**
     * Returns current state of this job.
     * If final state of the job is [Incomplete], then it is boxed into [IncompleteStateBox]
     * and should be [unboxed][unboxState] before returning to user code.
     */
    internal val state: Any? get() {
        _state.loop { state -> // helper loop on state (complete in-progress atomic operations)
            if (state !is OpDescriptor) return state
            state.perform(this)
        }
    }

    /**
     * @suppress **This is unstable API and it is subject to change.**
     */
    private inline fun loopOnState(block: (Any?) -> Unit): Nothing {
        while (true) {
            block(state)
        }
    }
    ...
        // ------------ state update ------------

    // Finalizes Finishing -> Completed (terminal state) transition.
    // ## IMPORTANT INVARIANT: Only one thread can be concurrently invoking this method.
    // Returns final state that was created and updated to
    private fun finalizeFinishingState(state: Finishing, proposedUpdate: Any?): Any? {
        /*
         * Note: proposed state can be Incomplete, e.g.
         * async {
         *     something.invokeOnCompletion {} // <- returns handle which implements Incomplete under the hood
         * }
         */
        assert { this.state === state } // consistency check -- it cannot change
        assert { !state.isSealed } // consistency check -- cannot be sealed yet
        assert { state.isCompleting } // consistency check -- must be marked as completing
        val proposedException = (proposedUpdate as? CompletedExceptionally)?.cause
        // Create the final exception and seal the state so that no more exceptions can be added
        val wasCancelling: Boolean
        val finalException = synchronized(state) {
            wasCancelling = state.isCancelling
            val exceptions = state.sealLocked(proposedException)
            val finalCause = getFinalRootCause(state, exceptions)
            if (finalCause != null) addSuppressedExceptions(finalCause, exceptions)
            finalCause
        }
        // Create the final state object
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
            // 如果当前协程的父协程属于监督作用域协程则`cancleParent`函数为false，则不会调用`handleJobException`函数。
            // 否则`cancleParent`函数为true，调用`handleJobException`函数。
            // 如果当前协程属于作用域协程，则`cancleParent`函数为true，调用`handleJobException`函数。
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
    ...
    // suppressed == true when any exceptions were suppressed while building the final completion cause
    private fun completeStateFinalization(state: Incomplete, update: Any?) {
        /*
         * Now the job in THE FINAL state. We need to properly handle the resulting state.
         * Order of various invocations here is important.
         *
         * 1) Unregister from parent job.
         */
        parentHandle?.let {
            it.dispose() // volatile read parentHandle _after_ state was updated
            parentHandle = NonDisposableHandle // release it just in case, to aid GC
        }
        val cause = (update as? CompletedExceptionally)?.cause
        /*
         * 2) Invoke completion handlers: .join(), callbacks etc.
         *    It's important to invoke them only AFTER exception handling and everything else, see #208
         */
        if (state is JobNode) { // SINGLE/SINGLE+ state -- one completion handler (common case)
            try {
                state.invoke(cause)
            } catch (ex: Throwable) {
                handleOnCompletionException(CompletionHandlerException("Exception in completion handler $state for $this", ex))
            }
        } else {
            state.list?.notifyCompletion(cause)
        }
    }

    // 通知子协程取消，通知父协程取消
    private fun notifyCancelling(list: NodeList, cause: Throwable) {
        // first cancel our own children
        onCancelling(cause)
        notifyHandlers<JobCancellingNode>(list, cause)
        // then cancel parent
        cancelParent(cause) // tentative cancellation -- does not matter if there is no parent
    }

    /**
     * The method that is invoked when the job is cancelled to possibly propagate cancellation to the parent.
     * Returns `true` if the parent is responsible for handling the exception, `false` otherwise.
     *
     * Invariant: never returns `false` for instances of [CancellationException], otherwise such exception
     * may leak to the [CoroutineExceptionHandler].
     */
    private fun cancelParent(cause: Throwable): Boolean {
        // Is scoped coroutine -- don't propagate, will be rethrown
        if (isScopedCoroutine) return true

        /* CancellationException is considered "normal" and parent usually is not cancelled when child produces it.
         * This allow parent to cancel its children (normally) without being cancelled itself, unless
         * child crashes and produce some other exception during its completion.
         */
        // 异常完成的场景，`cause`不属于`CancellationException`
        val isCancellation = cause is CancellationException
        val parent = parentHandle
        // No parent -- ignore CE, report other exceptions.
        if (parent === null || parent === NonDisposableHandle) {
            return isCancellation
        }

        // Notify parent but don't forget to check cancellation
        // 如果当前协程的父协程是监督作用域协程，则异常不会传递给父协程。
        return parent.childCancelled(cause) || isCancellation
    }
    
    private fun NodeList.notifyCompletion(cause: Throwable?) =
        notifyHandlers<JobNode>(this, cause)

    private inline fun <reified T: JobNode> notifyHandlers(list: NodeList, cause: Throwable?) {
        var exception: Throwable? = null
        list.forEach<T> { node ->
            try {
                node.invoke(cause)
            } catch (ex: Throwable) {
                exception?.apply { addSuppressedThrowable(ex) } ?: run {
                    exception =  CompletionHandlerException("Exception in completion handler $node for $this", ex)
                }
            }
        }
        exception?.let { handleOnCompletionException(it) }
    }
    ...
    @Suppress("OverridingDeprecatedMember")
    public final override fun invokeOnCompletion(handler: CompletionHandler): DisposableHandle =
        invokeOnCompletion(onCancelling = false, invokeImmediately = true, handler = handler)

    public final override fun invokeOnCompletion(
        onCancelling: Boolean,
        invokeImmediately: Boolean,
        handler: CompletionHandler
    ): DisposableHandle {
        // Create node upfront -- for common cases it just initializes JobNode.job field,
        // for user-defined handlers it allocates a JobNode object that we might not need, but this is Ok.
        val node: JobNode = makeNode(handler, onCancelling)
        loopOnState { state ->
            when (state) {
                is Empty -> { // EMPTY_X state -- no completion handlers
                    if (state.isActive) {
                        // try move to SINGLE state
                        if (_state.compareAndSet(state, node)) return node
                    } else
                        promoteEmptyToNodeList(state) // that way we can add listener for non-active coroutine
                }
                is Incomplete -> {
                    val list = state.list
                    if (list == null) { // SINGLE/SINGLE+
                        promoteSingleToNodeList(state as JobNode)
                    } else {
                        var rootCause: Throwable? = null
                        var handle: DisposableHandle = NonDisposableHandle
                        if (onCancelling && state is Finishing) {
                            synchronized(state) {
                                // check if we are installing cancellation handler on job that is being cancelled
                                rootCause = state.rootCause // != null if cancelling job
                                // We add node to the list in two cases --- either the job is not being cancelled
                                // or we are adding a child to a coroutine that is not completing yet
                                if (rootCause == null || handler.isHandlerOf<ChildHandleNode>() && !state.isCompleting) {
                                    // Note: add node the list while holding lock on state (make sure it cannot change)
                                    if (!addLastAtomic(state, list, node)) return@loopOnState // retry
                                    // just return node if we don't have to invoke handler (not cancelling yet)
                                    if (rootCause == null) return node
                                    // otherwise handler is invoked immediately out of the synchronized section & handle returned
                                    handle = node
                                }
                            }
                        }
                        if (rootCause != null) {
                            // Note: attachChild uses invokeImmediately, so it gets invoked when adding to cancelled job
                            if (invokeImmediately) handler.invokeIt(rootCause)
                            return handle
                        } else {
                            if (addLastAtomic(state, list, node)) return node
                        }
                    }
                }
                else -> { // is complete
                    // :KLUDGE: We have to invoke a handler in platform-specific way via `invokeIt` extension,
                    // because we play type tricks on Kotlin/JS and handler is not necessarily a function there
                    if (invokeImmediately) handler.invokeIt((state as? CompletedExceptionally)?.cause)
                    return NonDisposableHandle
                }
            }
        }
    }

    private fun makeNode(handler: CompletionHandler, onCancelling: Boolean): JobNode {
        val node = if (onCancelling) {
            (handler as? JobCancellingNode)
                ?: InvokeOnCancelling(handler)
        } else {
            (handler as? JobNode)
                ?.also { assert { it !is JobCancellingNode } }
                ?: InvokeOnCompletion(handler)
        }
        node.job = this
        return node
    }
    ...
    // Parent is cancelling child
    public final override fun parentCancelled(parentJob: ParentJob) {
        cancelImpl(parentJob)
    }

    /**
     * Child was cancelled with a cause.
     * In this method parent decides whether it cancels itself (e.g. on a critical failure) and whether it handles the exception of the child.
     * It is overridden in supervisor implementations to completely ignore any child cancellation.
     * Returns `true` if exception is handled, `false` otherwise (then caller is responsible for handling an exception)
     *
     * Invariant: never returns `false` for instances of [CancellationException], otherwise such exception
     * may leak to the [CoroutineExceptionHandler].
     */
    // 异常完成的场景，父协程会收到异常协程的取消请求，并调用该函数，且`cause`不属于`CancellationException`，执行取消的流程
    public open fun childCancelled(cause: Throwable): Boolean {
        if (cause is CancellationException) return true
        return cancelImpl(cause) && handlesException
    }

    ...
    // Performs promotion of incomplete coroutine state to NodeList for the purpose of
    // converting coroutine state to Cancelling, returns null when need to retry
    private fun getOrPromoteCancellingList(state: Incomplete): NodeList? = state.list ?:
        when (state) {
            is Empty -> NodeList() // we can allocate new empty list that'll get integrated into Cancelling state
            is JobNode -> {
                // SINGLE/SINGLE+ must be promoted to NodeList first, because otherwise we cannot
                // correctly capture a reference to it
                promoteSingleToNodeList(state)
                null // retry
            }
            else -> error("State should have list: $state")
        }
    ...
    /**
     * Completes this job. Used by [AbstractCoroutine.resume].
     * It throws [IllegalStateException] on repeated invocation (when this job is already completing).
     * Returns:
     * * [COMPLETING_WAITING_CHILDREN] if started waiting for children.
     * * Final state otherwise (caller should do [afterCompletion])
     */
    // 异常完成的场景，`proposedUpdate`属于`CompletedExceptionally`
    internal fun makeCompletingOnce(proposedUpdate: Any?): Any? {
        loopOnState { state ->
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
    
    // Returns one of COMPLETING symbols or final state:
    // COMPLETING_ALREADY -- when already complete or completing
    // COMPLETING_RETRY -- when need to retry due to interference
    // COMPLETING_WAITING_CHILDREN -- when made completing and is waiting for children
    // final state -- when completed, for call to afterCompletion
    private fun tryMakeCompleting(state: Any?, proposedUpdate: Any?): Any? {
        if (state !is Incomplete)
            return COMPLETING_ALREADY
        /*
         * FAST PATH -- no children to wait for && simple state (no list) && not cancelling => can complete immediately
         * Cancellation (failures) always have to go through Finishing state to serialize exception handling.
         * Otherwise, there can be a race between (completed state -> handled exception and newly attached child/join)
         * which may miss unhandled exception.
         */
        if ((state is Empty || state is JobNode) && state !is ChildHandleNode && proposedUpdate !is CompletedExceptionally) {
            if (tryFinalizeSimpleState(state, proposedUpdate)) {
                // Completed successfully on fast path -- return updated state
                return proposedUpdate
            }
            return COMPLETING_RETRY
        }
        // The separate slow-path function to simplify profiling
        return tryMakeCompletingSlowPath(state, proposedUpdate)
    }

    // Returns one of COMPLETING symbols or final state:
    // COMPLETING_ALREADY -- when already complete or completing
    // COMPLETING_RETRY -- when need to retry due to interference
    // COMPLETING_WAITING_CHILDREN -- when made completing and is waiting for children
    // final state -- when completed, for call to afterCompletion
    private fun tryMakeCompletingSlowPath(state: Incomplete, proposedUpdate: Any?): Any? {
        // get state's list or else promote to list to correctly operate on child lists
        val list = getOrPromoteCancellingList(state) ?: return COMPLETING_RETRY
        // promote to Finishing state if we are not in it yet
        // This promotion has to be atomic w.r.t to state change, so that a coroutine that is not active yet
        // atomically transition to finishing & completing state
        // 构造`Finishing`，isCompleting为true。然后设置当前线程的`state`为该`Finishing`。
        val finishing = state as? Finishing ?: Finishing(list, false, null)
        // must synchronize updates to finishing state
        var notifyRootCause: Throwable? = null
        synchronized(finishing) {
            // check if this state is already completing
            if (finishing.isCompleting) return COMPLETING_ALREADY
            // 设置为`Completing`
            // mark as completing
            finishing.isCompleting = true
            // if we need to promote to finishing then atomically do it here.
            // We do it as early is possible while still holding the lock. This ensures that we cancelImpl asap
            // (if somebody else is faster) and we synchronize all the threads on this finishing lock asap.
            // 设置当前协程的状态为`Completing`
            if (finishing !== state) {
                if (!_state.compareAndSet(state, finishing)) return COMPLETING_RETRY
            }
            // ## IMPORTANT INVARIANT: Only one thread (that had set isCompleting) can go past this point
            assert { !finishing.isSealed } // cannot be sealed
            // add new proposed exception to the finishing state
            val wasCancelling = finishing.isCancelling
            // 异常完成的场景，协程的状态转换为`Completing`，同时为`Cancelling`。
            (proposedUpdate as? CompletedExceptionally)?.let { finishing.addExceptionLocked(it.cause) }
            // If it just becomes cancelling --> must process cancelling notifications
            notifyRootCause = finishing.rootCause.takeIf { !wasCancelling }
        }
        // 异常完成的场景会先取消自己，通知子协程取消，通知父协程取消。然后选择性等待子协程的完成。
        // process cancelling notification here -- it cancels all the children _before_ we start to to wait them (sic!!!)
        notifyRootCause?.let { notifyCancelling(list, it) }
        // 根据`state`获取一个`ChildHandleNode`句柄，在协程`Job`构造的过程中建立了协程之间parent-child关系，
        // 并构造了持有父协程、子协程引用的`ChildHandleNode`句柄，且将该`ChildHandleNode`添加到了协程`Job`的
        // `state.list`的链表上。
        // 如果有`ChildHandleNode`，则`tryWaitForChild`返回true，函数return，结果为`COMPLETING_WAITING_CHILDREN`
        // 需要等待子协程的完成。否则说明子协程完成了，调用`finalizeFinishingState`函数。
        // now wait for children
        val child = firstChild(state)
        if (child != null && tryWaitForChild(finishing, child, proposedUpdate))
            return COMPLETING_WAITING_CHILDREN
        // otherwise -- we have not children left (all were already cancelled?)
        return finalizeFinishingState(finishing, proposedUpdate)
    }
    ...
    private fun firstChild(state: Incomplete) =
        state as? ChildHandleNode ?: state.list?.nextChild()

    // return false when there is no more incomplete children to wait
    // ## IMPORTANT INVARIANT: Only one thread can be concurrently invoking this method.
    private tailrec fun tryWaitForChild(state: Finishing, child: ChildHandleNode, proposedUpdate: Any?): Boolean {
        val handle = child.childJob.invokeOnCompletion(
            invokeImmediately = false,
            handler = ChildCompletion(this, state, child, proposedUpdate).asHandler
        )
        if (handle !== NonDisposableHandle) return true // child is not complete and we've started waiting for it
        val nextChild = child.nextChild() ?: return false
        return tryWaitForChild(state, nextChild, proposedUpdate)
    }

    // ## IMPORTANT INVARIANT: Only one thread can be concurrently invoking this method.
    private fun continueCompleting(state: Finishing, lastChild: ChildHandleNode, proposedUpdate: Any?) {
        assert { this.state === state } // consistency check -- it cannot change while we are waiting for children
        // figure out if we need to wait for next child
        val waitChild = lastChild.nextChild()
        // try wait for next child
        if (waitChild != null && tryWaitForChild(state, waitChild, proposedUpdate)) return // waiting for next child
        // no more children to wait -- try update state
        val finalState = finalizeFinishingState(state, proposedUpdate)
        afterCompletion(finalState)
    }

    private fun LockFreeLinkedListNode.nextChild(): ChildHandleNode? {
        var cur = this
        while (cur.isRemoved) cur = cur.prevNode // rollback to prev non-removed (or list head)
        while (true) {
            cur = cur.nextNode
            if (cur.isRemoved) continue
            if (cur is ChildHandleNode) return cur
            if (cur is NodeList) return null // checked all -- no more children
        }
    }
    ...
    /**
     * This function is invoked once as soon as this job is being cancelled for any reason or completes,
     * similarly to [invokeOnCompletion] with `onCancelling` set to `true`.
     *
     * The meaning of [cause] parameter:
     * * Cause is `null` when the job has completed normally.
     * * Cause is an instance of [CancellationException] when the job was cancelled _normally_.
     *   **It should not be treated as an error**. In particular, it should not be reported to error logs.
     * * Otherwise, the job had been cancelled or failed with exception.
     *
     * The specified [cause] is not the final cancellation cause of this job.
     * A job may produce other exceptions while it is failing and the final cause might be different.
     *
     * @suppress **This is unstable API and it is subject to change.*
     */
    protected open fun onCancelling(cause: Throwable?) {}
    ...
    /**
     * Override for completion actions that need to update some external object depending on job's state,
     * right before all the waiters for coroutine's completion are notified.
     *
     * @param state the final state.
     *
     * @suppress **This is unstable API and it is subject to change.**
     */
    protected open fun onCompletionInternal(state: Any?) {}

    /**
     * Override for the very last action on job's completion to resume the rest of the code in
     * scoped coroutines. It is called when this job is externally completed in an unknown
     * context and thus should resume with a default mode.
     *
     * @suppress **This is unstable API and it is subject to change.**
     */
    protected open fun afterCompletion(state: Any?) {}
    
    ...
    // Completing & Cancelling states,
    // All updates are guarded by synchronized(this), reads are volatile
    @Suppress("UNCHECKED_CAST")
    private class Finishing(
        override val list: NodeList,
        isCompleting: Boolean,
        rootCause: Throwable?
    ) : SynchronizedObject(), Incomplete {
        private val _isCompleting = atomic(isCompleting)
        var isCompleting: Boolean
            get() = _isCompleting.value
            set(value) { _isCompleting.value = value }

        private val _rootCause = atomic(rootCause)
        var rootCause: Throwable? // NOTE: rootCause is kept even when SEALED
            get() = _rootCause.value
            set(value) { _rootCause.value = value }

        private val _exceptionsHolder = atomic<Any?>(null)
        private var exceptionsHolder: Any? // Contains null | Throwable | ArrayList | SEALED
            get() = _exceptionsHolder.value
            set(value) { _exceptionsHolder.value = value }

        // Note: cannot be modified when sealed
        val isSealed: Boolean get() = exceptionsHolder === SEALED
        val isCancelling: Boolean get() = rootCause != null
        override val isActive: Boolean get() = rootCause == null // !isCancelling       
        ...
    }
    
    private val Incomplete.isCancelling: Boolean
        get() = this is Finishing && isCancelling

    // 该处理器会被注册到指定协程内，添加到协程`Job`的`state.list`的链表上。
    // Used by parent that is waiting for child completion
    private class ChildCompletion(
        private val parent: JobSupport,
        private val state: Finishing,
        private val child: ChildHandleNode,
        private val proposedUpdate: Any?
    ) : JobNode() {
        override fun invoke(cause: Throwable?) {
            parent.continueCompleting(state, child, proposedUpdate)
        }
    }
    
    ...
    /**
     * @suppress **This is unstable API and it is subject to change.**
     */
    protected suspend fun awaitInternal(): Any? {
        // fast-path -- check state (avoid extra object creation)
        while (true) { // lock-free loop on state
            val state = this.state
            // `async`启动的协程运行过程中出现了未捕获的异常，可以通过`await`函数抛出。
            if (state !is Incomplete) {
                // already complete -- just return result
                if (state is CompletedExceptionally) { // Slow path to recover stacktrace
                    recoverAndThrow(state.cause)
                }
                return state.unboxState()

            }
            if (startInternal(state) >= 0) break // break unless needs to retry
        }
        return awaitSuspend() // slow-path
    }


    
   
    
}

...
// --------------- helper classes & constants for job implementation

private val COMPLETING_ALREADY = Symbol("COMPLETING_ALREADY")
@JvmField
internal val COMPLETING_WAITING_CHILDREN = Symbol("COMPLETING_WAITING_CHILDREN")
private val COMPLETING_RETRY = Symbol("COMPLETING_RETRY")
private val TOO_LATE_TO_CANCEL = Symbol("TOO_LATE_TO_CANCEL")
...
internal interface Incomplete {
    val isActive: Boolean
    val list: NodeList? // is null only for Empty and JobNode incomplete state objects
}

internal abstract class JobNode : CompletionHandlerBase(), DisposableHandle, Incomplete {
    /**
     * Initialized by [JobSupport.makeNode].
     */
    lateinit var job: JobSupport
    override val isActive: Boolean get() = true
    override val list: NodeList? get() = null
    override fun dispose() = job.removeNode(this)
    override fun toString() = "$classSimpleName@$hexAddress[job@${job.hexAddress}]"
}

internal class NodeList : LockFreeLinkedListHead(), Incomplete {
    override val isActive: Boolean get() = true
    override val list: NodeList get() = this

    fun getString(state: String) = buildString {
        append("List{")
        append(state)
        append("}[")
        var first = true
        this@NodeList.forEach<JobNode> { node ->
            if (first) first = false else append(", ")
            append(node)
        }
        append("]")
    }

    override fun toString(): String =
        if (DEBUG) getString("Active") else super.toString()
}
...
/**
 * Marker for node that shall be invoked on in _cancelling_ state.
 * **Note: may be invoked multiple times.**
 */
internal abstract class JobCancellingNode : JobNode()
...
internal class ChildHandleNode(
    @JvmField val childJob: ChildJob
) : JobCancellingNode(), ChildHandle {
    override val parent: Job get() = job
    override fun invoke(cause: Throwable?) = childJob.parentCancelled(job)
    // 异常完成的场景，会调用该函数，通知父协程取消
    override fun childCancelled(cause: Throwable): Boolean = job.childCancelled(cause)
}

```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\Builds.common.kt

```kotlin
@Suppress("UNCHECKED_CAST")
private open class DeferredCoroutine<T>(
    parentContext: CoroutineContext,
    active: Boolean
) : AbstractCoroutine<T>(parentContext, true, active = active), Deferred<T> {
    override fun getCompleted(): T = getCompletedInternal() as T
    // await挂起函数会抛出协程运行时未不获取的异常。
    override suspend fun await(): T = awaitInternal() as T
    override val onAwait: SelectClause1<T> get() = onAwaitInternal as SelectClause1<T>
}

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

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\channel\Actor.kt

```kotlin
@ObsoleteCoroutinesApi
public fun <E> CoroutineScope.actor(
    context: CoroutineContext = EmptyCoroutineContext,
    capacity: Int = 0, // todo: Maybe Channel.DEFAULT here?
    start: CoroutineStart = CoroutineStart.DEFAULT,
    onCompletion: CompletionHandler? = null,
    block: suspend ActorScope<E>.() -> Unit
): SendChannel<E> {
    val newContext = newCoroutineContext(context)
    val channel = Channel<E>(capacity)
    val coroutine = if (start.isLazy)
        LazyActorCoroutine(newContext, channel, block) else
        ActorCoroutine(newContext, channel, active = true)
    if (onCompletion != null) coroutine.invokeOnCompletion(handler = onCompletion)
    coroutine.start(start, coroutine, block)
    return coroutine
}

private open class ActorCoroutine<E>(
    parentContext: CoroutineContext,
    channel: Channel<E>,
    active: Boolean
) : ChannelCoroutine<E>(parentContext, channel, initParentJob = false, active = active), ActorScope<E> {

    init {
        initParentJob(parentContext[Job])
    }

    override fun onCancelling(cause: Throwable?) {
        _channel.cancel(cause?.let {
            it as? CancellationException ?: CancellationException("$classSimpleName was cancelled", it)
        })
    }

    override fun handleJobException(exception: Throwable): Boolean {
        handleCoroutineException(context, exception)
        return true
    }
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\CoroutineExceptionHandler.kt

```kotlin
@InternalCoroutinesApi
public fun handleCoroutineException(context: CoroutineContext, exception: Throwable) {
    // Invoke an exception handler from the context if present
    try {
        // 获取协程上下文中的`CoroutineExceptionHandler`元素调用`handleException`处理异常。
        context[CoroutineExceptionHandler]?.let {
            it.handleException(context, exception)
            return
        }
    } catch (t: Throwable) {
        handleUncaughtCoroutineException(context, handlerException(exception, t))
        return
    }
    // If a handler is not present in the context or an exception was thrown, fallback to the global handler
    handleUncaughtCoroutineException(context, exception)
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\internal\CoroutineExceptionHandlerImpl.common.kt

```kotlin
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
    propagateExceptionFinalResort(exception)
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\jvmMain\internal\CoroutineExceptionHandlerImpl.kt

```kotlin
internal actual fun propagateExceptionFinalResort(exception: Throwable) {
    // use the thread's handler
    val currentThread = Thread.currentThread()
    currentThread.uncaughtExceptionHandler.uncaughtException(currentThread, exception)
}

internal actual class DiagnosticCoroutineContextException actual constructor(@Transient private val context: CoroutineContext) : RuntimeException() {
    override fun getLocalizedMessage(): String {
        return context.toString()
    }

    override fun fillInStackTrace(): Throwable {
        // Prevent Android <= 6.0 bug, #1866
        stackTrace = emptyArray()
        return this
    }
}
```

jdk11\java\base\java\lang\Thread.java

```java
public
class Thread implements Runnable {
    ...
    private ThreadGroup group;
    ...
    private volatile UncaughtExceptionHandler uncaughtExceptionHandler;
    ...
	public UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return uncaughtExceptionHandler != null ?
            uncaughtExceptionHandler : group;
    }
    
}
```

jdk11\java\base\java\lang\ThreadGroup.java

```java
public
class ThreadGroup implements Thread.UncaughtExceptionHandler {
    private final ThreadGroup parent;
    ...
    public void uncaughtException(Thread t, Throwable e) {
        if (parent != null) {
            parent.uncaughtException(t, e);
        } else {
            Thread.UncaughtExceptionHandler ueh =
                Thread.getDefaultUncaughtExceptionHandler();
            if (ueh != null) {
                ueh.uncaughtException(t, e);
            } else if (!(e instanceof ThreadDeath)) {
                // 在控制台打印异常堆栈日志。
                System.err.print("Exception in thread \""
                                 + t.getName() + "\" ");
                e.printStackTrace(System.err);
            }
        }
    }
    ...
}
```

kotlinx-coroutines-core-jvm-1.7.3-sources\jvmMain\internal\StackTraceRecovery.kt

```kotlin
internal actual suspend inline fun recoverAndThrow(exception: Throwable): Nothing {
    if (!RECOVER_STACK_TRACES) throw exception
    suspendCoroutineUninterceptedOrReturn<Nothing> {
        if (it !is CoroutineStackFrame) throw exception
        throw recoverFromStackFrame(exception, it)
    }
}
```



+ 协程运行过程中出现了未捕获的异常，会在协程第二次包装的`Continuation#resumeWith`函数中捕获拦截到，然后协程的结果是`Resulet.Fail`，`cause`为捕获的异常，然后调用第一层包装的`AbstractCoroutine#resumeWith`函数执行协程异常完成的流程。

+ 协程的状态先转换为`Completing`，然后同时设置为`Canceleing`状态，取消了自己，然后将取消请求传递给所有的子协程，选择性传递给父协程。接着等待子协程的异常取消完成，直到所有的子协程取消完成后，当前协程也完成了。

+ 选择性传递给父协程的逻辑：

  + 当前协程不属于作用域内协程。如果父协程不属于监督作用域协程，则异常传递给父协程，否则不传递给父协程。
  + 当前协程属于作用域内协程。异常不传递给父协程。

+ 选择性执行`handleJobException`逻辑：

  + 当前协程不属于作用域内协程。如果父协程不属于监督作用域协程，则异常传递给父协程，执行`handleJobException`函数，否则不传递给父协程，不执行`handleJobException`函数。
  + 当前协程属于作用域内协程。异常不传递给父协程，执行`handleJobExcepiton`函数。

+ `handleJobException`逻辑。默认该函数不处理异常。如果协程的`Job`是`StandaloneCoroutine`或者`ActorCoroutine`则会处理异常。主要实现步骤如下：

  获取协程上下中的`CoroutineExceptionHandler`元素。

  + 存在，在调用`CoroutineExceptionHandler#handleException`函数处理异常。
  + 不存在；在控制台打印异常堆栈日志。

+ 通过`coroutineScope`顶层挂起函数可以构造作用域协程；通过`supervisorScope`顶层挂起函数可以构造监督作用域内协程。

+ `async`启动的协程运行过程中出现了未捕获的异常，可以通过`await`函数抛出。

  



## 19.13 协程的并发

协程就是可以挂起和恢复执行的运算逻辑，挂起函数用状态机的方式用挂起点将协程的运算逻辑拆分为不同的片段，每次运行协程执行的不同的逻辑片段。所以协程在运行时只是线程中的一块代码，线程的并发处理方式都可以用在协程上。不过协程还提供两种特有的方式，一是不阻塞线程的互斥锁`Mutex`，一是通过` ThreadLocal `实现的协程局部数据。

### 19.13.1 Mutex

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\sync\Mutex.kt

```kotlin
/**
 * Mutual exclusion for coroutines.
 *
 * Mutex has two states: _locked_ and _unlocked_.
 * It is **non-reentrant**, that is invoking [lock] even from the same thread/coroutine that currently holds
 * the lock still suspends the invoker.
 *
 * JVM API note:
 * Memory semantic of the [Mutex] is similar to `synchronized` block on JVM:
 * An unlock operation on a [Mutex] happens-before every subsequent successful lock on that [Mutex].
 * Unsuccessful call to [tryLock] do not have any memory effects.
 */
public interface Mutex {
    /**
     * Returns `true` if this mutex is locked.
     */
    public val isLocked: Boolean

    /**
     * Tries to lock this mutex, returning `false` if this mutex is already locked.
     *
     * It is recommended to use [withLock] for safety reasons, so that the acquired lock is always
     * released at the end of your critical section, and [unlock] is never invoked before a successful
     * lock acquisition.
     *
     * @param owner Optional owner token for debugging. When `owner` is specified (non-null value) and this mutex
     *        is already locked with the same token (same identity), this function throws [IllegalStateException].
     */
    public fun tryLock(owner: Any? = null): Boolean

    /**
     * Locks this mutex, suspending caller until the lock is acquired (in other words, while the lock is held elsewhere).
     *
     * This suspending function is cancellable. If the [Job] of the current coroutine is cancelled or completed while this
     * function is suspended, this function immediately resumes with [CancellationException].
     * There is a **prompt cancellation guarantee**. If the job was cancelled while this function was
     * suspended, it will not resume successfully. See [suspendCancellableCoroutine] documentation for low-level details.
     * This function releases the lock if it was already acquired by this function before the [CancellationException]
     * was thrown.
     *
     * Note that this function does not check for cancellation when it is not suspended.
     * Use [yield] or [CoroutineScope.isActive] to periodically check for cancellation in tight loops if needed.
     *
     * Use [tryLock] to try acquiring the lock without waiting.
     *
     * This function is fair; suspended callers are resumed in first-in-first-out order.
     *
     * It is recommended to use [withLock] for safety reasons, so that the acquired lock is always
     * released at the end of the critical section, and [unlock] is never invoked before a successful
     * lock acquisition.
     *
     * @param owner Optional owner token for debugging. When `owner` is specified (non-null value) and this mutex
     *        is already locked with the same token (same identity), this function throws [IllegalStateException].
     */
    public suspend fun lock(owner: Any? = null)

    /**
     * Clause for [select] expression of [lock] suspending function that selects when the mutex is locked.
     * Additional parameter for the clause in the `owner` (see [lock]) and when the clause is selected
     * the reference to this mutex is passed into the corresponding block.
     */
    @Deprecated(level = DeprecationLevel.WARNING, message = "Mutex.onLock deprecated without replacement. " +
        "For additional details please refer to #2794") // WARNING since 1.6.0
    public val onLock: SelectClause2<Any?, Mutex>

    /**
     * Checks whether this mutex is locked by the specified owner.
     *
     * @return `true` when this mutex is locked by the specified owner;
     * `false` if the mutex is not locked or locked by another owner.
     */
    public fun holdsLock(owner: Any): Boolean

    /**
     * Unlocks this mutex. Throws [IllegalStateException] if invoked on a mutex that is not locked or
     * was locked with a different owner token (by identity).
     *
     * It is recommended to use [withLock] for safety reasons, so that the acquired lock is always
     * released at the end of the critical section, and [unlock] is never invoked before a successful
     * lock acquisition.
     *
     * @param owner Optional owner token for debugging. When `owner` is specified (non-null value) and this mutex
     *        was locked with the different token (by identity), this function throws [IllegalStateException].
     */
    public fun unlock(owner: Any? = null)
}
```

`Mutex`是协程互斥锁，是不可重入的锁，持有`Mutex`锁的协程，再次调用`lock`函数会挂起协程。定义了索取锁和释放锁的函数：

`lock(owner: Any? = null)`和`unlock(owner: Any? = null)`。

## 19.13.2 MutexImpl

kotlinx-coroutines-core-jvm-1.7.3-sources\commonMain\sync\Mutex.kt

```kotlin
/**
 * Creates a [Mutex] instance.
 * The mutex created is fair: lock is granted in first come, first served order.
 *
 * @param locked initial state of the mutex.
 */
@Suppress("FunctionName")
public fun Mutex(locked: Boolean = false): Mutex =
    MutexImpl(locked)

/**
 * Executes the given [action] under this mutex's lock.
 *
 * @param owner Optional owner token for debugging. When `owner` is specified (non-null value) and this mutex
 *        is already locked with the same token (same identity), this function throws [IllegalStateException].
 *
 * @return the return value of the action.
 */
@OptIn(ExperimentalContracts::class)
public suspend inline fun <T> Mutex.withLock(owner: Any? = null, action: () -> T): T {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }

    lock(owner)
    try {
        return action()
    } finally {
        unlock(owner)
    }
}

internal open class MutexImpl(locked: Boolean) : SemaphoreImpl(1, if (locked) 1 else 0), Mutex {
    /**
     * After the lock is acquired, the corresponding owner is stored in this field.
     * The [unlock] operation checks the owner and either re-sets it to [NO_OWNER],
     * if there is no waiting request, or to the owner of the suspended [lock] operation
     * to be resumed, otherwise.
     */
    private val owner = atomic<Any?>(if (locked) null else NO_OWNER)
    ...
    override suspend fun lock(owner: Any?) {
        // 如果没有获取到锁，则挂起当前协程。
        if (tryLock(owner)) return
        lockSuspend(owner)
    }

    private suspend fun lockSuspend(owner: Any?) = suspendCancellableCoroutineReusable<Unit> { cont ->
        val contWithOwner = CancellableContinuationWithOwner(cont, owner)
        acquire(contWithOwner)
    }
    
    override fun tryLock(owner: Any?): Boolean = when (tryLockImpl(owner)) {
        TRY_LOCK_SUCCESS -> true
        TRY_LOCK_FAILED -> false
        TRY_LOCK_ALREADY_LOCKED_BY_OWNER -> error("This mutex is already locked by the specified owner: $owner")
        else -> error("unexpected")
    }

    private fun tryLockImpl(owner: Any?): Int {
        while (true) {
            if (tryAcquire()) {
                assert { this.owner.value === NO_OWNER }
                this.owner.value = owner
                return TRY_LOCK_SUCCESS
            } else {
                // The semaphore permit acquisition has failed.
                // However, we need to check that this mutex is not
                // locked by our owner.
                if (owner == null) return TRY_LOCK_FAILED
                when (holdsLockImpl(owner)) {
                    // This mutex is already locked by our owner.
                    HOLDS_LOCK_YES -> return TRY_LOCK_ALREADY_LOCKED_BY_OWNER
                    // This mutex is locked by another owner, `trylock(..)` must return `false`.
                    HOLDS_LOCK_ANOTHER_OWNER -> return TRY_LOCK_FAILED
                    // This mutex is no longer locked, restart the operation.
                    HOLDS_LOCK_UNLOCKED -> continue
                }
            }
        }
    }

    override fun unlock(owner: Any?) {
        while (true) {
            // Is this mutex locked?
            check(isLocked) { "This mutex is not locked" }
            // Read the owner, waiting until it is set in a spin-loop if required.
            val curOwner = this.owner.value
            if (curOwner === NO_OWNER) continue // <-- ATTENTION, BLOCKING PART HERE
            // Check the owner.
            check(curOwner === owner || owner == null) { "This mutex is locked by $curOwner, but $owner is expected" }
            // Try to clean the owner first. We need to use CAS here to synchronize with concurrent `unlock(..)`-s.
            if (!this.owner.compareAndSet(curOwner, NO_OWNER)) continue
            // Release the semaphore permit at the end.
            release()
            return
        }
    }
    
}

private val NO_OWNER = Symbol("NO_OWNER")
private val ON_LOCK_ALREADY_LOCKED_BY_OWNER = Symbol("ALREADY_LOCKED_BY_OWNER")

private const val TRY_LOCK_SUCCESS = 0
private const val TRY_LOCK_FAILED = 1
private const val TRY_LOCK_ALREADY_LOCKED_BY_OWNER = 2

private const val HOLDS_LOCK_UNLOCKED = 0
private const val HOLDS_LOCK_YES = 1
private const val HOLDS_LOCK_ANOTHER_OWNER = 2
```

`MutexImple`实现接口`Mutex`，继承`SemaphoreImpl`，该锁，在获取锁的时候如果获取失败，会挂起当前协程。使用该锁实现协程并发，获取锁和释放锁的参数`owner`需要是同一个对象。

+ 通过`Mutex(locked: Boolean = false)`顶层函数可以得到`MutexImpl`对象。
+ 通过`Mutex.withLock(owner: Any? = null, action: () -> T): T`扩展函数可以方便对`action`代码块加锁，并在代码块执行完后释放锁。



## 完











