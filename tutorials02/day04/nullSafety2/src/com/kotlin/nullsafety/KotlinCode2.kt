package com.kotlin.nullsafety

fun main() {
//    val notNullableString: String = "Hello World"
//    val nullableString: String? = null
//
//    println(nullableString == notNullableString)// ==也是safe call operator

    var name: String? = "John"
//    printName(name)//方法参数是notNullable data type
//    if (name != null) {
//        printName(name)
//    }

    val result = name?.let { printName(it) }
    println("result:$result")
}

fun printName(name: String) {
    println("Name: $name")
}