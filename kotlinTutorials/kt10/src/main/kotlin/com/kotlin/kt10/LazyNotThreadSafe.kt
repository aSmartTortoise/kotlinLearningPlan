package com.kotlin.kt10

/**
 * 基本懒加载
 */
class LazyNotThreadSafe {
    companion object {
        val instace by lazy (LazyThreadSafetyMode.NONE) {
            LazyNotThreadSafe()
        }
        var instance2: LazyNotThreadSafe? = null

        fun getInstance(): LazyNotThreadSafe {
            if (instance2 == null) {
                instance2 = LazyNotThreadSafe()
            }
            return instance2!!
        }
    }
}