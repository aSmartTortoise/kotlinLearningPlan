package com.kotlin.kt06.type

import java.util.Random

fun main(args: Array<String>) {
    // 多行字符串
    val text = """
|Tell me and I forget.
|Teach me and I remember.
|Involve me and I learn.
|(Benjamin Franklin)
    """.trimMargin()
//    println("text:$text")

    // 转义字符串
    val s = "Hello, world!\n, nina."
//    println("s:$s")

    // 获取 0和1 之间的随机数
    val random = Random().apply {
        for (i in 0 until 20) {
            val num = nextInt(0, 2)
            println("i:$i num:$num")
        }


    }


}

fun jsonToString() {
    val timeRange = "{\"start\": \"2024-05-23 18:00:00\", \"end\": \"2024-05-23 20:00:00\"}"
}

