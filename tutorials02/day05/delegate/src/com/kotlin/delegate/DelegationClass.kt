package com.kotlin.delegate

import kotlin.math.exp
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * 委托属性：一个类中的属性的值不是在类内部直接定义的，而是委托一个类，由该类统一管理
 */
class Example {
    var p: String by MyDelegate()
    /**
     * 延迟属性 lazy()方法是接收一个lambda表达式作为参数，返回Lazy<T>实例的函数，返回的实例作为延迟
     * 属性的委托。第一次调用get会执行传递给lazy（）的lambda表达式，并记录结果，后续调用get()只
     * 会返回记录的结果。
     */
    val lazyValue: Boolean by lazy {
        println("computed")
        false
    }

    var obserableValue: String by Delegates.observable("not value") {
        prop, old, new ->
        println("旧值,$old, 新值$new")
    }

    /**
     * 把属性存储在映射中
     */
    val map: Map<String, Any> = mapOf("name" to "kotlin教程", "url" to "https://www.kotlincn.net")
    val name: String by map
    val url: String by map

    /**
     *  NOT NULL 使用于属性在声明阶段，初始化器中无法确定其值
     */

    var notNullValue: String by Delegates.notNull<String>()

//    fun functionExample(computeFoo: () -> Foo) {
//        val memoizedFoo by lazy(computeFoo)
//        if (something && memoizedFoo.isValid) {
//            memoizedFoo.doSomething()
//        }
//    }
}

class MyDelegate {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef,这里委托了${property.name}属性"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
       println("$thisRef 的${property.name}属性的赋值为$value")
    }

}

fun main(args: Array<String>) {
    val example: Example = Example()
    /**
     * 自定义委托
     */
    println(example.p)

    example.p = "wyjDelegation属性"
    println(example.p)

    /**
     * 延迟属性
     */
    println(example.lazyValue)
    println(example.lazyValue)

    /**
     * 可观察属性
     */

    example.obserableValue = "wang jie"

    /**
     * 把属性存储在映射中
     */

    println(example.name)
    println(example.url)

    /**
     * NOT NULL
     */

    example.notNullValue = "gave me a hug"
    println(example.notNullValue)

}