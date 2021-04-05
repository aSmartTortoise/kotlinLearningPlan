package com.kotlin.kt06

/**
 *  15 委托
 *      委托模式是软件设计中的一项基本技巧。在委托模式中有两个对象处理同一个请求，接收请求的对象将
 *      请求委托给另一个对象处理。Kotlin中使用by标识符来实现委托。
 *      委托分为类委托、属性委托。
 *      15.1 类委托
 *          类的委托即委托的类中的方法实际上是由受委托的类的对象的方法实现的。
 *          如下方例子中的by子句表示将b保存在Derived的对象内部，编译器会生成接口Base的所有
 *          方法，并将调用转发给b。
 *          如果Derived中重写的对应的函数，则实际上这个方法的执行不会转发给b。
 */

interface Base {
    fun print()
}

class BaseImpl(val x: Int) : Base {
    override fun print() {print(x)}
}

/**
 *  Derived类的方法委托传入的Base类的实例b处理，执行b的相应方法。
 */
class Derived(b: Base) : Base by b {
//    override fun print() {
//        print("Derived")
//    }
}

fun main(args: Array<String>) {
    val b = BaseImpl(10)
    Derived(b).print()
}
