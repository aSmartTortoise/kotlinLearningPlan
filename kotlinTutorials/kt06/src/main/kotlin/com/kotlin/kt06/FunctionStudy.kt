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
 *              如果一个Lambada表达式中只有一个参数，那么可以省略这个参数和->，用it代表这个参数（
 *              https://blog.csdn.net/u011288271/article/details/108385586）
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

                forEach函数被inline修饰，它是一个内联函数，
            16.5.2 内联函数的定义
                内联函数的定义很简单，就是用inline修饰。
                虽然说内联函数可以减少函数的调用来优化性能，但是并不是每一个函数前加inline修饰就可以
                优化性能。我们一般将高阶函数定义为内联函数。

                使用高阶函数会带来一些性能上的损失：高阶函数的函数体中有关函数的引用的对象带来的内存分配
                和高阶函数的虚拟调用会带来运行时间的开销。而将高阶函数内联化后可减少了高阶函数的调用（直接
                调用的是函数体）、减少了函数引用的对象的创建。

                inline修饰符影响函数本身和传递给它的Lambda表达式：所有这些都将内联到调用处。
            16.5.3 禁用内联 https://www.kotlincn.net/docs/reference/inline-functions.html
                如果只希望内联一部分传递给内联函数的函数应用的参数，那么可以使用oninline修饰符
                修饰不希望内联的函数引用的参数。
                inline fun foo(inlined: () -> Unit, noinline noinlined: () -> Unit): Unit {}
                可以内联的Lambda表达式只能在内联函数的内部调用或者作为可以内联的参数传递，但是被
                oninline修饰的Lambda表达式可以以任何我们喜欢的方式操作：存储在字段中、传递它等。
            16.5.4 非局部返回
                在Kotlin中，我们只能对具名函数或匿名函数使用正常的、非限定的return返回。这意味着
            要退出一个Lambda表达式，我们必须使用标签，并且在Lambda表达式内部禁止使用裸return，因为
            Lambada表达式不能使包含它的函数返回。(https://www.kotlincn.net/docs/reference/inline-functions.html)
            Lambda禁止使用return关键字，但是可以使用限定的返回语法：return@函数名 显示返回一个值，否则
            则隐式返回最后一个表达式的值。（https://blog.csdn.net/u011288271/article/details/108385586）
            一个不带标签的return语句总是在使用fun关键字声明的函数中返回。
            内联函数中的Lambda表达式可以使用非限定的return语句，返回的是外部那个调用内联函数的函数，而
            不是内联函数本身。这就叫内联函数的Lambda表达式的非局部返回。
            16.5.5 具体化的类型参数

            16.5.6 内联属性
                inline可以修饰没有幕后字段的属性的访问器；可以标注独立的属性访问器，也可以标注整个
            属性。
            16.5.7 公有api内联函数的限制
                当一个函数被public或者protected修饰的时候，它就是一个模块级的公有api，可以在其他
            模块中调用它。
                这样对于公有api的内联函数来说，当本模块的的api发生变更时导致其他调用这个内联函数的模块
            发生二进制兼容的风险。——声明一个内联函数但调用它的模块在它发生改变时并没有重新编译。
            为了消除由非公有api变更引起的二进制兼容的风险，公有api的内联函数的函数体内不允许使用非公有
            的声明，即不允许使用由private或internal修饰的声明或其他组件。一个internal声明，可以由
            @PublishedApi标注，这允许在公有api的内联函数的函数体内部使用该api。当一个internal修饰的
            内联函数被标记为@PublishedApi时，它会像公有函数一样检测其函数体。

 **/

val n = fun Int.(other: Int): Int = this + other//带接受者的匿名函数作为函数类型的实例
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
    val n: (Int) -> Int = {it + 5}//只有一个参数的Lambada表达式的函数类型的声明

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

    /**
     * 内联属性 1
     */
    var name: String
        inline get() = "FunctionExample"
        set(value) {
            println("set $value")
        }

    /**
     * 内联属性2
     */
    inline var length: Int
        get() = 100
        set(value) {
            println("set $value")
        }

    /**
     * 公有api的内联函数的限制，被interanl修饰的函数被标记为@PublishedApi时，可以在
     * 公有api的内联函数的函数体中使用。
     */
    @PublishedApi
    internal fun hello() {
        println("hello this is a internal function.")
    }

    inline fun fun1(block: () -> Unit) {
        hello()
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
    println("只有一个参数的Lambada表达式中用it代表这个参数${functionExample.n.invoke(10)}")
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