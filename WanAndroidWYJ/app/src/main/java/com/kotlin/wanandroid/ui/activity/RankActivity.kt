package com.kotlin.wanandroid.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kotlin.wanandroid.R
import com.kotlin.wanandroid.adapter.RankAdapter
import com.kotlin.wanandroid.base.BaseSwipeBackMVPActivity
import com.kotlin.wanandroid.mvp.contract.RankContract
import com.kotlin.wanandroid.mvp.model.bean.BaseListResponseBody
import com.kotlin.wanandroid.mvp.model.bean.CoinInfoBean
import com.kotlin.wanandroid.mvp.presenter.RankPresenter
import com.kotlin.wanandroid.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_refresh_layout.*
import kotlinx.android.synthetic.main.toolbar.*

class RankActivity : BaseSwipeBackMVPActivity<RankContract.View, RankContract.Presenter>(), RankContract.View {
    private val TAG: String = "RankActivity"
    private var pageSize = 20
    private var mIsRefresh = true
    private val mOnRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        mIsRefresh = true
        mRankAdapter.setEnableLoadMore(false)
        mPresenter?.getRankList(1)
    }
    private val mRankAdapter by lazy {
        RankAdapter()
    }
    private val mRecyclerViewItemDecoration by lazy {
        SpaceItemDecoration(this)
    }
    private val mOnRequestLoadMoreListener = BaseQuickAdapter.RequestLoadMoreListener {
        mIsRefresh = false
        swipeRefreshLayout.isRefreshing = false
        val page = mRankAdapter.data.size / pageSize + 1
        mPresenter?.getRankList(page)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: wyj")
    }

    override fun createPrenter(): RankContract.Presenter = RankPresenter()

    override fun attachLayoutRes() = R.layout.activity_rank

    override fun initView() {
        super.initView()
        mLayoutStatusView = multiple_status_view
        toolbar.run {
            title = getString(R.string.score_list)
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        swipeRefreshLayout.run {
            setOnRefreshListener(mOnRefreshListener)
        }
        recyclerView.run {
            layoutManager = LinearLayoutManager(this@RankActivity)
            adapter = mRankAdapter
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(mRecyclerViewItemDecoration)
        }
        mRankAdapter.run {
            bindToRecyclerView(recyclerView)
            setOnLoadMoreListener(mOnRequestLoadMoreListener, recyclerView)
        }
    }

    override fun initData() {
    }

    override fun start() {
        mLayoutStatusView?.showLoading()
        mPresenter?.getRankList(1)
    }

    override fun showRankList(body: BaseListResponseBody<CoinInfoBean>) {
        body.datas.let {
            mRankAdapter.run {
                if (mIsRefresh) {
                    replaceData(it)
                } else {
                    addData(it)
                }

                pageSize = body.size

                if (body.over) {
                    loadMoreEnd(mIsRefresh)
                } else {
                    loadMoreComplete()
                }
            }
        }

        if (mRankAdapter.data.isEmpty()) {
            mLayoutStatusView?.showEmpty()
        } else {
            mLayoutStatusView?.showContent()
        }
    }

    override fun hideLoading() {
        super.hideLoading()
        swipeRefreshLayout?.isRefreshing = false
        if (mIsRefresh) {
            mRankAdapter.setEnableLoadMore(true)
        }
    }

    override fun showError(errorMsg: String) {
        super.showError(errorMsg)
        mLayoutStatusView?.showError()

        if (mIsRefresh) {
            mRankAdapter.setEnableLoadMore(true)
        } else {
            mRankAdapter.loadMoreFail()
        }
    }
}