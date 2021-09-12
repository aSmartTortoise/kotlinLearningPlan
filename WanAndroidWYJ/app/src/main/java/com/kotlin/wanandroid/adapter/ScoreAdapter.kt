package com.kotlin.wanandroid.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotlin.wanandroid.R
import com.kotlin.wanandroid.mvp.model.bean.UserScoreBean

class ScoreAdapter : BaseQuickAdapter<UserScoreBean, BaseViewHolder>(R.layout.item_socre_list) {
    override fun convert(helper: BaseViewHolder?, item: UserScoreBean?) {
        helper ?: return
        item ?: return
        helper.setText(R.id.tv_reason, item.reason)
            .setText(R.id.tv_desc, item.desc)
            .setText(R.id.tv_score, "+${item.coinCount}")
    }
}