package com.kotlin.kt06

import kotlin.jvm.functions.FunctionN

/**
 *  16 函数
 *      16.1 返回Unit的函数
 *          如果一个函数不返回任何有用的值，它的返回类型是Unit。
 *      16.2 单表达式函数
 *          当函数的返回值为非Unit时，且函数体中只有一行代码，则可以省略花括号并且在=号之后直接
 *          书写代码接口。
 *          当返回值类型可以由编译推断出类型时，可以省略返回值类型的显示声明。
 *      16.3 中缀函数
 *          标有infix关键字的函数可以使用中缀表示法（忽略调用时候的点和圆括号）。中缀函数满足的条件
 *          ：该函数需是成员函数或扩展函数；必须只有一个参数；其参数不能有默认值。
 *      16.4 高阶函数
 *          概念：是将函数作为参数或返回值的函数。
 *          16.4.1 函数类型
 *          http://www.hudroid.cn/kotlin/kotlin-FunctionType/
 *              所有函数类型的形式为有一个圆括号括起来的参数列表和一个返回类型:(A, B) -> C
 *              表示接受类型是A、B的两个参数和返回一个C类型的函数类型。参数列表可以为空。函数类型
 *              可以有一个额外的接受者类型，例如A.(B) -> C
 *              函数类型在表示的时候可以选择性地在参数列表中包含参数名，例如(x: Int, y: Int) -> Point
 *              如需将函数类型指定为可空类型则：((x: Int, y: Int) - > Int)?
 *              函数类型对应的类为FunctionN，FunctionN中定义了invoke方法，其中N表示函数类型
 *              中的参数的个数。函数类型的实例可以直接作为函数调用-其实质是调用invoke函数。
 *              函数类型和String、Int类型一样，是一种独立类型。lambda只是实现函数类型实例化的一种
 *              方式。
 *              Unit返回类型不能省略。
 *              函数类型和普通类型一样，可以被继承。
 *         16.4.2 函数类型的实例化
 *              (1)使用lambda表达式或匿名函数进行实例化。
 *              (2)使用实现函数类型接口的自定义类的实例。
 *              (3)函数引用及属性引用。https://www.jianshu.com/p/10358883455c
 *              默认情况下，函数声明中的返回值为函数类型是不带接受者的。
 *         16.4.3 匿名函数
 *              匿名函数的参数只能在括号内传递，这与普通函数声明时一样的，而Lambda表达式作为函数的最后一个参数
 *              其可以在圆括号以外。
 *         16.4.4 lambda表达式 https://www.cnblogs.com/Jetictors/p/8647888.html
 *              lambda表达式的本质其实是匿名函数，底层是通过匿名函数实现的。
 *              特点：
 *              (1)总是被大括号扩着。
 *              (2)参数在->之前声明，参数类型可以省略。
 *              (3)函数体在->的后面。
 *              it 不是Kotlin中的一个关键字。
 *              lambda表达式中的参数只有一个的时候，可以用it来代表此参数。it可表示为单个参数的隐式名称，
 *              是Kotlin语言约定的。
 *              在lambda表达式中，使用_表示未使用的参数，表示不处理这个参数。
 *         16.4.5 带接收者的函数字面值
 *              在Kotlin中提供了指定的接受者对象调用Lambda表达式的功能。在函数字面值的函数体中，可以调用
 *              接受者对象的方法而无需任何额外的限定符，它允许在函数体中访问接受者对象的成员。
 *         16.4.4 闭包
 *              闭包即函数中包含函数，这里的函数包括：匿名函数、lambda表达式、局部函数、对象表达式。
 *              Java是不支持闭包的，Kotlin支持闭包。
 *              Kotlin中几种闭包的表现形式。
 *        16.5 内联函数 https://segmentfault.com/a/1190000038996559
 *        16.5.1 内联函数的概念
 *              被inline修饰的函数就是内联函数。其原理：在编译期，把调用这个函数的地方用这个函数的方法
 *              体进行替换。
 *              val ints = intArrayOf(1, 2, 3, 4, 5)
                ints.forEach {
                    println("Hello $it")
                }

                forEach函数的定义为：
                public inline fun forEach(action: (Int) -> Unit): Unit {
                    for(element in this) action(element)
                }

                forEach函数每inline修饰，它是一个内联函数，
            16.5.2 内联函数的定义
                内联函数的定义很简单，就是在用inline修饰。
                虽然说内联函数可以减少函数的调用来优化性能，但是并不是每一个函数前加inline修饰就可以
                优化性能。我们一般将高阶函数定义为内联函数。

                使用高阶函数会带来一些性能上的损失：高阶函数中函数体中有关函数的引用的对象带来的内存分配
                和高阶函数的虚拟调用会带来运行时间的开销。而将高阶函数内联化后可减少了高阶函数额调用（直接
                调用的是函数体）、减少了函数引用的对象的创建。

                inline修饰符影响函数本身和传递给它的Lambda表达式：所有这些都将内联到调用处。
            16.5.3 禁用内联 https://www.kotlincn.net/docs/reference/inline-functions.html
                如果只希望内联一部分传递给内联函数的函数应用的参数，那么可以使用oninline修饰符
                修饰不希望内联的函数引用的参数。
                inline fun foo(inlined: () -> Unit, noinline noinlined: () -> Unit): Unit {}
                


 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 **/

