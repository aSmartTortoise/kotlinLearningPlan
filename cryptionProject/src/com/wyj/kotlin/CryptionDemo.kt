package com.wyj.kotlin

import com.wyj.kotlin.utils.HttpUtils

fun main() {
    var urlStr: String = "https://zhuanlan.zhihu.com/api/columns/zhihuadmin"
    var responseStr = HttpUtils.getRequest(urlStr)
    
}