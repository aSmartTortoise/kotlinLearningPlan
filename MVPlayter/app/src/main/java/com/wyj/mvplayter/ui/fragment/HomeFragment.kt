package com.wyj.mvplayter.ui.fragment


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.wyj.mvplayter.R
import com.wyj.mvplayter.base.BaseFragment
import com.wyj.mvplayter.model.HomeItemEntity
import com.wyj.mvplayter.model.HomeResponse
import com.wyj.mvplayter.ui.adapter.HomeAdapter
import com.wyj.mvplayter.utils.ThreadUtils
import com.wyj.mvplayter.utils.URLProviderUtils
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.*
import org.jetbrains.anko.textColor
import java.io.IOException
import kotlin.math.log

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : BaseFragment() {
    val homeAdapter:HomeAdapter by lazy {
        HomeAdapter()
    }

    override fun getContentView(): View {
        var view = View.inflate(context, R.layout.fragment_home, null)
        return view
    }

    override fun initView() {
        super.initView()
        rcv.layoutManager = LinearLayoutManager(context)
        rcv.overScrollMode = View.OVER_SCROLL_NEVER
        rcv.adapter = homeAdapter
        refresh_layout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        refresh_layout.setOnRefreshListener {
            loadData()
        }
    }

    override fun initData() {
        super.initData()
        refresh_layout.isRefreshing = true
        loadData()
    }

    private fun loadData() {
        val url = URLProviderUtils.getHomeUrl(0, 10)
        val request: Request = Request
            .Builder()
            .url(url)
            .get()
            .build()
        val okHttpClient = OkHttpClient()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                ThreadUtils.runOnUIThread(Runnable {
                    refresh_layout.isRefreshing = false
                })
                showToast("获取网络数据出错")
            }

            override fun onResponse(call: Call, response: Response) {
                ThreadUtils.runOnUIThread(Runnable {
                    refresh_layout.isRefreshing = false
                })
                val result = response.body?.string()
                Log.d("wyj1", "onResponse " + result)
                result?.let {
                    val homeResponse = Gson().fromJson<HomeResponse>(
                        result,
                        object : TypeToken<HomeResponse>() {}.type
                    )
                    val homeItemEntityList = homeResponse?.data
                    homeItemEntityList?.let { list ->
                        ThreadUtils.runOnUIThread(Runnable { homeAdapter.reloadData(list) })
                    }
                }
            }

        })
    }


}
