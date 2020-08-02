package com.kotlin.objectexpression

open class A(x: Int) {
    public open val y: Int = x

}

interface B {}

val ab: A = object : A(1), B {
    override val y: Int = 15
}

fun main(args: Array<String>) {
    /**
     * 对象表达式，越过类的定义，创建一个对象
     */
    val adHoc = object {
        val x: Int = 10
        val y: Int = 10
    }
    println(adHoc.x)
    println(adHoc.y)
}