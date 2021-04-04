package com.kotlin.kt06

/**
 * 13 对象表达式与对象声明
 *      13.1 对象表达式
 *          有时候我们需要创建一个对某个类做轻微改动的类的实例，而不需要声明一个新类然后在获取实例。
 *          在Kotlin中可以使用对象表达式、对象声明来实现。
 *          创建一个继承某个类的匿名内部类的实例。
 *          如果基类有构造函数，则必须传递给适当的构造函数参数给它。
 *          匿名对象可以用作本地或私有作用域中声明的类型，如果使用匿名对象作为共有函数的返回类型
 *          或共有属性的类型，那么该函数的返回类型或该属性的类型实际上是该匿名对象的超类型；如果
 *          匿名对象没有超类型，那么就是Any，这样在匿名对象中定义的成员无法访问，如下例子。
 *          对象表达式中可以访问其外部类中定义的成员。
 *      13.2 对象声明
 *          同类声明一样，我们可以使用标识符object声明一个对象，并指定该对象的基类，如果没有任何基类
 *          则默认是Any，对象声明不是表达式不能用在赋值符号的右边。
 *          对象声明的初始化过程是线程安全的并且在首次使用的时候进行。
 *          可以使用对象声明的名称直接访问其内部成员。
 *          对象声明不能在局部作用域，即不能再函数每部声明对象。但是它可以嵌套在其他对象声明中或
 *          非内部类中。
 *      13.3 伴生对象
 *          类内部的对象声明，可以用companion修饰符，称之为伴生对象。伴生对象内部成员可以通过
 *          类名直接调用。
 *          如果对象声明的名称和类一样则可以省略名称。
 *          伴生对象所在类的名称可以作为该伴生对象的引用。
 *          即使伴生对象的成员看起来像是Java语言中的静态成员，但实际上在运行时他们仍然是真实
 *          对象的实例成员。在jvm平台可以使用注解@jvmStatic，可以将伴生对象中的成员修饰为真正
 *          的静态函数或属性。
 *      对象表达式与对象声明的一个重要语义差别：
 *          对象表达式是在在使用的时候立即初始化的；
 *          对象声明时在第一次使用的时候延迟初始化的；
 *          伴生对象是在类被加载的时候初始化的，与Java静态初始化器语义匹配。
 *
 */

open class A(x: Int) {
    companion object {
        private const val TAG: String = "A"
        val desc: String = "a open class and has a companion object"
    }
    public open val y: Int = x
    fun foo() {
        val xy = object {
            var x: Int = 1
            val y: Int = 2
        }
        println("xy 的值 ${xy.x}:${xy.y}")
    }

    private fun fun1() = object {
        val x: String = "x"
    }

    /**
     * 无法访问函数fun2返回的对象中的成员x
     */
    public fun fun2() = object {
        val x: String = "x"
    }

    fun bar() {
        var x1 = fun1().x
//        val x2 = fun2().x
    }


}

interface B {}

object C {
    val name: String = "C.kotln"
    val lenght: Int = name.length
}



fun main(args: Array<String>) {
    val ab: A = object : A(1), B {
        override val y: Int = 15
    }
    ab.foo()
    var name = C.name
    println(A.desc)//伴生对象所在类的名称可以作为该伴生对象的引用。

}