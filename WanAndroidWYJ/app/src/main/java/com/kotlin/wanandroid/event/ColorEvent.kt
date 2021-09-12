package com.kotlin.wanandroid.event

import com.kotlin.wanandroid.utils.SettingUtil


/**
 * Created by chenxz on 2018/6/18.
 */
class ColorEvent(var isRefresh: Boolean, var color: Int = SettingUtil.getColor())