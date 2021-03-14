package com.kotlin.kt06

fun main(args: Array<String>) {

}

/**
 * 一个类可以有一个主构造函数和多个次构造函数，主构造函数不能有其他代码，初始化工作可以
 * 在init初始化代码块中进行。init初始化代码块中的代码是主构造函数的一部分，
 * 2 如果类有主构造函数和次构造函数，则次构造函数要委托给主构造函数，可以是直接委托或通过
 * 其他次构造函数间接委托。委托到同一个类的另一个构造函数，用this即可
 * 3 如果一个非抽象类没有声明任何构造函数（主或次），那么，它实际上是有一个生成不带参数的主构造
 * 函数，而且这个主构造函数的可见性是publc的；如果希望这个类的主构造函数的可见性不是public的，
 * 则需要声明一个带有默认可见性的主构造函数（private）
 * 4 默认情况下一个类是final的，不能被继承，要使一个class能够被继承，需要声明为open。
 * 5 如果子类有一个主构造函数，则子类必须用其主构造函数的参数初始化父类。
 * 6 如果子类没有主构造函数，有次构造函数，那么子类必须通过super关键字初始化父类；
 * 7 Kotlin父类中可覆盖的成员和子类覆盖后的成员要显示表示 父类加open，子类加override；标记为
 * override的成员是open的，如果希望它不继续被覆盖，可继续追加final修饰符。
 * 8 子类中可以定义var的属性覆盖父类中val的属性，反之则不行；因为val属性本质上声明了一个get方法，
 * 而子类覆盖该属性并声明为var只是在子类中额外增加了一个set方法。
 */
public open class Person public constructor(open var name: String
                                            , open var age: Int) {
    init {
        println("初始化语句")
    }

    var children: MutableList<Person> = mutableListOf()

    public constructor(name: String, parent: Person) : this(name, 1) {
        parent.children.add(this)
    }

    open fun learn() : Unit {

    }



}

open class Student public constructor(override var name: String,
                                      override var age: Int) : Person(name, age) {
    constructor(name: String, age: Int, grade: Int) : this(name, age) {

    }

    constructor(name: String, age: Int, gender: String) : this(name, age) {

    }

    final override fun learn() {
        super.learn()
        println("跟着老师学习知识")
    }
}

open class Teacher : Person {

    public constructor(name: String, age: Int, subject: String) : super(name, age) {

    }
}