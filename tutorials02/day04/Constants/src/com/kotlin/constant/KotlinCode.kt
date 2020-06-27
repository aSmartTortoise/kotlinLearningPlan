package com.kotlin.constant

import com.kotlin.classes.Person

/**
 *1.常量可以定义在top-lever的位置，使用val修饰符
 * 2.编译器常量(compiler constant)，用const修饰。可以用在注解中
 *
 */
val GREETING_CONSTANT = "hello world!"
const val SYSTERM_DEPRECATED = "this systerm is deprecated"
fun main(args: Array<String>) {
    println(GREETING_CONSTANT)
    val person = Person("Jonny", 30, "male", true)
}