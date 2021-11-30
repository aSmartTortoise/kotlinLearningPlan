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
        GlobalScope.launch {
            val job = launch {
                Log.d(TAG, "start: wyj launch 启动一个协程。")
            }
            Log.d(TAG, "start: wyj job:$job")
            val defered = async {
                Log.d(TAG, "start: wyj async 启动一个协程。")
                "返回值是啥"
            }
            Log.d(TAG, "start: wyj defered result ${defered.await()}")
            Log.d(TAG, "start: wyj defered $defered")
        }


    }
}