package com.kotlin.kt06

/**
 *  作用域函数
 *      作用域函数设计的目的是：在对象的上下文中执行代码块。当一个对象调用这样的函数，并提供一个
 *  Lambda表达式的函数类型参数的时候，会形成一个临时的作用域。可以访问其对象而无需使用其名称。
 *  共有5类：let、run、with、supply、also。
 *      这些函数基本做了同样的事情：在一个对象上执行一个代码块。不同的是这个对象在代码块中
 *  如何使用，以及整个表达式的结果是什么。
 *  1 上下文对象：this or it
 *      每个作用域函数都使用以下两种方式之一来访问上下文对象。作为Lambda表达式的接收者
 *  （this）或者作为Lambda表达式的参数（it）。
 *  1.1 this
 *      run、with以及also通过关键字this引用对象。因此在它们的Lambda表达式中，可以像
 *  在普通的类函数中一样使用this来访问对象。大多数场景下，当访问对象时可以省略this。相对
 *  地如果省略了this，那么很难区分接收者对象的成员及外部对象或函数。
 *  1.2 it
 *      let和also将上下文对象做为Lambda表达式的参数。如果没有指定参数名，则可以使用隐式
 *  it访问该对象。
 *  2 返回值
 *      根据返回结果，作用域函数可以分为两类。
 *      apply、also返回上下文对象。
 *      let、run、with返回Lambda表达式结果。
 *  3 具体的函数
 *  3.1 run
 *      上下文对象作为Lambda表达式的接收者出现，返回结果为Lambda表达式的结果。
 *      当Lambda表达式中包含对象的初始化和返回值的计算时，run很有用。
 *
 */