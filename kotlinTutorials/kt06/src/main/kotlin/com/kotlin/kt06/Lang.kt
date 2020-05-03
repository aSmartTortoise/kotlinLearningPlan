package com.kotlin.kt06

enum class Lang(val hello: String) {
    CHINESE("你好"),
    ENGLISH("hello"),
    JAPANESE("にいはお"),
    KOREAN("안녕하세요");

    fun sayHello() {
        println(this.hello)
    }

    init {

    }

    companion object {
        fun parse(lang: String):Lang {
            return Lang.valueOf(lang.toUpperCase())
        }
    }
}

fun main(vararg args: String) {
    if (args.isEmpty()) return
    var lang = Lang.parse(args.get(0))
    println(lang)
    lang.sayHello()
    lang.sayBye()
}

fun Lang.sayBye() {
    var bye = when(this) {
        Lang.CHINESE -> "再见"
        Lang.ENGLISH -> "bye"
        Lang.JAPANESE -> "さようなら"
        Lang.KOREAN -> "안녕히 가세요"
    }
    println(bye)
}