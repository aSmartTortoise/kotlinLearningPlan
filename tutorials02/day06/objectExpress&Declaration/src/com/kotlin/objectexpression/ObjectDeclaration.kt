package com.kotlin.objectexpression

class Test {
    val name: String = "Test"
    object DataProviderManager {
        val url: String = "http://www.sina.com"
        fun registProver(provider: DataProvider) {
//            val providerName = name 不能访问外部类成员

        }
    }
}

fun main(args: Array<String>) {
    var test = Test()
//    test.DataProviderManager.url//不能访问
    println(Test.DataProviderManager.url)//通过类名访问该类内部的对象声明的对象
}