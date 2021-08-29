package com.kotlin.wanandroid

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.kotlin.wanandroid.constant.Constant
import com.kotlin.wanandroid.ext.showToast
import com.kotlin.wanandroid.mvp.model.bean.UserInfoBody
import com.kotlin.wanandroid.utils.CommonUtil
import com.kotlin.wanandroid.utils.DisplayManager
import com.kotlin.wanandroid.utils.SettingUtil
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.beta.upgrade.UpgradeStateListener
import com.tencent.bugly.crashreport.CrashReport
import org.jetbrains.anko.displayManager
import org.litepal.LitePal
import java.util.*
import kotlin.properties.Delegates

class WanAndroidApplication: MultiDexApplication() {

    private var refWatcher: RefWatcher? = null

    private val mActivityLifecycleCallbacks = object: ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        }

        override fun onActivityResumed(activity: Activity) {
        }

    }

    private val mUpgradeStateListener = object: UpgradeStateListener {
        override fun onDownloadCompleted(isManual: Boolean) {
        }

        override fun onUpgradeSuccess(isManual: Boolean) {
        }

        override fun onUpgradeFailed(isManual: Boolean) {
            if (isManual) {
                showToast(getString(R.string.check_version_fail))
            }
        }

        override fun onUpgrading(isManual: Boolean) {
            if (isManual) {
                showToast(getString(R.string.check_version_ing))
            }
        }

        override fun onUpgradeNoVersion(isManual: Boolean) {
            if (isManual) {
                showToast(getString(R.string.check_no_version))
            }
        }

    }

    companion object {
        val TAG = "wan_android"
        var context: Context by Delegates.notNull()

        lateinit var instance: WanAndroidApplication

        fun getRefWatcher(context: Context): RefWatcher? {
            val app = context.applicationContext as WanAndroidApplication
            return app.refWatcher
        }

        var userInfo: UserInfoBody? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext
        refWatcher = setUpLeakCanary()
        initConfig()
        DisplayManager.init(this)
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
        initTheme()
        initLitePal()
        initBugly()
    }

    private fun setUpLeakCanary(): RefWatcher {
        return if (LeakCanary.isInAnalyzerProcess(this)) {
            RefWatcher.DISABLED
        } else LeakCanary.install(this)
    }

    private fun initConfig() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)// 隐藏线程信息 默认：显示
            .methodCount(0)// 决定打印多少行（每一行代表一个方法）默认：2
            .methodOffset(7)// (Optional) Hides internal method calls up to offset. Default 5
            .tag(TAG)// (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(object: AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }

    private fun initTheme() {
        if (SettingUtil.getIsAutoNightMode()) {
            val nightStartHour = SettingUtil.getNightStartHour()!!.toInt()
            val nightStartMinute = SettingUtil.getNightStartMinute()!!.toInt()
            val dayStartHour = SettingUtil.getDayStartHour()!!.toInt()
            val dayStartMinute = SettingUtil.getDayStartMinute()!!.toInt()

            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            val nightValue = nightStartHour!! * 60 + nightStartMinute
            val dayValue = dayStartHour!! * 60 + dayStartMinute
            val currentValue = currentHour * 60 + currentMinute

            if (currentValue >= nightValue || currentValue <= dayValue) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                SettingUtil.setIsNightMode(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                SettingUtil.setIsNightMode(false)
            }
        } else {
            if (SettingUtil.getIsNightMode()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun initLitePal() {
        LitePal.initialize(this)
    }

    private fun initBugly() {
        if (BuildConfig.DEBUG) {
            return
        }
        Beta.autoCheckUpgrade = false
        Beta.initDelay = 3 * 1000
        Beta.largeIconId = R.mipmap.ic_launcher
        Beta.smallIconId = R.mipmap.ic_launcher
        Beta.defaultBannerId = R.mipmap.ic_launcher
        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        Beta.showInterruptedStrategy = false

        Beta.upgradeStateListener = mUpgradeStateListener
        // 自定义更新布局要设置在 init 之前
        // R.layout.layout_upgrade_dialog 文件要注意两点
        // 注意1: 半透明背景要自己加上
        // 注意2: 即使自定义的弹窗不需要title, info等这些信息, 也需要将对应的tag标出出来, 一共有5个
        Beta.upgradeDialogLayoutId = R.layout.layout_upgrade_dialog
        //获取当前进程名
        val processName = CommonUtil.getProcessName(android.os.Process.myPid())
        val strategy = CrashReport.UserStrategy(applicationContext)
        strategy.isUploadProcess = false || processName == applicationContext.packageName
        Bugly.init(context, Constant.BUGLY_ID, BuildConfig.DEBUG, strategy)
    }

}