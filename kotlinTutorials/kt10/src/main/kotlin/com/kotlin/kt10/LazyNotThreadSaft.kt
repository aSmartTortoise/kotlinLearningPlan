package com.kotlin.kt10

/**
 * 基本懒加载
 */
class LazyNotThreadSaft {
    companion object {
        val instace by lazy (LazyThreadSafetyMode.NONE) {
            LazyNotThreadSaft()
        }
        var instance2: LazyNotThreadSaft? = null

        fun getInstance(): LazyNotThreadSaft {
            if (instance2 == null) {
                instance2 = LazyNotThreadSaft()
            }
            return instance2!!
        }
    }
}