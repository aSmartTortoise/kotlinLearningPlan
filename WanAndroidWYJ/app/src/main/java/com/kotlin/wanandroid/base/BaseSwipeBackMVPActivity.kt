package com.kotlin.wanandroid.base

import android.os.Bundle
import android.util.Log
import com.cxz.swipelibrary.SwipeBackActivityBase
import com.cxz.swipelibrary.SwipeBackActivityHelper
import com.cxz.swipelibrary.SwipeBackLayout
import com.cxz.swipelibrary.Utils

abstract class BaseSwipeBackMVPActivity<in V: IView, P: IPresenter<V>>
    : BaseMVPActivity<V, P>(), SwipeBackActivityBase {
    private lateinit var mHelper: SwipeBackActivityHelper
    private val TAG: String = "BaseSwipeBackActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: wyj")
        mHelper = SwipeBackActivityHelper(this)
        mHelper.onActivityCreate()
        initSwipeBack()

    }

    private fun initSwipeBack() {
        setSwipeBackEnable(enableSwipeBack())
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT)
    }

    fun enableSwipeBack() = true

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        Log.d(TAG, "onPostCreate: wyj")
        mHelper.onPostCreate()
    }

    override fun getSwipeBackLayout(): SwipeBackLayout = mHelper.swipeBackLayout

    override fun setSwipeBackEnable(enable: Boolean) {
        swipeBackLayout.setEnableGesture(enable)
    }

    override fun scrollToFinishActivity() {
        Log.d(TAG, "scrollToFinishActivity: wyj")
        Utils.convertActivityFromTranslucent(this)
        swipeBackLayout.scrollToFinishActivity()
    }
}