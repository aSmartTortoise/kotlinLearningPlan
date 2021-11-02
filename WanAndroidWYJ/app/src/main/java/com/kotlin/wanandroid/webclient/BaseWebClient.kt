package com.kotlin.wanandroid.webclient

import android.net.Uri
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi

open class BaseWebClient : WebViewClient() {
    protected val TAB = "BaseWebClient"
    private val blockHostList = arrayListOf<String>(
        "www.taobao.com",
        "www.jd.com",
        "yun.tuisnake.com",
        "yun.lvehaisen.com",
        "yun.tuitiger.com"
    )

    private fun isBlackHost(host: String): Boolean {
        for (blackHost in blockHostList) {
            if (blackHost == host) {
                return true
            }
        }

        return false
    }

    private fun shouldIntercept(uri: Uri?): Boolean {
        if (uri != null) {
            return isBlackHost(uri.host ?: "")
        }

        return false
    }

    private fun shouldOverrideUrlLoading(uri: Uri?): Boolean {
        if (uri != null) {
            return isBlackHost(uri.host ?: "")
        }

        return false
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        if (shouldIntercept(request?.url)) {
            return WebResourceResponse(null, null, null)
        }

        return super.shouldInterceptRequest(view, request)
    }
}