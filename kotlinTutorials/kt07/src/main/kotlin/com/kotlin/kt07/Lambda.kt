package com.kotlin.kt07

import rx.Observable
import java.io.File

fun main(args: Array<out String>) {
    //根据project 资源中的文件路径构造File对象，kotlin中可根据File的扩展方法，读取其中的文本内容
    var text = File(ClassLoader.getSystemResource("input").path).readText()
    Observable
        .from(text.toCharArray().asIterable())
        .filter {
            !it.isWhitespace()
        }
        .groupBy {
            it
        }
        .map { o ->
            o.count().subscribe {
                println(o.key + " -> " + "$it")
            }
        }
        .subscribe()
}