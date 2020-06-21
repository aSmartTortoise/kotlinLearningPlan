package com.kotlin.buildInDataType

fun main() {
    val d = 10.03
    val f = 10.36f
//    val c: Char = 65//直接将Int类型的数据赋值给Char类型的变量是不允许的
    val c: Char = 65.toChar()
    println("c is $c")
}