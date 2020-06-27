package com.kotlin.array

import com.kotlin.nullsafety.Person
import java.math.BigDecimal

fun main() {
    val str: String? = null//在None-nullable data type后面加上?，即表示该变量可以是nullable type
    println(str?.toUpperCase())//safe call operator

    val personA: Person = Person("John", 25)
    val street = personA.address?.street?.name
    println(street)

//    val str1 = if (str != null) {
//        str
//    } else {
//        "the default value"
//    }

    val str1 = str?: "the default value"//always operator
    println(str1)



    
}