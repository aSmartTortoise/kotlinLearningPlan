package com.kotlin.nullsafety

fun main() {
    val something = arrayOf(1, 2, 3, 4)
    val str =  something as? String// safe cast operator
    println(str)

    val str1: String = "Hello World"
    val str2 = str1!!.toUpperCase()//not null asset operator
    println(str2)

}