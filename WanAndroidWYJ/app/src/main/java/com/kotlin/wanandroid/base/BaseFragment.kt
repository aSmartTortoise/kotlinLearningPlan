package com.kotlin.wanandroid.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.cxz.multiplestatusview.MultipleStatusView
import com.kotlin.wanandroid.WanAndroidApplication
import com.kotlin.wanandroid.constant.Constant
import com.kotlin.wanandroid.event.NetworkChangeEvent
import com.kotlin.wanandroid.utils.Preference
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class BaseFragment : Fragment() {
    private var mIsViewPrepare = false
    private var hasLoadData = false
    protected var mLayoutStatusView: MultipleStatusView? = null
    protected var mIsLogin: Boolean by Preference(Constant.LOGIN_KEY, false)
    protected var mHasNetwork: Boolean by Preference(Constant.HAS_NETWORK_KEY, true)
    val mRetryClickListener: View.OnClickListener = View.OnClickListener {
        lazyLoad()
    }
    protected var TAG: String? = null

    init {
        TAG = this.javaClass.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(attachLayoutRes(), null)
    }

    @LayoutRes
    abstract fun attachLayoutRes(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
        mIsViewPrepare = true
        initView(view)
        lazyLoadDataIfPrepared()
        mLayoutStatusView?.setOnClickListener(mRetryClickListener)

    }

    open fun useEventBus(): Boolean = true

    abstract fun initView(view: View)

    private fun lazyLoadDataIfPrepared() {
        if (userVisibleHint && mIsViewPrepare && !hasLoadData) {
            lazyLoad()
            hasLoadData = true
        }
    }

    abstract fun lazyLoad()

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            lazyLoadDataIfPrepared()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNetworkChangeEvent(event: NetworkChangeEvent) {
        if (event.isConnected) {
            doReconnected()
        }
    }

    open fun doReconnected() {
        lazyLoad()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }
}