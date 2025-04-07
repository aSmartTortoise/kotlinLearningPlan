package com.kotlin.wanandroid.utils

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.util.Log
import kotlin.properties.Delegates

/**
 * Created by chenxz on 2018/4/21.
 */
object DisplayManager {
    private var displayMetrics: DisplayMetrics? = null

    private var screenWidth: Int by Delegates.notNull<Int>()

    private var screenHeight: Int by Delegates.notNull<Int>()

    private var screenDpi: Int by Delegates.notNull<Int>()
    private var nonCompatDensity: Float = 0f
    private var nonCompatScaledDensity: Float = 0f

    fun init(context: Context) {
        displayMetrics = context.resources.displayMetrics
        screenWidth = displayMetrics?.widthPixels ?: 0
        screenHeight = displayMetrics?.heightPixels ?: 0
        screenDpi = displayMetrics?.densityDpi ?: 0
        val scaledDensity = displayMetrics?.scaledDensity
    }


    //UI图的大小
    private val STANDARD_WIDTH = 1080
    private val STANDARD_HEIGHT = 1920

    /**
     * 传入UI图中问题的高度，单位像素
     * @param size
     * @return
     */
    fun getPaintSize(size: Int): Int {
        return getRealHeight(size)
    }

    /**
     * 输入UI图的尺寸，输出实际的px
     *
     * @param px ui图中的大小
     * @return
     */
    fun getRealWidth(px: Int): Int {
        //ui图的宽度
        return getRealWidth(px, STANDARD_WIDTH.toFloat())
    }

    /**
     * 输入UI图的尺寸，输出实际的px,第二个参数是父布局
     *
     * @param px          ui图中的大小
     * @param parentWidth 父view在ui图中的高度
     * @return
     */
    fun getRealWidth(px: Int, parentWidth: Float): Int {
        return (px / parentWidth * screenWidth).toInt()
    }

    /**
     * 输入UI图的尺寸，输出实际的px
     *
     * @param px ui图中的大小
     * @return
     */
    fun getRealHeight(px: Int): Int {
        //ui图的宽度
        return getRealHeight(px, STANDARD_HEIGHT.toFloat())
    }

    /**
     * 输入UI图的尺寸，输出实际的px,第二个参数是父布局
     *
     * @param px           ui图中的大小
     * @param parentHeight 父view在ui图中的高度
     * @return
     */
    fun getRealHeight(px: Int, parentHeight: Float): Int {
        return (px / parentHeight * screenHeight).toInt()
    }

    /**
     * dip转px
     * @param dipValue
     * @return int
     */
    fun dip2px(dipValue: Float): Int {
        val scale = displayMetrics?.density
        return (dipValue * scale!! + 0.5f).toInt()
    }

    /**
     *  字节跳动屏幕适配方案 https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA
     */
    fun setCustomDensity(activity: Activity, app: Application) {
        val appDisplayMetrics = app.resources.displayMetrics
        if (nonCompatDensity == 0f) {
            nonCompatDensity = appDisplayMetrics.density
            nonCompatScaledDensity = appDisplayMetrics.scaledDensity
            app.registerComponentCallbacks(object : ComponentCallbacks {


                override fun onConfigurationChanged(newConfig: Configuration) {
                    Log.d("DisplayManager", "onConfigurationChanged: wyj")
                    if (newConfig.fontScale > 0) {
                        nonCompatScaledDensity = app.resources.displayMetrics.scaledDensity
                    }
                }

                override fun onLowMemory() {
                }



            })
        }
        val widthPixels = appDisplayMetrics.widthPixels
        val heightPixels = appDisplayMetrics.heightPixels
        val densityDpi = appDisplayMetrics.densityDpi
        Log.d("DisplayManager", "setCustomDensity before: wyj width:$widthPixels"  + " height:$heightPixels"
        + " densityDpi:$densityDpi" + " density:${appDisplayMetrics.density}" +
        " scaledDensity:${appDisplayMetrics.scaledDensity}" )
        val targetDensity: Float = (appDisplayMetrics.widthPixels / 360).toFloat()
        val targetScaledDensity = targetDensity * nonCompatScaledDensity / nonCompatDensity
        val targetDensityDpi: Int = 160 * targetDensity.toInt()
        appDisplayMetrics.density =  targetDensity
        appDisplayMetrics.scaledDensity = targetScaledDensity
        appDisplayMetrics.densityDpi = targetDensityDpi
        Log.d("DisplayManager", "setCustomDensity after: wyj"
                + " densityDpi:$targetDensityDpi" + " density:$targetDensity" +
                " scaledDensity:$targetScaledDensity" )
        val activityDisplayMetrics = activity.resources.displayMetrics
        activityDisplayMetrics.density = targetDensity
        activityDisplayMetrics.scaledDensity = targetScaledDensity
        activityDisplayMetrics.densityDpi = targetDensityDpi
    }
}