package com.wyj.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

/**
 *  https://juejin.cn/post/6956115368578383902#heading-2
 */
class CoroutinePractiseActivity : AppCompatActivity() {
    companion object {
        const val TAG = "CoroutinePractise"
    }

    init {
        lifecycleScope.launchWhenResumed {
            Log.d(TAG, "init 启动协程 $coroutineContext")
        }
    }
    private var mBtn: Button? = null
    private var mJob: Job? = null
    private val mMainScope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine_practise)
        mBtn = findViewById<Button>(R.id.btn_start)?.apply {
            setOnClickListener {
                start()
            }
        }

        lifecycleScope.launch {
            delay(200L)
            Toast.makeText(this@CoroutinePractiseActivity, "lifecycleScope 启动协程", Toast.LENGTH_LONG).show()
        }
    }

    private fun start() {
        //launch方法中设置Dispatchers.main保证在主线程刷新UI，同时设置SupervisorJob，保证在子协程
        //抛异常取消，不会导致整个协程树终结。在Activity的onDestory的时候调用job.cancel取消整个
        //协程树。
//        mJob = GlobalScope.launch(Dispatchers.Main + SupervisorJob()) {
//            processJob()
//        }
        //通过mainScope启动的协程，没必要保存Job实例，需要的是在Activity的onDestroy方法中，调用
        //mainScope.cancle就可以取消整个协程树。
        mMainScope.launch {
            processJob()
        }
    }

    private suspend fun CoroutineScope.processJob() {
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

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: wyj")
    }

    override fun onDestroy() {
        super.onDestroy()
        mJob?.cancel()
        mMainScope.cancel()
    }
}