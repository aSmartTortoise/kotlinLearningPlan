package com.kotlin.classes

import com.kotlin.constant.SYSTERM_DEPRECATED

/**
 * 1.kotlin中的class的可见性修饰符默认是public的，
 * 2.如果类成员为空，即没有属性和方法，则大括号可以省略
 * 3.其构造方法分为主构造方法和次构造方法
 * 3.1每个类都是有主构造方法的，如果没有给类声明主构造方法，则系统会默认给声明一个没有参数的构造方法，且
 * 可见性是public的
 * 3.2 主构造方法位于类名和花括号之间，这个部分是类头(class header)，
 * 3.3声明主构造方法，使用constructor关键字，紧跟小括号，小括号中可以声明类型参数列表
 * 3.4 构造方法的可见性默认是public的，如果要有其他可见性，可见性修饰符位于类名和constructor之间
 * 如果可见性修饰符是public的，则public可以省略，constructor关键字也可以省略。
 * 4 类还需要有成员变量接收构造方法传进来的参数，其方式可以在主构造方法的类型参数列表中，加上val或
 * var的修饰符
 * 5 成员变量也可以定义在类成员中，成员变量的初始化可放在init初始化代码块中，init初始化代码块
 * 是主构造方法的一部分。也可直接赋值。
 * 6 在主构造方法类型参数列表中也可以对成员变量进行赋值
 */
@Deprecated(SYSTERM_DEPRECATED)
class Person constructor(val name: String, var age: Int = 25) {
    var gender: String = "male"
    var isMarried: Boolean
    init {
        isMarried = true
        println("this is init block.")
    }

    constructor(name: String, age: Int, gender: String): this(name, age) {
        this.gender = gender
        println("This is the first secondary constructor.")
    }

    constructor(name: String, age: Int, gender: String, isMarried: Boolean): this(name, age, gender) {
        println("This is the second secondary constructor.")
        this.isMarried = isMarried
    }
}
