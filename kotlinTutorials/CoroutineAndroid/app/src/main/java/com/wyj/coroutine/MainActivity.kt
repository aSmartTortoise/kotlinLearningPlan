package com.wyj.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.coroutines.*

/**
 *  https://juejin.cn/post/6953441828100112392
 *
 *  1 CoroutineDispatcher
 *      协程调度器：调度器确定了相关的协程在哪个线程或哪些线程上运行。协程调度器可以限制协程在某个线程上运行、
 *  可以将协程分派到一个线程池中、或者让协程不受限制地运行。
 *  2 CoroutineContext
 *      它是一个包含了用户定义的各种不同元素的Element的集合。其中主要元素有Job、CoroutineDispatcher、
 *  CoroutineExceptionHandler、ContinuationInterceptor、CoroutineName。这些都是Element的衍生类。
 *  这些类或接口都有一个CoroutineContext.Element类型的伴生对象Key。由CoroutineContext的集合根据对应的
 *  Key可以获取指定的Element。
 *
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