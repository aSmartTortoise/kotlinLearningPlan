package com.kotlin.wanandroid.ui.frament

import android.content.Intent
import android.text.Layout
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.bingoogolapple.bgabanner.BGABanner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kotlin.wanandroid.R
import com.kotlin.wanandroid.WanAndroidApplication
import com.kotlin.wanandroid.adapter.HomeAdapter
import com.kotlin.wanandroid.base.BaseMVPFragment
import com.kotlin.wanandroid.ext.showSnackMsg
import com.kotlin.wanandroid.ext.showToast
import com.kotlin.wanandroid.mvp.contract.HomeContract
import com.kotlin.wanandroid.mvp.model.bean.Article
import com.kotlin.wanandroid.mvp.model.bean.ArticleResponseBody
import com.kotlin.wanandroid.mvp.model.bean.Banner
import com.kotlin.wanandroid.mvp.presenter.HomePresenter
import com.kotlin.wanandroid.ui.activity.ContentActivity
import com.kotlin.wanandroid.ui.activity.LoginActivity
import com.kotlin.wanandroid.utils.NetWorkUtil
import com.kotlin.wanandroid.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_refresh_layout.*
import kotlinx.android.synthetic.main.item_home_banner.view.*

class HomeFragment : BaseMVPFragment<HomeContract.View, HomeContract.Presenter>(),
    HomeContract.View {
    private var mRefresh: Boolean = true
    private var mDatas: MutableList<Article> = mutableListOf()
    private val mHomeAdapter: HomeAdapter by lazy {
        HomeAdapter(activity, mDatas)
    }
    private val mLinearLayoutManager: LinearLayoutManager by lazy(LazyThreadSafetyMode.NONE) {
        LinearLayoutManager(activity)
    }
    private val mOnRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        mRefresh = true
        mHomeAdapter.setEnableLoadMore(false)
        mPresenter?.requestHomeData()
    }
    private val mRequestLoadMoreListener = BaseQuickAdapter.RequestLoadMoreListener {
        mRefresh = false
        swipeRefreshLayout.isRefreshing = false
        val page = mHomeAdapter.data.size / 20
        mPresenter?.requestArticles(page)
    }
    private val mOnItemClickListener = BaseQuickAdapter.OnItemClickListener { _, _, position ->
        if (mDatas.size != 0) {
            val data = mDatas[position]
            ContentActivity.start(activity, data.id, data.title, data.link)
        }
    }
    private val mOnItemChildClickListener =
        BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
            if (mDatas.size != 0) {
                val data = mDatas[position]
                when (view.id) {
                    R.id.iv_like -> {
                        if (mIsLogin) {
                            if (!NetWorkUtil.isNetworkAvailable(WanAndroidApplication.context)) {
                                showSnackMsg(resources.getString(R.string.no_network))
                                return@OnItemChildClickListener
                            }

                            val collect = data.collect
                            data.collect = !collect
                            mHomeAdapter.setData(position, data)
                            if (collect) {
                                mPresenter?.cancleCollectArticle(data.id)
                            } else {
                                mPresenter?.addCollectArticle(data.id)
                            }
                        } else {
                            Intent(activity, LoginActivity::class.java).run {
                                startActivity(this)
                            }
                            showToast(resources.getString(R.string.login_tint))
                        }
                    }
                }
            }
        }

    private val mItemDecoration by lazy {
        activity?.run {
            SpaceItemDecoration(this)
        }
    }
    private var mBannerView: View? = null
    private lateinit var mBannerDatas: ArrayList<Banner>
    private val mBannerDelegate =
        BGABanner.Delegate<ImageView, String> { banner, imageView, model, position ->
            if (mBannerDatas.size > 0) {
                val data = mBannerDatas[position]
                ContentActivity.start(activity, data.id, data.title, data.url)
            }
        }

    override fun attachLayoutRes() = R.layout.fragment_refresh_layout

    override fun initView(view: View) {
        super.initView(view)
        mLayoutStatusView = multiple_status_view
        swipeRefreshLayout.run {
            setOnRefreshListener(mOnRefreshListener)
        }
        recyclerView.run {
            layoutManager = mLinearLayoutManager
            adapter = mHomeAdapter
            itemAnimator = DefaultItemAnimator()
            mItemDecoration?.let { addItemDecoration(it) }
        }
        mBannerView = layoutInflater.inflate(R.layout.item_home_banner, null)
        mBannerView?.banner?.run {
            setDelegate(mBannerDelegate)
        }
        mHomeAdapter.run {
            bindToRecyclerView(recyclerView)
            setOnLoadMoreListener(mRequestLoadMoreListener, recyclerView)
            onItemClickListener = mOnItemClickListener
            onItemChildClickListener = mOnItemChildClickListener
        }


    }

    override fun lazyLoad() {
        TODO("Not yet implemented")
    }

    override fun creatPresenter(): HomeContract.Presenter = HomePresenter()

    override fun scrollToTop() {
        TODO("Not yet implemented")
    }

    override fun setBanner(banners: List<Banner>) {
        TODO("Not yet implemented")
    }

    override fun setArticles(articles: ArticleResponseBody) {
        TODO("Not yet implemented")
    }

    override fun showCollectSuccess(seccess: Boolean) {
        TODO("Not yet implemented")
    }

    override fun showCancelCollectSuccess(success: Boolean) {
        TODO("Not yet implemented")
    }
}