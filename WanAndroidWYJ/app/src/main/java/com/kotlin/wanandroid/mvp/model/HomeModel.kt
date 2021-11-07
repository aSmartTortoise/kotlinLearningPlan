package com.kotlin.wanandroid.mvp.model

import com.kotlin.wanandroid.http.RetrofitHelper
import com.kotlin.wanandroid.mvp.contract.HomeContract
import com.kotlin.wanandroid.mvp.model.bean.Article
import com.kotlin.wanandroid.mvp.model.bean.ArticleResponseBody
import com.kotlin.wanandroid.mvp.model.bean.Banner
import com.kotlin.wanandroid.mvp.model.bean.HttpResult
import io.reactivex.Observable

class HomeModel : CommonModel(), HomeContract.Modle {
    override fun requestBanner(): Observable<HttpResult<List<Banner>>> {
        return RetrofitHelper.service.getBanners()
    }

    override fun requestTopArticles(): Observable<HttpResult<MutableList<Article>>> {
        return RetrofitHelper.service.getTopArticles()
    }

    override fun requestArticles(num: Int): Observable<HttpResult<ArticleResponseBody>> {
        return RetrofitHelper.service.getArticles(num)
    }
}