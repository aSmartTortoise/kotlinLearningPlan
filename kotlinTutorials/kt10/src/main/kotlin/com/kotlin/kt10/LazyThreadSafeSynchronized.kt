package com.kotlin.kt10

/**
 * 线程同步锁
 */
class LazyThreadSafeSynchronized private constructor(){
    companion object {
        var instance: LazyThreadSafeSynchronized? = null

        @Synchronized
        fun getInstance2() : LazyThreadSafeSynchronized {
            if (null == instance) {
                instance = LazyThreadSafeSynchronized()
            }
            return instance!!
        }
    }
}