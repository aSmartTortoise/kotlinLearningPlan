package com.jie.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
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
        lifecycleScope.launch {
            delay(2000L)
            Toast.makeText(this@MainActivity, "haha", Toast.LENGTH_LONG).show()
        }
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