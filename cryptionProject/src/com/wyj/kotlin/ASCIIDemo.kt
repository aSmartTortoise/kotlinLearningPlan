package com.wyj.kotlin

import java.lang.StringBuilder

fun main() {
//    var character: Char = 'a'
//    var num = character.toInt()
//    println("num = $num")
    var str:String = "I LOVE YOU"
    var charArray = str.toCharArray()
    var sb = StringBuilder()
    //with 高阶函数的使用
    var result = with(sb) {
        for (c in charArray) {
            append(c.toInt().toString() + " ")
        }
        sb.toString()
    }

    println(result)
}