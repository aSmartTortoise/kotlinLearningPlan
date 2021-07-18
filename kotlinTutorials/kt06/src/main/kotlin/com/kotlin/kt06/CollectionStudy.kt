package com.kotlin.kt06

import java.lang.StringBuilder

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
 * 17.2 集合操作概述
 * 17.2.1 转换
 * 17.2.1.1 映射 https://www.bookstack.cn/read/JackChan1999-Kotlin-Tutorials/%E9%9B%86%E5%90%88%E6%A1%86%E6%9E%B6-%E6%98%A0%E5%B0%84%E6%93%8D%E4%BD%9C%E5%87%BD%E6%95%B0.md#94efh5
 *      map(transform: (T) -> R): List<R>
 *      将集合中的元素通过转换函数transform映射后的结果，存到一个集合中返回。原理是遍历结合中的元素，并将元素通过
 * 转换函数transform映射后的结果放到一个新的集合destination中，并返回destination。
 *      mapIndexed(transform: (kotlin.Int, T) -> R): List<R>
 *      同时可以使用集合中的索引和元素的值来进行转换。原理是遍历集合中的元素，将index和对应的元素根据转换
 * 函数transform映射后的结果放到一个集合中，并返回这个新的集合。
 *      mapNotNull(transform: (T) -> R?): List<R>
 *      遍历集合中的每个元素，将元素根据转换函数transform映射后的结果，进行判断，如果不为null，则放到
 * 一个新的集合中，最后返回这个集合，该集合中的元素不为null。
 *      flatMap(transform: (T) -> Iterable<R>): List<R>
 *      遍历集合中的每一个元素，把第一个元素映射成一个List1，将第二个元素映射成List2后，List1和List2
 * 合并List1.addAll(List2)，依次类推，然后返回一个扁平的List。
 *  17.2.1.2 合拢 https://www.kotlincn.net/docs/reference/collection-transformations.html
 *      合拢转换就是两个集合由相同位置的元素构建配对。在Kotlin标准库中是通过zip扩展函数来实现的。zip是
 *  生产操作符的一种。
 *      zip(other: Iterable<R>): List<Pair<T, R>>
 *  两个集合按照下标配对，组合成的每个Pair对象作为新的List集合中的元素，并返回这个集合。如果两个集合的长度
 *  不一样，则去最短的长度。https://www.bookstack.cn/read/JackChan1999-Kotlin-Tutorials/%E9%9B%86%E5%90%88%E6%A1%86%E6%9E%B6-%E7%94%9F%E4%BA%A7%E6%93%8D%E4%BD%9C%E7%AC%A6.md
 *  zip生产操作符也可以用中缀形式 list1 zip list2。
 *      Iterable<T>.zip(other: Iterable<R>, transform: (t1, t2) -> V): List<V>
 *  两个集合中相同位置的元素根据指定的转换函数transform转换后，形成的V对象，由V元素构建新的集合，并返回
 *  这个集合。
 *      List<Pair<T, R>>.unzip(): Pair<List<T>, List<R>>
 *  以Pair<T, R>为元素的集合，依次去Pair<T, R>元素的key， value，将key放到集合list1中，将value放到集合
 *  list2中，然后以集合list1为key，集合list2为value构建新的Pair对象，并返回这个Pair对象。
 *  17.2.1.3 关联
 *      基本的关联函数是associateWith，其中原始集合中的元素是键，原始集合中的元素根据指定的转换函数转换后
 *  得到的结果为值，有key和value形成map，如果同一个key有相同的value，则去最后个一个Pair对象，并返回。
 *      List<T>.associateBy(transform: (T) -> K): Map<K, T>
 *      将结合中的元素根据指定的转换函数transform转换得到结果K，形成以K为key，以T为value的map，并返回
 *  这个map。
 *      List<T>.associate(keySelector: (T) -> K, valueTransform: (T) -> V): Map<K, V>
 *      将集合中的元素根据指定的转换函数转换得到的结果 为key，将集合中的元素根据指定的值转换函数转换得到的结果
 *  为value，得到key value映射的集合Map，并返回。
 *      List<T>.associate(transform: (T) -> Pair<K, V>): Map<K, V>
 *      集合中的元素根据指定的转换函数transform 生成Pair对象，其中key、value为元素映射函数的结果，然后根据
 *  Pair生成对应的Map，并返回。但是这种方式会生成临时的Pair对象，一定程度上会带来性能上的损失。
 *  17.2.1.4 打平
 *      打平转换是用来操作或访问嵌套集合中的元素的。
 *      Interable<Interable<T>>flatten(): Interable<T>
 *      flatMap(transform: (T) -> Iterable<R>): List<R>
 *      遍历集合中的每一个元素，把第一个元素映射成一个List1，将第二个元素映射成List2后，List1和List2
 * 合并List1.addAll(List2)，依次类推，然后返回一个扁平的List。
 *  17.2.1.5 字符串表示
 *      Iterable<T>.joinToString()
 *      默认的实现是将集合中的元素逐个取出，并以逗号分隔形成String。
 *      要构建自定义的String形式，可以在实际参数中指定separator，prefix， postfix，得到的字符串将以
 * prefix前缀开头，以postfix后缀结尾，每个元素以separator分隔的String。
 *      如果集合的长度比较大，只想得到前面limit个元素的String的形式，则可以指定limit参数，和truncated
 * 参数。得到的String中以truncated表示剩余的元素。
 *      如果要求Sting中的元素自定义，可以指定transform: (T) -> CharSequence
 *      <T, A: Appendable> Iterable<T>.joinTo(buffer: A, separator: Charsequece,
 *  prefix: Charsequence, posfix: Charsequenc, limit: Int, truncated: Charsequenc,
 *  transform: (T) -> Charsequence): Charsequece
 *      实现的思路和joinToString类似，将集合中的每个元素逐个根据指定的transform函数转换后，再追加到指定的
 *  appendable对象上去。
 *  17.2.2 过滤
 *      https://www.kotlincn.net/docs/reference/collection-filtering.html
 *      https://www.bookstack.cn/read/JackChan1999-Kotlin-Tutorials/%E9%9B%86%E5%90%88%E6%A1%86%E6%9E%B6-List%E8%BF%87%E6%BB%A4%E6%93%8D%E4%BD%9C%E5%87%BD%E6%95%B0.md#9ri08o
 *      过滤是最常用的集合处理任务之一。过滤条件有谓词定义——接收一个集合元素，并返回一个Boolean的Lambda
 *  表达式。
 *  17.2.2.1 按谓词过滤
 *      Iterable<T>.filter(predicate: (T) -> Boolean): List<T>
 *      过滤出满足条件的元素组成的子集合。返回的结果是List有索引的，可以重复的集合。
 *      Iterable<T>.filterIndexed(predicate: (index, T) -> Boolean): List<T>
 *      Iterable<T>.filterNot(predicate: (T) -> Boolean): List<T>
 *      过滤不满足条件的元素组成的子集合。
 *  17.2.2.2 划分
 *      Iterable<T>.partition(predicate: (T) -> Boolean): Pair<List<T>, List<T>>
 *      根据指定的过滤条件将集合划分为符合过滤条件的子集合list1和不符合条件的子集合list2，并组成Pair对象，
 * 其中key为list1，value为list2.
 *  17.2.2.3 检验谓词
 *      有些扩展函数只是对针对集合中的元素简单地做检验一次谓词。
 *      Iterable<T>.any(predicate: (T) -> Boolean): Boolean
 *      如果集合中有一个元素符合条件则返回true，否则返回false。
 *      Iterable<T>.none(predicate: (T) -> Boolean): Boolean
 *      如果集合中的元素都满足条件，则返回false，否则返回true。
 *      Iterable<T>.all(predicate: (T) -> Boolean): Boolean
 *      如果集合中的元素都满足条件，则返回true，否则返回false。一个空集合调用all扩展函数始终返回true。
 *      any和none也可以不带谓词，在这种情况下它们用来检测集合是否为空。
 *  17.2.3 plus和minus操作符
 *      Kotlin为集合定义了加减操作符，返回值是一个只读集合。
 *
 *
 *
 *
 *
 *
 *
 *
 *      ，
 *
 *
 *
 *
 *
 *
 *
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
    println("-----------映射转换----------")
    val numberList1 = listOf<Int>(1, 2, 3, 4, 5)
    val mapNumberList1 = numberList1.map { it }
    val mapNumberList2 = numberList1.map { it * it }
    println(mapNumberList1)
    println(mapNumberList2)
    val mapNumberList3 = numberList1.mapIndexed { index, it -> index * it }
    println(numberList1)
    println(mapNumberList3)
    val charListNull = listOf<String?>("a", "b", null, "x", "y", null)
    val charList1 = listOf<String?>("a", "b", "c", "d")
    val mapCharList1 = charListNull.mapNotNull { it }
    val flatMapNumber4 = numberList1.flatMap { it -> listOf(it + 1, it + 2, it + 3) }
    println(flatMapNumber4)
    val mapCharList2 = charList1.map { it -> listOf(it + 1, it + 2, it + 3) }
    println(mapCharList2)
    val flattenMapCharList3 = charList1.map { it -> listOf(it + 1, it + 2, it + 3) }.flatten()
    println(flattenMapCharList3)
    println("-----------------合拢转换--------------------")
    val colors = listOf<String>("Red", "Borown", "Grey", "White")
    val animals = listOf<String>("Fox", "Bear", "Wolf", "Rabit")
    val zipColorAnimals = colors.zip(animals)
    println(zipColorAnimals)
    val zipColorAnimals2 = colors.zip(animals) { t1, t2 -> "$t1 $t2" }
    println(zipColorAnimals2)
    var countryPairs = listOf<Pair<String, String>>(
        Pair("asin", "china"), Pair("north america", "america"), Pair("europe", "england")
    )
    println(countryPairs.unzip())
    println("-------------------关联转换-----------------")
    println(colors.associateWith { it.first().toUpperCase() })
    println(colors.associateBy { it.first().toUpperCase() })
    println(colors.associateBy({ it.first().toUpperCase()}, { it.length}))
    println(colors.associate { Pair(it.first().toUpperCase(), it) })
    println("------------打平转换-------------")
    val numbers2 = listOf(setOf(1, 2, 3), setOf(4, 5), setOf(6, 7, 8))
    println(numbers2.flatten())
    println("-------------------字符串表示转换--------------------")
    println(animals.toString())
    println(animals.joinToString())
    println(animals.joinToString(separator = ",", prefix = "start:", postfix = ":end"))
    val numbers3 = (1..100).toList()
    println(numbers3.joinToString(limit = 10, truncated = "..."))
    println(animals.joinToString { it.toUpperCase() })
    val resultNumberStr = StringBuilder("the list of numbers:")
    (1..100).toList().joinTo(resultNumberStr, separator = ",", prefix = "[", postfix = "]",
        limit = 10, truncated = "...", { "${it + 100}" })
    println(resultNumberStr)
    println("-----------------过滤操作-----------")
    println(colors.filter { it.length > 3 })
    println(colors.filterIndexed { index, it -> (index > 0 && it.length > 2) })
    println(colors.filterNot { it.length > 3 })
    println(colors.partition { it.length > 4 })
    println(colors.any { it.length > 3 })
    println(colors.none { it.length > 3})
    println(colors.all { it.length > 4 })
    println(listOf<String>().any())
    println(listOf<String>().none())
    println("------------加减操作符-------------")
    println(colors + "Black")
    println(colors - "White")


}


