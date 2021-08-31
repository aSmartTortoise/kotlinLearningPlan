package com.kotlin.wanandroid.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.android.material.navigation.NavigationView
import com.kotlin.wanandroid.R
import com.kotlin.wanandroid.base.BaseMVPActivity
import com.kotlin.wanandroid.ext.showToast
import com.kotlin.wanandroid.mvp.contract.MainContract
import com.kotlin.wanandroid.mvp.model.bean.UserInfoBody
import com.kotlin.wanandroid.mvp.presenter.MainPresenter
import com.kotlin.wanandroid.utils.SettingUtil
import com.tencent.bugly.beta.Beta
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : BaseMVPActivity<MainContract.View, MainContract.Presenter>(), MainContract.View {
    private val BOTTOM_INDEX: String = "bottom_index"
    private val TAG = "MainActivity"

    private val FRAGMENT_HOME = 0x01
    private val FRAGMENT_SQUARE = 0x02
    private val FRAGMENT_WECHAT = 0x03
    private val FRAGMENT_SYSTEM = 0x04
    private val FRAGMENT_PROJECT = 0x05

    private var mIndex = FRAGMENT_HOME

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            return@OnNavigationItemSelectedListener when(item.itemId) {
                R.id.action_home -> {
                    Log.d(TAG, "onNavigationItemSelected: home")
                    true
                }
                else -> false
            }

    }

    private val onDrawerNavigationItemSelectedListener =
        NavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_score -> {
                    if (isLogin) {
                        Log.d(TAG, "onNavigationItemSelected wyj: 去积分榜")
                    } else {
                        showToast(resources.getString(R.string.login_tint))
                        Log.d(TAG, "onNavigationItemSelected wyj 去登陆: ")
                    }
                }

                R.id.nav_collect -> {
                    if (isLogin) {
                        Log.d(TAG, "onNavigationItemSelected wyj: collect type")
                    } else {
                        showToast(resources.getString(R.string.login_tint))
                        Log.d(TAG, "onNavigationItemSelected wyj 去登陆: ")
                    }
                }

                R.id.nav_share -> {
                    if (isLogin) {
                        Log.d(TAG, "onNavigationItemSelected wyj: share")
                    } else {
                        showToast(resources.getString(R.string.login_tint))
                        Log.d(TAG, "onNavigationItemSelected wyj 去登陆: ")
                    }
                }

                R.id.nav_setting -> {
                    Log.d(TAG, "onNavigationItemSelected wyj: 去设置")
                }

                R.id.nav_logout -> {
                    Log.d(TAG, "onNavigationItemSelected wyj: 退出登录")
                }

                R.id.nav_night_mode -> {
                    if (SettingUtil.getIsNightMode()) {
                        SettingUtil.setIsNightMode(false)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    } else {
                        SettingUtil.setIsNightMode(true)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    window.setWindowAnimations(R.style.WindowAnimationFadeInOut)
                    recreate()
                }

                R.id.nav_todo -> {
                    if (isLogin) {
                        Log.d(TAG, "onNavigationItemSelected wyj: to do")
                    } else {
                        showToast(resources.getString(R.string.login_tint))
                        Log.d(TAG, "onNavigationItemSelected wyj 去登陆: ")
                    }
                }
            }

            true

    }
    override fun attachLayoutRes(): Int = R.layout.activity_main

    override fun useEventBus(): Boolean = true;

    override fun initData() {
        Beta.checkUpgrade(false, false)
    }

    override fun initView() {
        super.initView()
        toolbar.run {
            title = getString(R.string.app_name)
            setSupportActionBar(this)
        }

        bottom_navigation.run {
            // 以前使用 BottomNavigationViewHelper.disableShiftMode(this) 方法来设置底部图标和字体都显示并去掉点击动画
            // 升级到 28.0.0 之后，官方重构了 BottomNavigationView ，目前可以使用 labelVisibilityMode = 1 来替代
            // BottomNavigationViewHelper.disableShiftMode(this)
            labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
            setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        }

        initDrawerLayout()
        initNavView()
    }

    private fun initNavView() {
        nav_view.run {
            setNavigationItemSelectedListener(onDrawerNavigationItemSelectedListener)
        }
    }

    private fun initDrawerLayout() {
        drawer_layout.run {
            val toggle = ActionBarDrawerToggle(
                this@MainActivity,
                this,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            addDrawerListener(toggle)
            toggle.syncState()
        }
    }

    override fun start() {
    }

    override fun createPrenter(): MainContract.Presenter = MainPresenter()

    override fun showLogoutSuccess(success: Boolean) {
        TODO("Not yet implemented")
    }

    override fun showUserInfo(bean: UserInfoBody) {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mIndex = savedInstanceState?.getInt(BOTTOM_INDEX) ?: mIndex
        super.onCreate(savedInstanceState)
    }




}
