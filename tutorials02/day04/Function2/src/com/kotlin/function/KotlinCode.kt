package com.kotlin.dataclass

fun main(args: Array<String>) {
    val studengA = Student("Tommy", 102)
    val studengB = Student("Jaccy", 102)
    printName(studengA, studengB, str = "学生姓名打印")//命名参数
    val students = arrayOf(studengA, studengB)
//    printName(students, str = "学生姓名打印如下")//不允许调用
    printName(*students, str = "学生姓名打印如下")//伸展操作符（spread operator）将Array数据类型
    //的数据解包。

}

data class Student(val name: String, val roomNo: Int) {
    val grade: Int = 12
    fun printGrade() {
        println("Grade:$grade")
    }

}

/**
 * vararg修饰的可变参数
 */
fun printName(vararg students: Student, str: String) {
    println(str)
    var str1 = "This student is called"
    /**
     * 局部函数，局部函数可以访问其外部函数的局部变量
     */
    fun myPrint(student: Student) {
        println("$str1 " + student.name)
    }
    for (std in students) {//for 循环语句
//        println(std.name)
        myPrint(std)
    }
}