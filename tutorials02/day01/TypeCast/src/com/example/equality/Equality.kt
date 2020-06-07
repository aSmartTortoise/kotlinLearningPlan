package com.example.equality

fun main() {
    val personA = Person("John", 25)
    val someone: Any = personA
    val personB = someone as Person
    if (someone is Person) {
        println(someone.name)
    }
}