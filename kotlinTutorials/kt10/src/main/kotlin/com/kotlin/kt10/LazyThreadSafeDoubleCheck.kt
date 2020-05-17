package com.kotlin.kt10

class LazyThreadSafeDoubleCheck private constructor(){

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            LazyThreadSafeDoubleCheck()
        }
        
        @Volatile
        private var instance2 : LazyThreadSafeDoubleCheck ? = null

        fun getInstance2() : LazyThreadSafeDoubleCheck {
            if (null == instance2) {
                synchronized(this) {
                    if (null == instance2) {
                        instance2 = LazyThreadSafeDoubleCheck()
                    }
                }
            }
            return instance2!!
        }
    }
}