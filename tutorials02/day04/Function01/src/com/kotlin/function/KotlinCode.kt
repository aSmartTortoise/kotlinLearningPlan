package com.kotlin.function

fun main(args: Array<String>) {
    println(sum(56, 65))//位置参数
    println(sum(opera2 = 65, opera1 = 56))//命名参数
}

fun sum(opera1: Int, opera2: Int, lable: String = "the result is") = "$lable ${opera1 + opera2}"
