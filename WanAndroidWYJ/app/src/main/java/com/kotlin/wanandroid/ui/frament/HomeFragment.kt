package com.kotlin.wanandroid.ui.frament

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Debug
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.kotlin.wanandroid.utils.ImageLoader
import com.kotlin.wanandroid.utils.NetWorkUtil
import com.kotlin.wanandroid.widget.SpaceItemDecoration
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_refresh_layout.*
import kotlinx.android.synthetic.main.item_home_banner.view.*

class HomeFragment : BaseMVPFragment<HomeContract.View, HomeContract.Presenter>(),
    HomeContract.View {

    companion object {
        fun getInstance(): HomeFragment = HomeFragment()
    }

    private var mRefresh: Boolean = true
    private var mDatas: MutableList<Article> = mutableListOf()
    private val mHomeAdapter: HomeAdapter by lazy {
        HomeAdapter(activity, mDatas)
    }
    private val mBannerAdatpter: BGABanner.Adapter<ImageView, String> by lazy {
        BGABanner.Adapter<ImageView, String> { bgaBanner, imageView, feedImageUrl, position ->
            ImageLoader.load(activity, feedImageUrl, imageView)
        }
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
            Debug.startMethodTracing("content_activity_start")
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initView(view: View) {
        super.initView(view)
        Thread.sleep(1000L)
        mLayoutStatusView = multiple_status_view
        swipeRefreshLayout.run {
            setOnRefreshListener(mOnRefreshListener)
        }
        recyclerView.run {
            layoutManager = mLinearLayoutManager
            adapter = mHomeAdapter
            itemAnimator = DefaultItemAnimator()
            //如果lambda表达式中只使用一个函数，且该函数中的参数，全部是lambda表达式的参数，且顺序相同
            //那么就可以使用标识符::来使用函数引用
            mItemDecoration?.let(::addItemDecoration)
//            mItemDecoration?.let { addItemDecoration(it) }
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
            addHeaderView(mBannerView)
        }
    }

    override fun lazyLoad() {
        mLayoutStatusView?.showLoading()
        mPresenter?.requestHomeData()
    }

    override fun creatPresenter(): HomeContract.Presenter = HomePresenter()

    override fun scrollToTop() {
    }

    @SuppressLint("CheckResult")
    override fun setBanner(banners: List<Banner>) {
        mBannerDatas = banners as ArrayList<Banner>
        val bannerFeedList = ArrayList<String>()
        val bannerTitleList = ArrayList<String>()
        Observable.fromIterable(banners).subscribe { banner ->
            bannerFeedList.add(banner.imagePath)
            bannerTitleList.add(banner.title)

        }
        mBannerView?.banner?.run {
            setAutoPlayAble(bannerFeedList.size > 1)
            setData(bannerFeedList, bannerTitleList)
            setAdapter(mBannerAdatpter)
        }
    }

    override fun setArticles(articles: ArticleResponseBody) {
        articles.datas.let {
            mHomeAdapter.run {
                if (mRefresh) {
                    replaceData(it)
                } else {
                    addData(it)
                }
                if (it.size < articles.size) {
                    loadMoreEnd(mRefresh)
                } else {
                    loadMoreComplete()
                }
            }
        }

        if (mHomeAdapter.data.isEmpty()) {
            mLayoutStatusView?.showEmpty()
        } else {
            mLayoutStatusView?.showContent()
        }
    }

    override fun showCollectSuccess(success: Boolean) {
        Log.d(TAG, "showCollectSuccess: wyj sucess:$success")
    }

    override fun showCancelCollectSuccess(success: Boolean) {
        Log.d(TAG, "showCancelCollectSuccess: wyj sucess:$success")
    }
}