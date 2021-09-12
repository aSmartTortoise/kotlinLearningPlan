package com.kotlin.wanandroid.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotlin.wanandroid.R
import com.kotlin.wanandroid.mvp.model.bean.CoinInfoBean

class RankAdapter : BaseQuickAdapter<CoinInfoBean, BaseViewHolder>(R.layout.item_rank_list) {
    override fun convert(helper: BaseViewHolder?, item: CoinInfoBean?) {
        helper ?: return
        item ?: return
        val index = helper.layoutPosition

        helper.setText(R.id.tv_username, item.username)
            .setText(R.id.tv_score, item.coinCount.toString())
            .setText(R.id.tv_ranking, (index + 1).toString())
    }

}