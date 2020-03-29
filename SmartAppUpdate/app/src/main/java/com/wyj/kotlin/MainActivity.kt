package com.wyj.kotlin

import android.app.ProgressDialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.sdk27.coroutines.onClick

class MainActivity : AppCompatActivity() {
    //mDialog 初始化的懒加载
    private val mDialog:ProgressDialog by lazy {
        ProgressDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_update.onClick {
            update()
        }
    }

    fun update() {
        var pm: PackageManager = packageManager
        var applicationInfo = pm.getApplicationInfo("com.ss.android.article.news", 0)
        var sourceDir = applicationInfo.sourceDir

        mDialog.show()
        //子线程处理异步任务
        doAsync {

        }
    }


}
