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
        val runBlockingJob = runBlocking {
            Log.d("runBlocking", "启动一个协程。")
            23
        }

        Log.d(TAG, "start: runBlockingJob:$runBlockingJob")
        val launchJob = GlobalScope.launch {
            Log.d("launch", "启动一个协程。")
        }
        Log.d(TAG, "start: launchJob:$launchJob")
        var defered = GlobalScope.async {
            Log.d("async", "启动一个协程。")
            "async 协程 defered返回值"
        }
        Log.d(TAG, "start: defered:$defered")
    }
}