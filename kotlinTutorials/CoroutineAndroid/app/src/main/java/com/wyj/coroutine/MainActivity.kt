package com.wyj.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.coroutines.*
import java.lang.NullPointerException
import kotlin.coroutines.coroutineContext

/**
 *  https://juejin.cn/post/6953441828100112392
 *  https://juejin.cn/post/6953287252373930021
 *
 *  1 CoroutineDispatcher
 *      协程调度器：调度器确定了相关的协程在哪个线程或哪些线程上运行。协程调度器可以限制协程在某个线程上运行、
 *  可以将协程分派到一个线程池中、或者让协程不受限制地运行。
 *  2 CoroutineContext
 *      它是一个包含了用户定义的各种不同元素的Element的集合。其中主要元素有Job、CoroutineDispatcher、
 *  CoroutineExceptionHandler、ContinuationInterceptor、CoroutineName。这些都是Element的衍生类。
 *  这些类或接口都有一个CoroutineContext.Element类型的伴生对象Key。由CoroutineContext的集合根据对应的
 *  Key可以获取指定的Element。
 *  3 CoroutineStart 协程启动模式
 *      是一个枚举类，有四种状态。
 *      DEFALUT 默认启动模式；协程创建之后，立即调度协程准备执行。虽然是立即调度准备执行，但并未开始执行，
 *  协程仍然可以在执行前被取消。
 *      LAZY 协程创建之后，不是立即调度协程准备执行，当调用start、join或await类似的方法的时候，才调度
 *  协程准备执行。
 *      ATOMIC 原子性的模式；当协程创建之后，原子性的立即调度协程准备执行，协程在执行到第一个挂起点之前
 *  不会被取消。
 *      UNDISPATCHED 非调度的模式；协程创建之后，不经过调度立即执行。协程在执行到第一个挂起点之前不会被
 *  取消。与ATOMIC相同的地方是都会立即执行，在执行到第一个挂起点之前不会被取消。不同点在于UNDISPATCHED
 *  模式不经过调度立即执行的。当然在遇到挂起点之后的执行，取决于挂起点本事的逻辑和协程上下文中的调度器。
 *  4 CoroutineScope 协程作用域
 *      CoroutineScope为协程定义了作用范围，每个协程构建器launch、async等都是CoroutineScope的扩展。
 *  是一个接口，只有一个属性coroutineContext。
 *      通过方法MainScope()和对象声明的GlobalScope可以获得CoroutineScope对象，都是顶级作用域。
 *      由方法MainScope()获取的CoroutineScope是一个在主线程中执行的协程作用域。
 *      4.1 作用域的分类
 *      顶级作用域：没有父协程的协程的作用域即为顶级作用域。
 *      协同作用域：在协程中启动一个协程，新的协程即为所在协程的子协程，子协程的作用域默认情况下就是
 *  协同作用域。当子协程发生未捕获的异常，会将异常传递给父协程处理，如果父协程被取消，则所有的子协程也
 *  会被同时取消。
 *      监督作用域/主从作用域：todo
 *
 *      父协程需要等待所有的子协程执行完毕之后进入completed状态。
 *      协程的声明周期演化形态
 *
 *      wait children
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
 *
 *
 *  子协程会继承父协程的协程上下文中的Element，如果自身有相同的Key成员，则覆盖指定Key的Element元素，覆盖
 *  的效果仅限自身范围内有效。
 */
