package com.kotlin.wanandroid.mvp.presenter

import com.kotlin.wanandroid.base.BasePresenter
import com.kotlin.wanandroid.ext.ss
import com.kotlin.wanandroid.mvp.contract.ScoreContract
import com.kotlin.wanandroid.mvp.model.ScoreModel

class ScorePresenter : BasePresenter<ScoreContract.Model, ScoreContract.View>(), ScoreContract.Presenter {
    override fun createModel(): ScoreContract.Model? = ScoreModel()

    override fun getUserScoreList(page: Int) {
        mModel?.getUserScoreList(page)?.ss(mModel, mView) {
            mView?.showUserScoreList(it.data)
        }
    }
}