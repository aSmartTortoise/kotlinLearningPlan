package com.kotlin.delegate

interface Base {
    fun print()
}

class BaseImpl constructor(var x: Int): Base {
    override fun print() {
        println(x)
    }
}

/**
 * Derived类完全继承接口Base，其成员方法print()，委托Base类的b对象的成员方法实现的
 */
class Derived(b: Base): Base by b

fun main(args: Array<String>) {
    val b = BaseImpl(10)
    Derived(b).print()
}