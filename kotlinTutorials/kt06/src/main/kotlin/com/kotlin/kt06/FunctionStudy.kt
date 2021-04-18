package com.kotlin.kt06

/**
 *  16 函数
 *      16.1 返回Unit的函数
 *          如果一个函数不返回任何有用的值，它的返回类型是Unit。
 *      16.2 单表达式函数
 *          当函数的返回值为非Unit时，且函数体中只有一行代码，则可以省略花括号并且在=号之后直接
 *          书写代码接口。
 *          当返回值类型可以由编译推断出类型时，可以省略返回值类型的显示声明。
 *          
 *
 **/

class FunctionExample {
    fun printHello(name: String?): Unit {
        if (name != null) println("Hello, $name")
        else println("Hi, there!")
    }
}