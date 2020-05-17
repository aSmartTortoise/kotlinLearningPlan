package com.java.kt10;

/**
 * 懒汉式
 */
public class PlainOldSingleton {
    private static PlainOldSingleton instance = new PlainOldSingleton();

    private PlainOldSingleton() {
    }

    public static PlainOldSingleton getInstance() {
        return instance;
    }
}
