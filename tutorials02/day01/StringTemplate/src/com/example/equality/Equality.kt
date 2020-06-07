package com.example.equality

fun main() {
    val personA = Person("John", 25)
    println("{personA name=${personA.name}, age=${personA.age}}")
    println("sum(10, 15) = ${sum(10, 15)}")
    val c = 4.25
    println("price = $$c")
}

fun sum(a: Int, b: Int) = a + b