class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG: String = "MainActivity"
    }
    private lateinit var btn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn = findViewById(R.id.btn)
        btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                start()
            }

        })

    }

    @DelicateCoroutinesApi
    private fun start() {
        /**
         *  当满足下列条件的时候，协程之间是同步的
         *  （1）父协程的协程调度器是Dispatchers.Main
         *  （2）子协程没有指定相应的协程调度器。
         */
//        GlobalScope.launch(Dispatchers.Main) {
//            for (index in 1 until 10) {
////                startCoroutine(index)
//                startCoroutineDefalutDispatcher(index)
//            }
//        }

//        plusCoroutineContextElement()
//        coroutineStartStudy()
//        coroutineUndispatchedStudy()
//        coroutineUndispatchedStudy2()
//        coroutineUndispatchedStudy3()
//        coroutineScopeStudy()
//        coroutineScopeStudy2()
        coroutineScopeStudy3()

    }

    private fun coroutineScopeStudy3() {
        val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
            Log.d(TAG, "coroutineScopeStudy3: wyj exceptionHandler ${context[CoroutineName]} " +
                    "$throwable")
        }
        GlobalScope.launch(Dispatchers.Main + CoroutineName("scope1") + exceptionHandler) {
            supervisorScope {
                Log.d(TAG, "coroutineScopeStudy3: wyj scope1 ---->1")
                launch(CoroutineName("scope2")) {
                    Log.d(TAG, "coroutineScopeStudy3: wyj socpe2 --->2")
                    throw NullPointerException("scope2 空指针")
                    Log.d(TAG, "coroutineScopeStudy3: wyj scope2 --->3")
                    val scope3Job = launch(CoroutineName("scope3")) {
                        Log.d(TAG, "coroutineScopeStudy3: wyj scope3 --->4")
                        delay(2000L)
                        Log.d(TAG, "coroutineScopeStudy3: wyj scope3 --->5")
                    }
                    scope3Job.join()
                }
                val scope4Job = launch(CoroutineName("scope4")) {
                    Log.d(TAG, "coroutineScopeStudy3: wyj scope4 ---->6")
                    delay(2000L)
                    Log.d(TAG, "coroutineScopeStudy3: wyj scope4 ---->7")
                }
                scope4Job.join()
                Log.d(TAG, "coroutineScopeStudy3: wyj scope1 ---->8")
            }
        }
    }

    /**
     *  子协程scope2抛出异常，并将异常传递给了父协程scope1，父协程取消，导致其子协程scope3也取消了。
     */
    private fun coroutineScopeStudy2() {
        val exceptionHanlder = CoroutineExceptionHandler {context, throwable ->
            Log.d(
                TAG,
                "coroutineScopeStudy2: wyj exceptionHandler ${context[CoroutineName]}," +
                        " $throwable."
            )
        }

        GlobalScope.launch(Dispatchers.Main + CoroutineName("scope1") + exceptionHanlder) {
            Log.d(TAG, "coroutineScopeStudy2: wyj scope1 ------> 1")
            launch(CoroutineName("scope2") + exceptionHanlder) {
                Log.d(TAG, "coroutineScopeStudy2: wyj scope2 ----->2")
                throw NullPointerException("scope2 空指针")
                Log.d(TAG, "coroutineScopeStudy2: wyj scope2 ----->3")
            }
            val scope3Job = launch(CoroutineName("scope3") + exceptionHanlder) {
                Log.d(TAG, "coroutineScopeStudy2: wyj scope3 ---->4")
                delay(2000L)
                Log.d(TAG, "coroutineScopeStudy2: wyj scope3 ---->5")
            }
            scope3Job.join()
            Log.d(TAG, "coroutineScopeStudy2: wyj scope1 ---->6")
        }
    }

    private fun coroutineScopeStudy() {
        GlobalScope.launch(context = Dispatchers.Main) {
            Log.d(TAG, "coroutineScopeStudy: wyj 父协程 $coroutineContext")
            launch(context = CoroutineName("第一个子协程")) {
                Log.d(TAG, "coroutineScopeStudy: wyj 第一个子协程 $coroutineContext")
            }
            launch(context = Dispatchers.Unconfined) {
                Log.d(TAG, "coroutineScopeStudy: wyj 第二个子协程 $coroutineContext")
            }
        }
    }

    private fun coroutineUndispatchedStudy3() {
        GlobalScope.launch(context = Dispatchers.Main) {
            val ioJob = launch(start = CoroutineStart.UNDISPATCHED) {
                Log.d(TAG, "coroutineUndispatchedStudy: wyj 线程:${Thread.currentThread().name}, 挂起前")
                delay(100L)
                Log.d(
                    TAG,
                    "coroutineUndispatchedStudy: wyj 线程：${Thread.currentThread().name}, 挂起后。"
                )
            }
            Log.d(TAG, "coroutineUndispatchedStudy: wyj 线程：${Thread.currentThread().name}, join之前。")
            ioJob.join()
            Log.d(TAG, "coroutineUndispatchedStudy: wyj 线程：${Thread.currentThread().name}, join之后。")
        }
    }

    /**
     *  先执行ioJob协程体挂起点之前的代码，且挂起点之前的线程是在主线程，接着执行mainJob协程体中、join之前的代码
     *  然后执行ioJob协程挂起点之后的代码，且挂起点之后的线程是子线程，最后执行mainJob join之后的代码。
     *  先执行ioJob协程体挂起点之前的代码也说明了CoroutineStart.UNDISPATCHED的启动模式的协程是不经过调度立即
     *  执行的。
     *  由于CoroutineStart.UNDISPATCHED模式启动的协程是不经过调度立即执行的，所以在挂起点之前位于
     *  主线程，由于ioJob的调度器是Dispatchers.IO所以挂起点之后是位于子线程的。
     */
    private fun coroutineUndispatchedStudy2() {
        GlobalScope.launch(context = Dispatchers.Main) {
            val ioJob = launch(context = Dispatchers.IO, start = CoroutineStart.UNDISPATCHED) {
                Log.d(TAG, "coroutineUndispatchedStudy: wyj 线程:${Thread.currentThread().name}, 挂起前")
                delay(100L)
                Log.d(
                    TAG,
                    "coroutineUndispatchedStudy: wyj 线程：${Thread.currentThread().name}, 挂起后。"
                )
            }
            Log.d(TAG, "coroutineUndispatchedStudy: wyj 线程：${Thread.currentThread().name}, join之前。")
            ioJob.join()
            Log.d(TAG, "coroutineUndispatchedStudy: wyj 线程：${Thread.currentThread().name}, join之后。")
        }
    }

    private fun coroutineUndispatchedStudy() {
        GlobalScope.launch(context = Dispatchers.Main) {
            val ioJob = launch(context = Dispatchers.IO) {
                Log.d(TAG, "coroutineUndispatchedStudy: wyj 线程:${Thread.currentThread().name}, 挂起前")
                delay(100L)
                Log.d(
                    TAG,
                    "coroutineUndispatchedStudy: wyj 线程：${Thread.currentThread().name}, 挂起后。"
                )
            }
            Log.d(TAG, "coroutineUndispatchedStudy: wyj 线程：${Thread.currentThread().name}, join之前。")
            ioJob.join()
            Log.d(TAG, "coroutineUndispatchedStudy: wyj 线程：${Thread.currentThread().name}, join之后。")
        }
    }

    /**
     *  undispatchedJob、atomicJob都执行了、lazyJob未执行，DefaultJob有可能没有执行。
     *  undispatchedJob和atomicJob在挂起点之后都被取消，挂起点之后的代码未执行。
     */
    private fun coroutineStartStudy() {
        val defaultJob = GlobalScope.launch {
            Log.d(TAG, "coroutineStartStudy: wyj CoroutineStart.DEFAULT.")
        }
        defaultJob.cancel()
        val lazyJob = GlobalScope.launch(start = CoroutineStart.LAZY) {
            Log.d(TAG, "coroutineStartStudy: wyj CoroutineStart.LAZY.")
        }
        val atomicJob = GlobalScope.launch(start = CoroutineStart.ATOMIC) {
            Log.d(TAG, "coroutineStartStudy: wyj CoroutineStart.ATOMIC 挂起点之前。")
            delay(100L)
            Log.d(TAG, "coroutineStartStudy: wyj CoroutineStart.ATOMIC 挂起点之后。")
        }
        atomicJob.cancel()
        val undispatchedJob = GlobalScope.launch(start = CoroutineStart.UNDISPATCHED) {
            Log.d(TAG, "coroutineStartStudy: wyj CoroutineStart.UNDISPATCHED 挂起点之前。")
            delay(100L)
            Log.d(TAG, "coroutineStartStudy: wyj CoroutineStart.UNDISPATCHED 挂起点之后。")
        }
        undispatchedJob.cancel()
    }

    private fun plusCoroutineContextElement() {
        val coroutineContext1 = Job() + CoroutineName("第一个上下文")
        Log.d(TAG, "start: wyj coroutineContext1:$coroutineContext1")
        val coroutineContext2 = coroutineContext1 + Dispatchers.Default + CoroutineName("第二个上下文")
        Log.d(TAG, "start: wyj coroutineContext2:$coroutineContext2")
        val coroutineContext3 = coroutineContext2 + Dispatchers.Main + CoroutineName("第三个上下文")
        Log.d(TAG, "start: wyj coroutineContext3:$coroutineContext3")
    }

    /**
     *  根据多次的测试结果发现index为2 的job有时会会先于index为1的job，有时候会落后于index为1的job。
     *  说明了index为2的协程与其他index的协程是并发的，而非同步。
     */
    private fun CoroutineScope.startCoroutineDefalutDispatcher(index: Int) {
        if (index == 2) {
            launch(Dispatchers.Default) {
                Log.d(TAG, "start: wyj index 2")
            }
        } else {
            launch {
                Log.d(TAG, "start: wyj index:$index")
            }
        }
    }

    private fun CoroutineScope.startCoroutine(index: Int) {
        launch {
            Log.d(TAG, "start: wyj index:$index")
        }
    }
}