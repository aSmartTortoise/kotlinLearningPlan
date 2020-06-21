package com.kotlin.array

import java.math.BigDecimal

fun main() {
    val arrayInt = arrayOf(1, 2, 3)
    val arrayInt1 = arrayOf<Int> (1, 2, 3)
    val arrayLong : Array<Long> = arrayOf(1L, 2L, 3L)
    val array = arrayOf(1, 10.21f, 'c', "Jackie", BigDecimal(1))
    println("arrayInt1 is ${arrayInt1 is Array<Int>}")

    val arrayInt2 = Array(5) {i -> i * 2}
    for (num in arrayInt2) println(num)
    val personA = Person("Jessie", 20)
//    personA.printNumbers(arrayInt)//不能传通用数组
    personA.printNumbers(arrayInt.toIntArray())

    val intArrayOf = intArrayOf(1, 2, 3)
    personA.printNumbers(intArrayOf)//调用java中的方法，传入原生数组而非通用数组。

    val arrayInt3 = intArrayOf.toTypedArray()//原生数组转通用数组
    
}