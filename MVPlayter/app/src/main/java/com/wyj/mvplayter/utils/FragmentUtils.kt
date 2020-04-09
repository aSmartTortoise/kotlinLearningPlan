package com.wyj.mvplayter.utils

import androidx.fragment.app.Fragment
import com.wyj.mvplayter.R
import com.wyj.mvplayter.base.BaseFragment
import com.wyj.mvplayter.ui.fragment.HomeFragment
import com.wyj.mvplayter.ui.fragment.MvFragment
import com.wyj.mvplayter.ui.fragment.VBangFragment
import com.wyj.mvplayter.ui.fragment.YueDanFragment

/**
 * 单例模式
 */
class FragmentUtils private constructor() {
    //private constructor() 表明 构造方法是私有的
    val homeFragment:HomeFragment by lazy {
        HomeFragment()
    }
    val mvFragment:MvFragment by lazy {
        MvFragment()
    }
    val vBangFragment:VBangFragment by lazy {
        VBangFragment()
    }
    val yueDanFragment:YueDanFragment by lazy {
        YueDanFragment()
    }
    companion object {
        // companion object 声明 为 伴生对象
        //by lazy 惰性加载，且是线程安全的
        val fragmentUtils: FragmentUtils by lazy {
            FragmentUtils()
        }
    }

    fun getFragment(tabId: Int): Fragment? {
        when(tabId) {
            R.id.tab_home -> return homeFragment
            R.id.tab_mv -> return mvFragment
            R.id.tab_vbang -> return vBangFragment
            R.id.tab_yuedan -> return yueDanFragment
        }
        return null
    }
}