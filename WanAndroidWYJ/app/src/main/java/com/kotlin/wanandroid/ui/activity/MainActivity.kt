package com.kotlin.wanandroid.ui.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.net.TrafficStats
import android.os.AsyncTask
import android.os.Bundle
import android.os.Trace
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.android.material.navigation.NavigationView
import com.kotlin.wanandroid.R
import com.kotlin.wanandroid.WanAndroidApplication
import com.kotlin.wanandroid.base.BaseMVPActivity
import com.kotlin.wanandroid.constant.Constant
import com.kotlin.wanandroid.event.ColorEvent
import com.kotlin.wanandroid.event.LoginEvent
import com.kotlin.wanandroid.ext.showToast
import com.kotlin.wanandroid.mvp.contract.MainContract
import com.kotlin.wanandroid.mvp.model.bean.UserInfoBody
import com.kotlin.wanandroid.mvp.presenter.MainPresenter
import com.kotlin.wanandroid.ui.frament.HomeFragment
import com.kotlin.wanandroid.utils.DialogUtil
import com.kotlin.wanandroid.utils.Preference
import com.kotlin.wanandroid.utils.SettingUtil
import com.tencent.bugly.beta.Beta
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : BaseMVPActivity<MainContract.View, MainContract.Presenter>(), MainContract.View {
    private val BOTTOM_INDEX: String = "bottom_index"
    private val TAG = "MainActivity"

    private val FRAGMENT_HOME = 0x01
    private val FRAGMENT_SQUARE = 0x02
    private val FRAGMENT_WECHAT = 0x03
    private val FRAGMENT_SYSTEM = 0x04
    private val FRAGMENT_PROJECT = 0x05

    private var mIndex = FRAGMENT_HOME
    private var mHomeFragment: HomeFragment? = null

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

    private val mDialog by lazy {
        DialogUtil.getWaitDialog(this, resources.getString(R.string.logout_ing))
    }

    private val onDrawerNavigationItemSelectedListener =
        NavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_score -> {
                    if (mIsLogin) {
                        Intent(this@MainActivity, ScoreActivity::class.java).run {
                            startActivity(this)
                        }
                    } else {
                        goLogin()
                    }
                }

                R.id.nav_collect -> {
                    if (mIsLogin) {
                        Log.d(TAG, "onNavigationItemSelected wyj: collect type")
                    } else {
                        showToast(resources.getString(R.string.login_tint))
                        Log.d(TAG, "onNavigationItemSelected wyj 去登陆: ")
                    }
                }

                R.id.nav_share -> {
                    if (mIsLogin) {
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
                    logout()
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
                    if (mIsLogin) {
                        Log.d(TAG, "onNavigationItemSelected wyj: to do")
                    } else {
                        showToast(resources.getString(R.string.login_tint))
                        Log.d(TAG, "onNavigationItemSelected wyj 去登陆: ")
                    }
                }
            }

            true

    }

    private fun goLogin() {
        showToast(resources.getString(R.string.login_tint))
        Intent(this@MainActivity, LoginActivity::class.java).run {
            startActivity(this)
        }
    }

    private fun logout() {
        DialogUtil.getConfirmDialog(this, resources.getString(R.string.confirm_logout),
            DialogInterface.OnClickListener { _, _ ->
                mDialog.show()
                mPresenter?.logout()
            }).show()
    }

    private var mTvUserName: TextView? = null
    private var mTvUserId: TextView? = null
    private var mTvUsrGrade: TextView? = null
    private var mTvUserRank: TextView? = null
    private var mIvRank: ImageView? = null
    private var mTvScore: TextView? = null
    private var mUsername: String by Preference(Constant.USERNAME_KEY, "")

    override fun attachLayoutRes(): Int = R.layout.activity_main

    override fun useEventBus(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        mIndex = savedInstanceState?.getInt(BOTTOM_INDEX) ?: mIndex
        super.onCreate(savedInstanceState)
    }

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
        showFragment(mIndex)
    }

    private fun initNavView() {
        nav_view.run {
            setNavigationItemSelectedListener(onDrawerNavigationItemSelectedListener)
            var headerView = getHeaderView(0)
            headerView.run {
                mTvUserName = findViewById(R.id.tv_username)
                mTvUserId = findViewById(R.id.tv_user_id)
                mTvUsrGrade = findViewById(R.id.tv_user_grade)
                mTvUserRank = findViewById(R.id.tv_user_rank)
                mIvRank = findViewById(R.id.iv_rank)
            }
            mTvScore = menu.findItem(R.id.nav_score).actionView as TextView
            mTvScore?.gravity = Gravity.CENTER_VERTICAL
            menu.findItem(R.id.nav_logout).isVisible = mIsLogin

            mTvUserName?.run {
                text = if (!mIsLogin) getString(R.string.go_login) else mUsername
                setOnClickListener {
                    if (!mIsLogin) {
                        Intent(this@MainActivity, LoginActivity::class.java).run {
                            startActivity(this)
                        }
                    }
                }
            }

            mIvRank?.setOnClickListener {
                startActivity(Intent(this@MainActivity, RankActivity::class.java))
            }
        }
    }

    private fun initDrawerLayout() {
        drawer_layout.run {
            val toggle = ActionBarDrawerToggle(
                this@MainActivity,
                this,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            addDrawerListener(toggle)
            toggle.syncState()
        }
    }

    override fun start() {
        mPresenter?.getUserInfo()
    }

    override fun createPrenter(): MainContract.Presenter = MainPresenter()

    override fun showLogoutSuccess(success: Boolean) {
        if (success) {
            doAsync {
                Preference.clearPreference()
                uiThread {
                    mDialog.dismiss()
                    showToast(resources.getString(R.string.logout_success))
                    mUsername = mTvUserName?.text.toString().trim()
                    mIsLogin = false
                    EventBus.getDefault().post(LoginEvent(false))
                }
            }
        }
    }

    override fun showUserInfo(data: UserInfoBody) {
        WanAndroidApplication.userInfo = data
        mTvUserId?.text = data.userId.toString()
        mTvUsrGrade?.text = (data.coinCount / 100 + 1).toString()
        mTvUserRank?.text = data.rank.toString()
        mTvScore?.text = data.coinCount.toString()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    private fun refreshColor(colorEvent: ColorEvent) {
        if (colorEvent.isRefresh) {
            nav_view.getHeaderView(0).setBackgroundColor(mThemeColor)
            floating_action_btn.backgroundTintList = ColorStateList.valueOf(mThemeColor)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loginEvent(event: LoginEvent) {
        if (event.isLogin) {
            mTvUserName?.text = mUsername
            nav_view.menu.findItem(R.id.nav_logout).isVisible = true
            mPresenter?.getUserInfo()
            //todo homeFragment load
        } else {
            mTvUserName?.text = resources.getString(R.string.go_login)
            nav_view.menu.findItem(R.id.nav_logout).isVisible = false
            mTvUserId?.text = getString(R.string.nav_line_4)
            mTvUsrGrade?.text = getString(R.string.nav_line_2)
            mTvUserRank?.text = getString(R.string.nav_line_2)
            mTvScore?.text = ""
            //todo homefragment load
        }
    }

    private fun showFragment(index: Int) {
        var transaction = supportFragmentManager.beginTransaction()
        hideFragments(transaction)
        mIndex = index
        when(index) {
            FRAGMENT_HOME -> {
                toolbar.title = getString(R.string.app_name)
                if (mHomeFragment == null) {
                    mHomeFragment = HomeFragment.getInstance()
                    transaction.add(R.id.container, mHomeFragment!!, "HOME")
                } else {
                    transaction.show(mHomeFragment!!)
                }
            }
        }
        transaction.commit()
    }

    private fun hideFragments(transaction: FragmentTransaction) {
        mHomeFragment?.let { transaction.hide(it) }
    }

    override fun initColor() {
        super.initColor()
        refreshColor(ColorEvent(true))
    }



    @SuppressLint("UnclosedTrace")
    override fun onResume() {
        super.onResume()
        Trace.beginSection("Main onResume")
        Log.i(TAG, "onResume")
        Trace.endSection()
    }

    @SuppressLint("UnclosedTrace")
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            Trace.beginSection("Main focus true")
            Log.i(TAG, "onWindowFocusChanged: focus true.")
            Trace.endSection()
        }
    }

}
