package com.wyj.mvplayter.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.wyj.mvplayter.R
import com.wyj.mvplayter.model.HomeItemEntity
import kotlinx.android.synthetic.main.item_home.view.*


class HomeItemView : RelativeLayout {


    constructor(context: Context?):this(context, null)
    constructor(context: Context?, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context?, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    init {
        View.inflate(context, R.layout.item_home, this)
    }

    fun bindData(itemEntity: HomeItemEntity) {
        tv_singer.text = itemEntity.description
        tv_title.text = itemEntity.title
        Glide
            .with(this)
            .load(itemEntity.posterPic)
            .into(iv_bg)
    }

}