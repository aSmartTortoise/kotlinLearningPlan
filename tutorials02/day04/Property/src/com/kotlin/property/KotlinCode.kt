package com.kotlin.dataclass

fun main(args: Array<String>) {
    val studengA = Student("Tommy", 102)
    println(studengA)
    var studentB = studengA.copy()
    println(studengA == studentB)
}

data class Student(val name: String, val roomNo: Int) {
    private val grade: Int = 12
    
    fun printGrade() {
        println("Grade:$grade")
    }
}