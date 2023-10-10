package com.kotlin.kt06

fun main() {
    var i = 0
    foo {
        i++
        println(i)
    }
}

private inline fun foo(block: () -> Unit) {
    block.invoke()
}