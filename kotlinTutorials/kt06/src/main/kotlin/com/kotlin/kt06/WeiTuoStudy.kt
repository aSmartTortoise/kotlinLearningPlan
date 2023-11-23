package com.kotlin.kt06

import kotlin.properties.Delegates
import kotlin.reflect.KProperty


interface Base {
    fun print()
}

class BaseImpl(val x: Int) : Base {
    override fun print() {
        print(x)
    }
}

/**
 *  Derived类的方法委托給传入的Base类的实例b处理，执行b的相应方法。
 */
class Derived(b: Base) : Base by b {
//    override fun print() {
//        print("Derived")
//    }
}

fun main(args: Array<String>) {
    println("---类委托---")
    val b = BaseImpl(10)
    Derived(b).print()
    println("---属性委托---")
    val example = Example()
    println(example.p)
    example.p = "runKotlin"
    println(example.p)
    println("---延迟属性---")
    println(example.lazyValue)
    println("---可观察属性---")
    println(example.observableValue)
    example.observableValue = "not only a tutorial"
    println(example.observableValue)
    println("---votable property---")
    example.votableValue = "kotlin votable property."
    println(example.votableValue)
    println("------将属性值存储在映射中--------")
    val map = mutableMapOf<String, String>("name" to "菜鸟kotlin",
        "url" to "runoob.com")
    val site: Site = Site(map)
    println(site.name)
    println(site.url)
    println("-------------")
    map.put("name", "google")
    map.put("url", "www.google.com")
    println(site.name)
    println(site.url)
    println("---notNull---")
    //    example.notNullValue = "not empty now"
    println(example.notNullValue)
    println("局部属性声明为委托")
    example.printP()
}

class Example {
    var p: String by Delegate()
    val lazyValue: String by lazy {
        println("computed")
        "Hello"
    }
    var observableValue: String by Delegates.observable("初始值") { property, oldValue, newValue ->
        println("修改的属性$property，旧值为$oldValue， 新值为$newValue")
    }
    var votableValue: String by Delegates.vetoable("初始值") { property, oldValue, newValue ->
        println("修改的属性${property.name}，旧值为$oldValue，新值为$newValue")
        false
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


