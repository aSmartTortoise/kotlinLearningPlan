package com.kotlin.visibiltymodifier

import com.koltin.accessmodifier.C

fun main() {
    val a = A()
    val b = B()
    val c = C()
    println(c)
}
//private visibility modifier在同一文件中可见
private class A
class B
