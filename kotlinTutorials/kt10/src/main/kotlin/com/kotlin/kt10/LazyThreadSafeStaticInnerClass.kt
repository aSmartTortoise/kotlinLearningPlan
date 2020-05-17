package com.kotlin.kt10

class LazyThreadSafeStaticInnerClass {
    companion object {
        fun getInstance() : LazyThreadSafeStaticInnerClass {
            return Holder.instance
        }
    }

    private object Holder {
        var instance = LazyThreadSafeStaticInnerClass()
    }
}