val n = fun Int.(other: Int): Int = this + other//待接受者的匿名函数作为函数引用
class FunctionExample {
    fun printHello(name: String?): Unit {
        if (name != null) println("Hello, $name")
        else println("Hi, there!")
    }

    fun double(x: Int) = x * 2

    val l: (Int) -> Int = { a: Int -> 111 + a }//lambda表达式
    val f: (Int) -> Unit = fun(x: Int) {//匿名函数
        println("x = $x")
        println("x ^ 2 = ${x * x}")
    }

    val m: (Int) -> String = fun(x: Int) = "$x,"//匿名函数

    fun test(num1: Int, bool: (Int) -> Boolean): Int {
        return if (bool(num1)) {
            num1
        } else 0
    }

    //闭包 函数的返回值类型为函数类型 且携带状态值
    fun test1(b: Int): () -> Int {
        var a = 1
        return fun(): Int {
            a ++
            return a + b
        }
    }

    inline fun cost(block: () -> Unit) {
        val start = System.currentTimeMillis()
        block()
        println(System.currentTimeMillis() - start)
    }

    fun costNotInline(block: () -> Unit) {
        val start = System.currentTimeMillis()
        block()
        println(System.currentTimeMillis() - start)
    }
}

class Test : (Int) -> String {
    override fun invoke(p1: Int): String {
        return "$p1 xxx"
    }
}

fun main(args: Array<String>) {
    println(1 shl 2)
    println(Test()(110))
    val arr = arrayOf(1, 3, 5, 7)
    println(arr.filter { it > 3 }.component1())
    val functionExample = FunctionExample()
    println(functionExample.test(10) { it > 5 })
    println("lambda作为函数的参数${functionExample.test(10) { it: Int -> it > 5 }}")
//    println(functionExample.test(5) {it > 5})
    val map = mapOf(
        "key1" to "value1", "key2" to "value2",
        "key3" to "value3"
    )
    map.forEach { (_, value) -> println(value) }
    println("FunctionExamplt m ${functionExample.m.invoke(10)}")
    println("FunctionExamplt m ${functionExample.m(10)}")
    println("带接受者的匿名函数${2.n(3)}")
    println("------------闭包演示-------------")
    var test1 = functionExample.test1(10)
    println(test1.invoke())
    println(test1.invoke())
    println(test1.invoke())
    println("-----------------内联函数------------")
    val ints = intArrayOf(1, 2, 3, 4, 5)
    ints.forEach {
        println("Hello $it")
    }

    println("---------内联函数性能优化的证明------------")
    val str: String = "Hello inline function, Hello inline function, Hello inline function, Hello inline function" +
            "Hello inline function, Hello inline function, Hello inline function, Hello inline function" +
            "Hello inline function, Hello inline function, Hello inline function, Hello inline function"
    functionExample.cost { println(str) }
    functionExample.costNotInline { println(str) }



}