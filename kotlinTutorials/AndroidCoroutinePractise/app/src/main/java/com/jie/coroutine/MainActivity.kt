package com.jie.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.jie.coroutine.extention.delayMain
import com.jie.coroutine.extention.requestIO
import com.jie.coroutine.extention.requestMain
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    init {
        lifecycleScope.launchWhenResumed {
            Log.d(TAG, "init wyj thread:${Thread.currentThread().name} ")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestMain {
            delay(2000L)
            Toast.makeText(this@MainActivity, "haha0", Toast.LENGTH_LONG).show()
        }

        requestIO {
            loadNetData()
        }

        delayMain(100L) {
            Toast.makeText(this@MainActivity, "hihi0", Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun loadNetData() {
        Log.d(TAG, "loadNetData: wyj")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: wyj")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: wyj")
    }
}