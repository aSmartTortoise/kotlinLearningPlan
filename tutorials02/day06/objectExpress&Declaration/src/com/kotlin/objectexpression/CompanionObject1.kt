package com.kotlin.objectexpression

interface Factory<T> {
    fun create(): T
}
class MyClass {
    companion object: Factory<MyClass> {
        lateinit var name: String
        override fun create() = MyClass()
    }

}

fun main(args: Array<String>) {
    val instance = MyClass.create()
    val instance2 = MyClass.Companion
    val instance3 = MyClass

}