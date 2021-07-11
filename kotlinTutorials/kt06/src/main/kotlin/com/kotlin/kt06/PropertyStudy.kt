package com.kotlin.kt06

/**
 * 1 属性声明的完整语法
 *      var <propertyName>[: <propertyType>] [= <property_initializer>]
 *          [<getter>]
 *          [<setter>]
 *      其初始化器initializer、getter和setter都是可选书写的。
 *      如果我们声明属性的时候自定义getter，那么每次访问这个属性的时候都会调用它的get函数。
 * 2 幕后字段 https://juejin.cn/post/6844903673353486343
 *      如果属性至少一个访问器使用了默认实现，那么Kotlin会自动提供幕后字段，用关键字field表示，
 *      幕后字段只能用于自定义的getter、setter访问器中。幕后字段field就是指当前的属性，在getter和setter这两个
 *      特殊的作用域中有特殊的意义，类似于一个类中的this就代表这个类。
 * 2.1 有幕后字段的属性，转换成Java代码后一定有一个与之对应的成员变量，反之则没有。
 * 3 编译期常量
 *      如果只读属性的值在编译期是已知的，那么可以使用const修饰符将其标记为编译期常量。这种属性需要
 *      满足以下要求：
 *      （1）位于顶层或者是object声明或者companion object的一个成员。
 *      （2）以String或原生类型值初始化。
 *      （3）没有自定义getter函数。
 * 4 延迟初始化属性与变量
 *      一般地，属性声明为非空类型需要在构造函数中进行初始化。然而这样经常不方便，比如属性可以通过依赖
 *      注入或在单元测试的setup方法中进行初始化。这种情况下我们不能再构造函数中为该属性提供非空的初始化
 *      器，又希望在类体中使用该属性时避免空检测。
 *      我们可以用lateInit修饰符标记该属性。
 *      4.1 用lateInit修饰符修饰的属性的类型不能是原始数据类型，且不能有自定义的访问器。
 *      4.2 在类体使用该属性的时候使用.isInitialized来检测该属性是否已经初始化。
 *  5 接口
 *      接口中的函数或属性可以是抽象的也可以是具体的（有函数体），接口中的属性没有幕后字段。
 *  6 函数式接口
 *      只有一个抽象函数的接口称为函数式接口或SAM接口（单个抽象函数），函数式接口中可以有多个非
 *      抽象成员。
 *
 *
 *
 *      
 *
 */

fun main() {
    val address: Address = Address()
    address.street = "BakerStreet"
    println("this address` street is ${address.street}")
    val isEvent = object : IPredicate {
            override fun accept(i: Int): Boolean {
                return i % 2 == 0
            }
    }

}

class Address {
    var name: String = "Holmes, Sherlock"
    var street: String = ""
        get() = field
        set(value) {
            field = value
        }
    val isEmpty: Boolean
        get() = street.isEmpty()
}

class PersonClass(var name: String) {
    /**
     * 可变属性age的访问器getter、setter的默认实现，是有幕后字段的。
     */
    var age: Int = 0
        get() = field
        set(value) {
            field = value
        }
    lateinit var homeAddress: Address

    fun initHomeAddress() {
        homeAddress = Address()
    }
}

interface Named {
    val name: String
}

interface IPerson : Named {
    val firstName: String
    val lastName: String
    override val name: String
        get() = "$firstName $lastName"
}

interface IPredicate {
    fun accept(i: Int): Boolean
}

