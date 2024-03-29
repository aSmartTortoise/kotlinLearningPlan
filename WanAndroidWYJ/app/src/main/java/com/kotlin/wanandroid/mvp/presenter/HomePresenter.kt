package com.kotlin.wanandroid.mvp.presenter

import com.kotlin.wanandroid.ext.ss
import com.kotlin.wanandroid.mvp.contract.HomeContract
import com.kotlin.wanandroid.mvp.model.HomeModel
import com.kotlin.wanandroid.mvp.model.bean.Article
import com.kotlin.wanandroid.mvp.model.bean.ArticleResponseBody
import com.kotlin.wanandroid.mvp.model.bean.HttpResult
import com.kotlin.wanandroid.utils.SettingUtil
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class HomePresenter : CommonPreseter<HomeContract.Modle, HomeContract.View>(), HomeContract.Presenter {
    override fun createModel(): HomeContract.Modle = HomeModel()

    override fun requestBanner() {
        model?.requestBanner()?.ss(model, view, false) {
            view?.setBanner(it.data)
        }
    }

    override fun requestHomeData() {
        requestBanner()
        val observable = if (SettingUtil.getIsShowTopArticle()) {
            model?.requestArticles(0)
        } else {
            Observable.zip(model?.requestTopArticles(), model?.requestArticles(0),
                BiFunction<HttpResult<MutableList<Article>>, HttpResult<ArticleResponseBody>,
                        HttpResult<ArticleResponseBody>> { t1, t2 ->
                    t1.data.forEach {
                        it.top = "1"
                    }
                    t2.data.datas.addAll(0, t1.data)
                    t2
                }
            )
        }
        observable?.ss(model, view, false) {
            view?.setArticles(it.data)
        }
    }

    override fun requestArticles(num: Int) {
        model?.requestArticles(num)?.ss(model, view, false) {
            view?.setArticles(it.data)
        }
    }
}