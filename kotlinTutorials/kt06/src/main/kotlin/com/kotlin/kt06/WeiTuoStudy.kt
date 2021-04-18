package com.kotlin.kt06

import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 *  15 委托
 *      https://www.runoob.com/kotlin/kotlin-delegated.html
 *      委托模式是软件设计中的一项基本技巧。在委托模式中有两个对象处理同一个请求，接收请求的对象将
 *      请求委托给另一个对象处理。Kotlin中使用by标识符来实现委托。
 *      委托分为类委托、属性委托。
 *      15.1 类委托
 *          类的委托即委托的类中的方法实际上是由受委托的类的对象的方法实现的。
 *          如下方例子中的by子句表示将b保存在Derived的对象内部，编译器会生成接口Base的所有
 *          方法，并将调用转发给b。
 *          如果Derived中重写对应的函数，则实际上这个函数的执行不会转发给b。
 *      15.2 属性委托
 *          概念：类中的某个属性的值不是在类中定义的，而是委托给一个代理类，从而实现对该类的指定属性
 *          的统一管理。
 *          语法：val/var <属性名>: <类型> by <表达式>
 *          by 标识符后的表达式就是委托。该属性的get()和set()函数将委托给指定类的对象的getValue()
 *          和setValue()函数，
 *      Kotln标准库中为几种有用的委托提供了工厂方法。
 *      15.3 延迟属性Lazy
 *          lazy()函数，接收一个lambda表达式作为参数，返回一个Lazy类的实例。该实例作为延迟属性
 *          的委托，第一次调用get（）函数，会执行lambda表达式，并记录结果，后续调用get只是返回
 *          记录的结果。延迟属性时只读的。
 *          默认情况下对lazy属性的求值是线程安全的：该值只会在一个线程中计算，并且所有线程会看到
 *          相同的值。如果初始化延迟属性的委托的同步不是必须的，这样多个线程可以同时执行，那么将
 *          LazyThreadSafetyMode.PUBLICATION作为参数传递给lazy()函数。如果确定初始化工作
 *          和属性的使用总是位于同一个线程，那么可以使用LazyThreadSafetyMode.None模式，它不会有
 *          任何线程安全的保证和任何开销。
 *      15.4 可观察属性Observable
 *          observable可以用于实现观察者模式。
 *          Delegates.observable()函数接受两个参数：第一个是初始化值、第二个是属性值变化事件的响应
 *          器handler。把属性赋值后会执行该响应器，该响应器有三个参数：被赋值的属性、旧值、新值。
 *      15.5 将属性值存储在映射中
 *          一个常见的用例是将属性的值存储在映射中。这经常出现在像解析json或做其他动态事件的应用中。
 *          在这种情况下可以使用map实例来作为委托实现委托属性。
 *          委托属性的值会从这个map中取值。
 *      15.6 局部属性委托
 *      15.7 NOT NULL
 *          not null使用于那些一个类的对象在初始化阶段尚不能确定某个属性的值的场合。如果该属性在初始化前
 *          就去访问该属性就会抛出异常。
 *
 *
 *
 */

interface Base {
    fun print()
}

class BaseImpl(val x: Int) : Base {
    override fun print() {
        print(x)
    }
}

/**
 *  Derived类的方法委托传入的Base类的实例b处理，执行b的相应方法。
 */
class Derived(b: Base) : Base by b {
//    override fun print() {
//        print("Derived")
//    }
}

fun main(args: Array<String>) {
    val b = BaseImpl(10)
    Derived(b).print()

    val example = Example()
    println(example.p)
    example.p = "runKotlin"
    println(example.p)
    println(example.lazyValue)
    println(example.observableValue)
    example.observableValue = "not only a tutorial"
    println(example.observableValue)
    println("-------------------")
    val map = mutableMapOf<String, String>("name" to "菜鸟kotlin",
        "url" to "runoob.com")
    val site: Site = Site(map)
    println(site.name)
    println(site.url)
    println("----------------------")
    map.put("name", "google")
    map.put("url", "www.google.com")
    println(site.name)
    println(site.url)
    println("局部属性声明为委托")
    example.printP()
    println("not null delegate--------------")
//    example.notNullValue = "not empty now"
    println(example.notNullValue)

}

class Example {
    var p: String by Delegate()
    val lazyValue: String by lazy {
        println("computed")
        "Hello"
    }
    var observableValue: String by Delegates.observable("初始值") { property, oldValue, newValue ->
        run {
            println("修改的属性$property，旧值为$oldValue， 新值为$newValue")
        }
    }

    var notNullValue: String by Delegates.notNull<String>()

    fun printP() {
        val count: Int by lazy {
            println("add 0")
            1
        }
        println("p:$p and count:$count")
    }
}

class Delegate {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef 这里委托了${property.name}属性"
    }


    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$thisRef 的${property.name}的属性赋值为$value")
    }

}

class Site(val map: MutableMap<String, String>) {
    var name: String by map
    var url: String by map
}


