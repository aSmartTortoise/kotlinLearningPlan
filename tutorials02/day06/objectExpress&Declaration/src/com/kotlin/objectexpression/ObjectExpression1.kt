package com.kotlin.objectexpression

class C {
    /**
     * 匿名对象（对象表达式）作为私有作用域的函数的返回类型
     */
    private fun foo() = object {
        val x: String = "x"
    }
    /**
     * 匿名对象（对象表达式）作为公有作用域的函数的返回类型
     */
    fun publicFoo() = object {
        val x: String = "x"
    }

    fun bar() {
        val x = foo().x
//        var y = publicFoo().x
    }
}

