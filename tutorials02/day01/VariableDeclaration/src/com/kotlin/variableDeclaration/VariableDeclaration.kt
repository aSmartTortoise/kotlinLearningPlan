package com.kotlin.variableDeclaration

typealias People = Set<Person>

fun main() {
    val number: Int = 25
    val john = Person("john", 25)
    val people = setOf(john, Person("Mary", 20))
    println(people)
}

data class Person(val name: String, var age:Int)