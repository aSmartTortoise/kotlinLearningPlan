package com.java.kt10;

/**
 * 静态内部类
 */
public class LazyThreadSafeStaticInnerClass {

    private static class Holder {
        private static LazyThreadSafeStaticInnerClass instance =
                new LazyThreadSafeStaticInnerClass();
    }

    private LazyThreadSafeStaticInnerClass() {}

    public static LazyThreadSafeStaticInnerClass getInstance() {
       return Holder.instance;
    }
}
