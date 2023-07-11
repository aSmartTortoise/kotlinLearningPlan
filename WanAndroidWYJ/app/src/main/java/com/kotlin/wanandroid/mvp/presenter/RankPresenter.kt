package com.kotlin.wanandroid.mvp.presenter

import com.kotlin.wanandroid.base.BasePresenter
import com.kotlin.wanandroid.ext.ss
import com.kotlin.wanandroid.mvp.contract.RankContract
import com.kotlin.wanandroid.mvp.model.RankModel

class RankPresenter : BasePresenter<RankContract.Model, RankContract.View>(), RankContract.Presenter {
    override fun createModel(): RankContract.Model? = RankModel()

    override fun getRankList(page: Int) {
        model?.getRankList(page)?.ss(model, view) {
            view?.showRankList(it.data)
        }
    }
}