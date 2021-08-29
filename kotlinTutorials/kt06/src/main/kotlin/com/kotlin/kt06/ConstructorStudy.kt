package com.kotlin.kt06

fun main(args: Array<String>) {
    val student = Student("Alice", 15).also { it.learn() }
    val teacher: Teacher = HistoryTeahcer("Tom", 29, "History")
}

/**
 * 一个类可以有一个主构造函数和多个次构造函数，主构造函数不能有其他代码，初始化工作可以
 * 在init初始化代码块中进行。init初始化代码块中的代码是主构造函数的一部分，
 * 2 如果类有主构造函数和次构造函数，则次构造函数要委托给主构造函数，可以是直接委托或通过
 * 其他次构造函数间接委托。委托到同一个类的另一个构造函数，用this即可
 * 3 如果一个非抽象类没有声明任何构造函数（主或次），那么，它实际上是有一个生成不带参数的主构造
 * 函数，而且这个主构造函数的可见性是publc的；如果希望这个类的主构造函数的可见性不是public的，
 * 则需要声明一个带有默认可见性的主构造函数（比如private）
 * 4 默认情况下一个类是final的，不能被继承，要使一个class能够被继承，需要声明为open。
 * 5 如果子类有一个主构造函数，则子类必须用其主构造函数的参数初始化父类。
 * 6 如果子类没有主构造函数，有次构造函数，那么子类必须通过super关键字初始化父类；
 * 7 Kotlin父类中可覆盖的成员和子类覆盖后的成员要显示表示 父类加open，子类加override；标记为
 * override的成员是open的，如果希望它不继续被覆盖，可继续追加final修饰符。
 * 8 子类中可以定义var的属性覆盖父类中val的属性，反之则不行；因为val属性本质上声明了一个get方法，
 * 而子类覆盖该属性并声明为var只是在子类中额外增加了一个set方法。
 * 9 派生类初始化顺序：在构造派生类的实例过程中，首先完成基类的初始化。这意味着在基类构造函数执行时
 * 派生类声明或覆盖的属性还未被初始化，如果在基类初始化逻辑中（直接或通过另一个覆盖的open成员的实现间接）
 * 使用了任何一个这种属性，那么就可能导致不正常的行为或运行时故障。设计一个基类时，应该避免在构造函数，
 * 属性初始化器以及init块中使用open成员。
 * 10 内部类访问其外部类超类的成员，可以通过通过由外部类名限定的super实现：super@OuterClassName
 * 10.1 接口中的成员默认是可覆盖的。
 * 11 如果派生类的某个覆盖的成员方法，从它的多个直接父类中继承了多个实现，那么为了区分是从那个父类继承的
 * 实现，可以使用尖括号中超类的类名的super关键字：Super<BaseClassName>。
 * 12 抽象类的抽象函数不能被实现，不需要用open关键字标注一个抽象类或抽象函数。
 *
 */
public open class Person public constructor(var name: String, var age: Int) {
    init {
        println("initialize Person")
    }

    var children: MutableList<Person> = mutableListOf()
    open val size: Int = name.length.also { println("initialize size in Prson : $it") }

    public constructor(name: String, parent: Person) : this(name, 1) {
        parent.children.add(this)
    }

    open fun learn() : Unit {
        println("Person learn skill")
    }
}

open class Student public constructor(name: String, age: Int) : Person(name, age), Learner {
    init {
        println("initialize Student")
    }

    override val size: Int = (super.size + "Student`s size".length).also {
        println("initialize size in Student : $it")
    }

    constructor(name: String, age: Int, grade: Int) : this(name, age) {

    }

    constructor(name: String, age: Int, gender: String) : this(name, age) {

    }

    final override fun learn() {
        super<Person>.learn()
        println("跟着老师学习知识 获取基类的name:${super<Person>.name}")
        super<Learner>.learn().run { println("as a independent person, a student could leanrn" +
                " some extra skills") }
    }
}

abstract class Teacher : Person {

    public constructor(name: String, age: Int, subject: String) : super(name, age) {
        val labor = Labor().also { it.work(subject) }
    }

    inner class Labor {
        fun work(subject: String) {
            println("teach ${super@Teacher.name} student course $subject, and make money~")
        }
    }

    abstract override fun learn()
}

class HistoryTeahcer public
    constructor(name: String, age: Int, subject: String = "history")
    : Teacher(name, age, subject) {

    override fun learn() {

    }
}

interface Learner {
    fun learn() {}
}