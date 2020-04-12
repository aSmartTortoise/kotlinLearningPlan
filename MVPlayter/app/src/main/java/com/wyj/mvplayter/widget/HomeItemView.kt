package com.wyj.mvplayter.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.wyj.mvplayter.R


class HomeItemView : RelativeLayout {
    constructor(context: Context?):this(context, null)
    constructor(context: Context?, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context?, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    init {
        View.inflate(context, R.layout.item_home, this)
    }

}