package com.kotlin.kt06

/**
 *  7 扩展
 *      Kotlin能够扩展一个类的新功能而不用继承该类或者使用装饰器这样的设计模式。比如可以为
 *      一个不能修改的、来自第三方库中的类编写一个新的函数，这个新增的函数就像那个原始的类本来
 *      就有的函数一样，可以用普通的方式进行调用。这样的函数被称为扩展函数，同样的也有扩展属性。
 *      7.1 如果一个类定义一个成员函数和一个扩展函数，而这两个函数又有相同的接受者类型、相同
 *      的名字，并且都适用给定的参数，这种情况总是去成员函数。
 *      7.2 可以为可以为空类型的接受者定义扩展函数。
 *      7.3 扩展属性不能有初始化器。
 *      7.4 如果一个类内部定义了伴生对象，则也可以为伴生对象定义扩展属性或扩展函数，就像伴生对象
 *      的普通成员一样，可以使用类名直接调用扩展函数或扩展属性。
 *      7.5 在一个类的内部可以为另一个类声明扩展。在这样的扩展内部，有多个隐式接收者。
 *      扩展声明所在类的对象称为分发接受者，调用扩展方法的类的对象称为扩展接受者。对于分发接受者和扩展
 *      接受者的成员名字一样的情况下，扩展接受者优先。要应用分散接受者成员可以使用this关键字限定符。
 *
 **/

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tem = this[index1]
    this[index1] = this[index2]
    this[index2] = tem
}

open class Shape
fun Shape.getName() = "Shape"
class Rectange: Shape()
fun Rectange.getName() = "Rectangel"
fun printClassName(s: Shape) {
    println(s.getName())
}

fun Any?.toString(): String {
    return this?.toString() ?: "null"
}

class Host(val hostName: String) {
    fun printHostName() {print(hostName)}
}

class Connection(val host: Host, val port: Int) {
    fun printPort() { print(port)}
    fun Host.printConnection() {
        printHostName()
        print(":")
        printPort()
    }
    fun connect() {host.printConnection()}
}

fun main() {
    printClassName(Rectange())
    Connection(Host("kotl.in"), 443).connect()
}
