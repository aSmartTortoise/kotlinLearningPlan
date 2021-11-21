package com.kotlin.wanandroid.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.just.agentweb.AgentWeb
import com.just.agentweb.NestedScrollAgentWebView
import com.kotlin.wanandroid.R
import com.kotlin.wanandroid.base.BaseSwipeBackMVPActivity
import com.kotlin.wanandroid.constant.Constant
import com.kotlin.wanandroid.ext.getAgentWeb
import com.kotlin.wanandroid.mvp.contract.ContentContract
import com.kotlin.wanandroid.mvp.presenter.ContentPresenter
import com.kotlin.wanandroid.webclient.WebClientFactory
import kotlinx.android.synthetic.main.activity_content.*
import kotlinx.android.synthetic.main.toolbar.*

class ContentActivity : BaseSwipeBackMVPActivity<ContentContract.View, ContentContract.Presenter>(),
    ContentContract.View {

    private var mAgentWeb: AgentWeb? = null
    private var mShareTitle: String = ""
    private var mShareUrl: String = ""
    private var mShareId: Int = -1

    companion object {
        fun start(context: Context?, id: Int, title: String, url: String, bundle: Bundle? = null) {
            Intent(context, ContentActivity::class.java).run {
                putExtra(Constant.CONTENT_ID_KEY, id)
                putExtra(Constant.CONTENT_TITLE_KEY, title)
                putExtra(Constant.CONTENT_URL_KEY, url)
                context?.startActivity(this, bundle)
            }
        }

        fun start(context: Context?, url: String) {
            start(context, -1, "", url)
        }
    }

    private val mWebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            tv_title?.text = title
        }
    }

    override fun attachLayoutRes() = R.layout.activity_content

    override fun initData() {
    }

    override fun initView() {
        super.initView()

        intent?.extras?.let {
            mShareId = it.getInt(Constant.CONTENT_ID_KEY, -1)
            mShareTitle = it.getString(Constant.CONTENT_TITLE_KEY, "")
            mShareUrl = it.getString(Constant.CONTENT_URL_KEY, "")
        }

        toolbar.apply {
            title = ""
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        tv_title.apply {
            text = getString(R.string.loading)
            visibility = View.VISIBLE
            postDelayed({
                tv_title.isSelected = true
            }, 2000L)
        }
        initWebView()

    }

    fun initWebView() {
        val webView = NestedScrollAgentWebView(this)
        val layoutParams = CoordinatorLayout.LayoutParams(-1, -1)
        layoutParams.behavior = AppBarLayout.ScrollingViewBehavior()
        mAgentWeb = mShareUrl.getAgentWeb(this, cl_main,
            layoutParams,
            webView,
            WebClientFactory.create(mShareUrl),
            mWebChromeClient,
            mThemeColor)

        mAgentWeb?.webCreator?.webView?.apply {
            overScrollMode = WebView.OVER_SCROLL_NEVER
            isVerticalScrollBarEnabled = false
            settings.domStorageEnabled = true
            settings.javaScriptEnabled = true
            settings.loadsImagesAutomatically = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        }
    }

    override fun start() {
    }

    override fun createPrenter(): ContentContract.Presenter = ContentPresenter()

    override fun showCollectSuccess(seccess: Boolean) {
    }

    override fun showCancelCollectSuccess(success: Boolean) {
    }
}