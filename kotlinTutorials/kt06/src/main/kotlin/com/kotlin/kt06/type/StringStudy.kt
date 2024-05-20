package com.kotlin.kt06.type

fun main(args: Array<String>) {
    // 多行字符串
    val text = """
|Tell me and I forget.
|Teach me and I remember.
|Involve me and I learn.
|(Benjamin Franklin)
    """.trimMargin()
    println("text:$text")

    // 转义字符串
    val s = "Hello, world!\n, nina."
    println("s:$s")
}