package com.kotlin.kt09

import java.math.BigInteger
class Result(var value:BigInteger = BigInteger.valueOf(1L))
// tailrec关键字 表示尾递归
tailrec fun factorial(num : Int, result: Result)  {
    if (num == 0) result.value = result.value.times(BigInteger.valueOf(1L))
    else {
        result.value = result.value.times(BigInteger.valueOf(num.toLong()))
        factorial(num - 1, result)
    }
}

fun main() {
//    println(factorial(10000))
    val result = Result()
    factorial(100000, result)
    println(result.value)
}