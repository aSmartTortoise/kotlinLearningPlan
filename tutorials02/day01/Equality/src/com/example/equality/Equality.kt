package com.example.equality

fun main() {
    val personA = Person("John", 25)
    val personB = Person("Adam", 30)
    val personC = Person("John", 25)

    println(personA == personB)
    println(personA == personC)

    println(personA.equals(personB))
    println(personA.equals(personC))

    println(personA === personC)
}