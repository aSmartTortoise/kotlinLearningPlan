package com.kotlin.kt06

fun main(args: Array<String>) {
    /**
     * 在Kotlin中，任何表达式都可以被标记 格式为 标识符+@符号
     */
//    foo()
//    fooWithExplicitLabel()
//    fooWithInexplicitLabel()
    fooAnonymousFunction()
}

fun foo() {
    listOf(0, 1, 2, 3, 4, 5).forEach {
        if (it == 3) return
        println(it)
    }

    println("This point is unreachable!")
}

fun fooWithExplicitLabel() {
    listOf(0, 1, 2, 3, 4, 5).forEach lit@{
        if (it == 3) return@lit
        println(it)
    }

    println("Done with explicit label!")
}

fun fooWithInexplicitLabel() {
    listOf(0, 1, 2, 3, 4, 5).forEach {
        if (it == 3) return@forEach
        println(it)
    }

    println("Done with inexplicit label!")
}

fun fooAnonymousFunction() {
    listOf(0, 1, 2, 3, 4, 5).forEach(fun(value: Int) {
        if (value == 3) return
        println(value)
    })

    println("Done with anonymous function!")
}