package com.kotlin.wanandroid.ui.frament

import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kotlin.wanandroid.R
import com.kotlin.wanandroid.base.BaseMVPFragment
import com.kotlin.wanandroid.mvp.contract.HomeContract
import com.kotlin.wanandroid.mvp.model.bean.Article
import com.kotlin.wanandroid.mvp.model.bean.ArticleResponseBody
import com.kotlin.wanandroid.mvp.model.bean.Banner
import com.kotlin.wanandroid.mvp.presenter.HomePresenter
import kotlinx.android.synthetic.main.fragment_refresh_layout.*

class HomeFragment : BaseMVPFragment<HomeContract.View, HomeContract.Presenter>(), HomeContract.View {
    private var mRefresh: Boolean = true
    private var mDatas: MutableList<Article> = mutableListOf()
    private val mOnRefreshListener: SwipeRefreshLayout.OnRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        mRefresh = true

    }
    override fun attachLayoutRes() = R.layout.fragment_refresh_layout

    override fun initView(view: View) {
        super.initView(view)
        mLayoutStatusView = multiple_status_view
        swipeRefreshLayout.run {
            setOnRefreshListener(mOnRefreshListener)
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