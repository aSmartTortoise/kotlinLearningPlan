package com.java.kt10;

/**
 * 线程同步锁
 */
public class LazyThreadSafeSynchronized {
    private static LazyThreadSafeSynchronized instance;

    private LazyThreadSafeSynchronized() {

    }

    public static synchronized LazyThreadSafeSynchronized getInstance() {
        if (null == instance) {
            instance = new LazyThreadSafeSynchronized();
        }
        return instance;
    }
}
