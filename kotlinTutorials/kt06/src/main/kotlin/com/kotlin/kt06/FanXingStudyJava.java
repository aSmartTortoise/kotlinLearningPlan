package com.kotlin.kt06;

class FanXingStudyJava {
    public static void main(String[]args){
        // Java中 父类型的集合对象不能赋值给子类型集合的应用，父类型集合和子类型集合
        //属于两个不同的类型。
        MyList<CharSequence> charsequences = new MyList<>();
//        MyList<String> strings = charsequences;//编译报错

    }

    private static class MyList<T> {
        void add(T t) {

        }
    }
}

