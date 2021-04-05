package com.kotlin.kt06

/**
 *  14 内联类
 *      有时候业务逻辑需要围绕某种类型创建装饰器，然而由于额外的堆内存分配问题，它会引入运行时的性能
 *      开销。此外如果被包装的类型是原生类型，性能的损失是很糟糕的，因为原生类型通常在运行时就进行了
 *      大量的性能优化，然而它们的装饰器却没有得到任何的处理。
 *      为了解决这类问题，Kotlin引入了内联类，内联类在声明的地方加上inline修饰符。
 *      内联类必须有一个属性，该属性在主构造函数进行初始化，且主构造函数只能有这一个参数。在运行时
 *      将使用这个属性来表示内联类的实例。
 *      内联类不能含有init代码块；内联类不能有幕后字段。
 *      内联类不能继承或被继承，内联类是final的；内联类可以继承接口。
 *      内联类在运行时表示为基础类型。
 *
 */

inline class Name(val str: String) {
    val length: Int
        get() = str.length
    fun greet() {
        println("Hello, $str")
    }
}

fun main(args: Array<String>) {
    val name = Name("Kotlin")
    name.greet()
    println(name.length)
}