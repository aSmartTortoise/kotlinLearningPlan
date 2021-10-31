package com.kotlin.wanandroid.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.appbar.AppBarLayout
import com.kotlin.wanandroid.R
import com.kotlin.wanandroid.WanAndroidApplication
import com.kotlin.wanandroid.adapter.ScoreAdapter
import com.kotlin.wanandroid.base.BaseSwipeBackMVPActivity
import com.kotlin.wanandroid.mvp.contract.ScoreContract
import com.kotlin.wanandroid.mvp.model.bean.BaseListResponseBody
import com.kotlin.wanandroid.mvp.model.bean.UserScoreBean
import com.kotlin.wanandroid.mvp.presenter.ScorePresenter
import com.kotlin.wanandroid.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.activity_score.*
import kotlinx.android.synthetic.main.fragment_refresh_layout.multiple_status_view

class ScoreActivity : BaseSwipeBackMVPActivity<ScoreContract.View, ScoreContract.Presenter>(), ScoreContract.View {
    private var mIsRefresh = true
    private var mPageSize = 20
    private val mOnRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        mIsRefresh = true
        mScoreAdapter.setEnableLoadMore(false)
        mPresenter?.getUserScoreList(1)
    }
    private val mOnRequestLoadMoreListener = BaseQuickAdapter.RequestLoadMoreListener {
        mIsRefresh = false
        swipeRefreshLayout.isRefreshing = false
        val page = mScoreAdapter.data.size / mPageSize + 1
        mPresenter?.getUserScoreList(page)
    }

    private val mScoreAdapter by lazy {
        ScoreAdapter()
    }
    private val mRecyclerViewItemDecoration by lazy {
        SpaceItemDecoration(this)
    }
    private var mContentHight = 0F
    override fun createPrenter(): ScoreContract.Presenter = ScorePresenter()

    override fun attachLayoutRes(): Int = R.layout.activity_score

    override fun initData() {
    }

    override fun initView() {
        super.initView()
        mLayoutStatusView = multiple_status_view
        toolbar.run {
            title = getString(R.string.score_detail)
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        WanAndroidApplication.userInfo?.let {
            tv_score.text = it.coinCount.toString()
        }

        swipeRefreshLayout.run {
            setOnRefreshListener(mOnRefreshListener)
        }

        recyclerView.run {
            layoutManager = LinearLayoutManager(this@ScoreActivity)
            adapter = mScoreAdapter
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(mRecyclerViewItemDecoration)
        }

        mScoreAdapter.run {
            bindToRecyclerView(recyclerView)
            setOnLoadMoreListener(mOnRequestLoadMoreListener, recyclerView)

        }
        app_bar_layout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener{
            appBarLayout, verticalOffset ->
            mContentHight = rl_content.height.toFloat()
            val alpha = 1 - (-verticalOffset) / (mContentHight)
            rl_content.alpha = alpha
        })
    }

    override fun start() {
        mLayoutStatusView?.showLoading()
        mPresenter?.getUserScoreList(1)
    }

    override fun hideLoading() {
        super.hideLoading()
        swipeRefreshLayout?.isRefreshing = false
        if (mIsRefresh) {
            mScoreAdapter.setEnableLoadMore(true)
        }
    }

    override fun showError(errorMsg: String) {
        super.showError(errorMsg)
        mLayoutStatusView?.showError()
        if (mIsRefresh) {
            mScoreAdapter.setEnableLoadMore(true)
        } else {
            mScoreAdapter.loadMoreFail()
        }
    }

    override fun showUserScoreList(body: BaseListResponseBody<UserScoreBean>) {
        body.datas.let {
            mScoreAdapter.run {
                if (mIsRefresh) {
                    replaceData(it)
                } else {
                    addData(it)
                }

                mPageSize = body.size

                if (body.over) {
                    loadMoreEnd(mIsRefresh)
                } else {
                    loadMoreComplete()
                }
            }
        }

        if (mScoreAdapter.data.isEmpty()) {
            mLayoutStatusView?.showEmpty()
        } else {
            mLayoutStatusView?.showContent()
        }
    }

    override fun initColor() {
        super.initColor()
        rl_content.setBackgroundColor(mThemeColor)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.menu_score, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_help -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
}