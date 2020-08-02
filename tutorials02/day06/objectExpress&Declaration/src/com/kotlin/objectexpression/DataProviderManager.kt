package com.kotlin.objectexpression

/**
 * 对象声明定义的对象是单例的。
 */
object DataProviderManager {
    lateinit var name: String
    fun registDataProvider(provider: DataProvider) {

    }
}

fun main(args: Array<String>) {
    /**
     *
     */
    val provider1 = DataProviderManager
    val provider2 = DataProviderManager
    provider1.name = "test"
    println("provider2的name:${provider2.name}")
}