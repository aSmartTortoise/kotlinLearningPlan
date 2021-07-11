package com.kotlin.kt06

/**
 ** 17 集合
 **     val numbersMap = mapOf<String, Int>("key0" to 1, "key1" to 2, "key2" to 3)
 ** to 符號 創建一個临时存活的Pair对象，因此仅在性能不重要的时候这样使用。为避免过多的内存消耗
 ** 可以这样使用
 **     val numbersMap = mutableMapOf<String, Int>().apply { this["key0"] = 1; this["key1"] = 2 }
 ** 17.1 序列
 *      除了集合之外，Kotlin标准库中还有另一种容器类型：序列 Sequence<T>。序列提供与Iterable相同的函数。
 *      通过使用计算其元素的函数来构建序列。要基于函数构建序列，则以该函数为参数并调用generageSequence()。
 *      可以将第一个元素指定为显示值，或函数调用的结果，当提供的函数返回null时，则序列生成终止。
 *      当Iterable的处理包含多个步骤的时候，它们会优先执行：每个处理完成并返回结果——中间集合。在此集合的基础上
 *      执行以下步骤。反过来，序列的多步处理在可能的情况下会延迟执行：仅当请求整个处理链的结果时，才进行实际计算。
 *      
 *
 */
class Version(val major: Int, val minor: Int): Comparable<Version> {
    override fun compareTo(other: Version): Int {
        if (this.major != other.major) {
            return this.major - other.major
        }
        return this.minor - other.minor
    }
}
fun main(args: Array<String>) {
    /**
     * 构造函数
     */
    val numbers = listOf<String>("one", "two", "three", "four")
    //过滤列表filter创建与过滤器匹配的新元素的列表。
    var longNumbers = numbers.filter { it.length > 3 }
    println(longNumbers)
    //映射生成转换结果列表
    val numberSet = setOf<Int>(1, 2, 3)
    println(numberSet.map { it * 2 })
    println(numberSet.mapIndexed { index, value -> index * value })
    println("-------------------------------")
    /**
     * 区间与数列
     */
    // 1 <= i && i <= 10
    for (i in 1 .. 10 step 2) print("$i ")
    println("-------------自定义类的区间的声明-----------")

    val versionRange = Version(1, 11) .. Version(1, 30)
    println(Version(1, 9) in versionRange)
    println(Version(1, 15) in versionRange)
    println("-------------序列Sequence-------------")
    val numberSequece = sequenceOf(0, 1, 2, 3)
    val numberList = listOf<String>("one", "two", "three", "four")
    var numberAsSquence = numberList.asSequence()
    println(generateSequence(1) {if (it + 1 < 10) it + 1 else null}.count())
    println("------Iterable与Sequece 的操作的示例")
    val wordList = "The Quick Brown Fox Jumps over the Lazy Dog".split(" ")
    var lenghtList = wordList.filter { println("filter: $it"); it.length > 3 }
        .map { println("length: ${it.length}"); it.length }
        .take(4)
    println("lengths of first 4 words longer than 3 cahrs.")
    println(lenghtList)
    println("-----the sequece------")
    var wordSquece = wordList.asSequence()
    var lengthSequence = wordSquece.filter { println("filter: $it"); it.length > 3 }
        .map { println("the leangth: ${it.length}"); it.length }
        .take(4)
    println("lengths of first 4 words longer than 3 cahrs.")
    println(lengthSequence.toList())


}


