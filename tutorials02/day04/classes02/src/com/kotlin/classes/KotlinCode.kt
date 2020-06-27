package com.kotlin.classes

fun main(args: Array<String>) {
    println("This call primitive constructor.")
    val personA = Person("Jonny Depp", 51)
    println("this call secondary constructor.")
    var personB = Person("Jackie", 33, "male", true)
}