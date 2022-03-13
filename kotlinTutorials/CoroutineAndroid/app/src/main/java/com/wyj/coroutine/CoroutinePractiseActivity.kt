package com.wyj.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.coroutines.*

class CoroutinePractiseActivity : AppCompatActivity() {
    companion object {
        const val TAG = "CoroutinePractise"
    }
    private var mBtn: Button? = null
    private var mJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine_practise)
        mBtn = findViewById<Button>(R.id.btn_start)?.apply {
            setOnClickListener {
                start()
            }
        }
    }

    private fun start() {
        //launch方法中设置Dispatchers.main保证在主线程刷新UI，同时设置SupervisorJob，保证在子协程
        //抛异常取消，不会导致整个协程树终结。在Activity的onDestory的时候调用job.cancel取消整个
        //协程树。
        mJob = GlobalScope.launch(Dispatchers.Main + SupervisorJob()) {
            launch {
                try {
                    throw NullPointerException()
                } catch (e: Exception) {
                    Log.d(TAG, "start: wyj nullpointerexception name:$CoroutineName")
                }
            }

            val result = withContext(Dispatchers.IO) {
                delay(200L)
                //网络请求
                "请求结果"
            }

            launch {
                //网络请求2
            }

            mBtn?.text = result
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mJob?.cancel()
    }
}