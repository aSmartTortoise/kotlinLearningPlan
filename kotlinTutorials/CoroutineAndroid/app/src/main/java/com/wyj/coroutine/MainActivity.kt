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
        GlobalScope.launch(Dispatchers.Main) {
            for (index in 1 until 10) {
//                startCoroutine(index)
                startCoroutineDefalutDispatcher(index)
            }
        }
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