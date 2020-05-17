package com.java.kt10;

/**
 * 懒加载+线程安全+双重检查机制
 */
public class LazyThreadSafeDoubleCheck {
    private static volatile LazyThreadSafeDoubleCheck instance;
    private LazyThreadSafeDoubleCheck(){}

    public static LazyThreadSafeDoubleCheck getInstance() {
        if (null == instance) {
            synchronized (LazyThreadSafeDoubleCheck.class) {
                if (null == instance) {
                    instance = new LazyThreadSafeDoubleCheck();
                }
            }
        }
        return instance;
    }
}
