package com.kotlin.variableDeclaration

typealias People = Set<Person>

fun main() {
    val number: Int = 25
    val john = Person("john", 25)
    val people:People = setOf(john, Person("Mary", number))
    println(people)
    val names = arrayListOf("Jack", "Smith", "Lucy")
    println(names[1])

}

data class Person(val name: String, var age:Int)