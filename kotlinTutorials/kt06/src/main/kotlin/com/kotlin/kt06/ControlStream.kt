package com.kotlin.kt06

import java.util.*

fun main(args: Array<String>) {
    val x = Random().nextInt(6)
    printWeekday(x)
    val weekday: String = getWeekday(x)
    print(weekday)


}

/**
 * when 条件表达式
 */
private fun getWeekday(x: Int): String {
    return when (x) {
        0 -> "Monday"
        1 -> "Tuesday"
        2 -> "Wednesday"
        3 -> "Thursday"
        4 -> "Friday"
        5 -> "Saturday"
        6 -> "Sunday"
        else -> "not a week day"
    }
}

/**
 * when条件语句
 */
private fun printWeekday(x: Int) {
    when (x) {
        0 -> print("Monday")
        1 -> print("Tuesday")
        2 -> print("Wednesday")
        3 -> print("Thursday")
        4 -> print("Friday")
        5 -> print("Saturday")
//        6 -> print("Sunday")
    }
}