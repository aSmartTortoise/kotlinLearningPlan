package com.wyj.mvplayter.utils

import android.content.Intent
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.wyj.mvplayter.R
import com.wyj.mvplayter.ui.activity.SettingActivity

interface ToolbarManager {
    val mToolbar: Toolbar

    fun initMainToolbar() {
        mToolbar.setTitle("MVPlayter plus")
        mToolbar.inflateMenu(R.menu.main)
        //如果Java的接口只有一个方法，那么kotlin语法，可以省略接口对象，直接用{}表示方法，
        mToolbar.setOnMenuItemClickListener {
            when(it?.itemId) {
                R.id.setting -> {
                    mToolbar.context.startActivity(Intent(mToolbar.context, SettingActivity::class.java))
                }
            }
            true
        }
//        mToolbar.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener {
//            override fun onMenuItemClick(item: MenuItem?): Boolean {
//                when(item?.itemId) {
//                    R.id.setting -> {
//                        Toast.makeText(mToolbar.context, "设置", Toast.LENGTH_LONG).show()
//                    }
//                }
//                return true
//            }
//
//        })
    }

    fun initSettingToolbar() {
        mToolbar.setTitle("设置")
    }

    fun initAboutToolbar() {
        mToolbar.setTitle("关于")
    }
}