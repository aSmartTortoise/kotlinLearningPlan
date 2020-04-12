package com.wyj.mvplayter.model

/**
 * kotlin data class
 */
data class HomeResponse(
    val `data`: List<HomeItemEntity>
)

data class HomeItemEntity(
    val description: String,
    val posterPic: String,
    val title: String
)