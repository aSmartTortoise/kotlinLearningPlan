package com.java.kt10;

/**
 * 基本懒加载
 */
public class LazyNotThreadSaft {
    private static LazyNotThreadSaft instance;

    private LazyNotThreadSaft() {}

    public static LazyNotThreadSaft getInstance() {
        if (null == instance) {
            instance = new LazyNotThreadSaft();
        }
        return instance;
    }
}
