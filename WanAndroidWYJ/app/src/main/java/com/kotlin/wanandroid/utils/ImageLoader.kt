package com.kotlin.wanandroid.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.kotlin.wanandroid.R
import com.kotlin.wanandroid.WanAndroidApplication

object ImageLoader {
    // 1.开启无图模式 2.非WiFi环境 不加载图片
    private val isLoadImage = !SettingUtil.getIsNoPhotoMode() || NetWorkUtil.isWifi(WanAndroidApplication.context)

    fun load(context: Context?, url: String?, iv: ImageView?) {
        !isLoadImage ?: return
        iv?.apply {
            Glide.with(context!!).clear(this)
            val options = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.drawable.bg_placeholder)
            Glide.with(context!!)
                .load(url)
                .transition(DrawableTransitionOptions().crossFade())
                .apply(options)
                .into(this)
        }
    }
}