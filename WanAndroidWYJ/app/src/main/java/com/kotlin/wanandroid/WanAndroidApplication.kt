package com.kotlin.wanandroid

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatDelegate
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

class WanAndroidApplication: Application() {

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
        var context: Context by Delegates.notNull()
            private set
        lateinit var instance: WanAndroidApplication

        var userInfo: UserInfoBody? = null
        val TAG = "wan_android"

        fun getRefWatcher(context: Context): RefWatcher? {
            val app = context.applicationContext as WanAndroidApplication
            return app.refWatcher
        }
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
            .showThreadInfo(false)
            .methodCount(0)
            .methodOffset(7)
            .tag(TAG)
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
        val processName = CommonUtil.getProcessName(android.os.Process.myPid())
        val strategy = CrashReport.UserStrategy(applicationContext)
        strategy.isUploadProcess = false || processName == applicationContext.packageName
        Beta.upgradeStateListener = mUpgradeStateListener
        Beta.upgradeDialogLayoutId = R.layout.layout_upgrade_dialog
        Bugly.init(context, Constant.BUGLY_ID, BuildConfig.DEBUG, strategy)
    }

}