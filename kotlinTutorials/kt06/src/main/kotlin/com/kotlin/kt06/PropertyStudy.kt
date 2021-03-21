package com.kotlin.kt06

/**
 * 1 属性声明的完整语法
 *      var <propertyName>[: <propertyType>] [= <property_initializer>]
 *          [<getter>]
 *          [<setter>]
 *      其初始化器initializer、getter和setter都是可选书写的。
 *      如果我们声明属性的时候自定义getter，那么每次访问这个属性的时候都会调用ta的get函数。
 * 2 幕后字段 https://juejin.cn/post/6844903673353486343
 *      如果属性至少一个访问器使用了默认实现，那么Kotlin会自动提供幕后字段，用field表示，
 *      幕后字段只能用于访问器中。幕后字段field就是指当前的属性，在getter和setter这两个
 *      特殊的作用域中有特殊的意义，类似于一个类中的this就代表这个类。
 * 2.1 有幕后字段的属性，转换成Java代码后一定有一个与之对应的成员变量，反之则没有。
 * 3 编译期常量
 *      如果只读属性的值在编译期是已知的，那么可以使用const修饰符将其标记为编译期常量。这种属性需要
 *      满足以下要求：
 *      （1）位于顶层或者是object声明或者companion object的一个成员。
 *      （2）以String或原生类型值初始化。
 *      （3）没有自定义getter函数。
 *      
 *
 */

fun main() {
    val address: Address = Address()
    address.street = "BakerStreet"
    println("this address` street is ${address.street}")

}

class Address {
    var name: String = "Holmes, Sherlock"
    var street: String = ""
        set(value) {
            field = value
        }
    val isEmpty: Boolean
        get() = street.isEmpty()

}