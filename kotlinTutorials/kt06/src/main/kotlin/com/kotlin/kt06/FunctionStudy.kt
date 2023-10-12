package com.kotlin.kt06


val n = fun Int.(other: Int): Int = this + other//带接受者的匿名函数作为函数类型的实例

/**
 * 具名函数
 */
fun increament(i: Int): Int {
    return i + 1
}

/**
 *  对象声明
 */
object SumObject {
    fun sum(x: Int): Int = x + 1
}



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

    val m: (Int) -> String = fun(x: Int) = "$x,"//匿名函数作为函数类型的实例
    val n: (Int) -> Int = {it + 5}//只有一个参数的Lambada表达式的函数类型的声明
    var a = 100

    fun sum(it: Int): Int = it + 1

    fun test(num1: Int, bool: (Int) -> Boolean): Int {
        return if (bool(num1)) {
            num1
        } else 0
    }

    /**
     * 高阶函数
     */
    inline fun test2(a: Int, b: (Int) -> Int): Int {
        return a + b(a)
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

    /**
     *  闭包
     */
    fun foo2(): Int {
        val a = 2
        fun f1(): Int {
            return a + 10
        }
        return f1()
    }
}

/**
 *  实现函数类型接口的类
 */
class Test : (Int) -> String {
    override fun invoke(p1: Int): String {
        return "$p1 xxx"
    }
}

fun main(args: Array<String>) {
    println(1 shl 2)
    println(Test()(110))//实现函数类型的接口的类的实例
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
    map.forEach { _, value -> println(value) }
    println("FunctionExamplt m ${functionExample.m.invoke(10)}")
    println("FunctionExamplt m ${functionExample.m(10)}")
    println("带接受者的匿名函数${2.n(3)}")
    println("只有一个参数的Lambada表达式中用it代表这个参数${functionExample.n.invoke(10)}")
    println("--------------函数引用实现的函数类型的实例化---------")
    //使用标识符 :: 由具名函数的函数名 获取函数引用
    println("函数引用之顶层方法 ${functionExample.test2(10, ::increament)}")
    println("函数引用之成员方法 ${functionExample.test2(10, functionExample::sum)}")
    println("函数引用之对象声明中的方法 ${functionExample.test2(10, SumObject::sum)}")
    println("------------属性引用演示--------")
    println("属性引用:${functionExample::m.get()}")
    println("属性引用:${functionExample::m.get()(5)}")
    println("属性引用:${functionExample::a.get()}")
    functionExample::a.set(101)
    println("属性引用:${functionExample::a.get()}")
    println("属性引用:${functionExample::class.java}")
    println("属性引用:${FunctionExample::class}")//获取KClass
    println("------------闭包演示-------------")
    var test1 = functionExample.test1(10)
    println(test1.invoke())
    println(test1.invoke())
    println(test1.invoke())

    println(functionExample.foo2())
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


