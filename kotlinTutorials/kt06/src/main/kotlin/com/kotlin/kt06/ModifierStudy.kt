package com.kotlin.kt06

import java.awt.Color

/**
 *  1 @JvmOverloads
 *      注解@JvmOverloads，使用在方法声明的地方，构造方法或其他一般方法上。方法的形式参数列表中，
 *  有的参数有默认值。那么它实际上声明了多个方法，且是重载方法。
 **/

/**
 *  类WebContainer的构造方法被注解@JvmOverloads修饰，且构造方法中有多个形式参数，有的参数有默认值
 *  则，实际上声明了三个构造方法。这三个构造方法的形式参数列表的参数分别为：
 *  1 width:Float
 *  2 width:Float, height:Float
 *  3 width:Float, height:Float, backgroud:Color
 *
 */
class WebContainer @JvmOverloads constructor(width: Float, hight: Float = 360f,
                                             backgroud: Color = Color.BLACK) {

}