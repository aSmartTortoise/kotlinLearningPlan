package com.kotlin.kt06

/**
 *  Kotlin泛型中的in、out
 *  https://segmentfault.com/a/1190000022679495
 *
 *  out声明称之为协变，可以兼容自身及其子类，相当于Java中的  ? extend E
 *  in声明称之为逆协变，可以兼容自身及其父类，相当于Java中的 ? super E
 *  与Java中的 ? extengd E和? super E只支持在方法中声明 不同，Kotlin中的in、out可以
 *  在类和方法上进行声明。
 */
/**
 *  泛型消费者
 */
class MyList<in T> {
    fun add(t: T): Unit {

    }
}

/**
 *  泛型生产者 只出不进
 */

private class MyList2<out T> {

}



fun main() {
    // Kotlin中父类型集合的对象可以赋值给子类型结合的引用，通过使用泛型标识符in
    //它表明被in修饰的类是这个泛型的消费者，只进不出。
    var charSequences: MyList<CharSequence> = MyList()
    var strings: MyList<String> = charSequences
    charSequences.add("Jack")
    //通过在泛型中使用out修饰符，可以将子类型的集合对象赋值给父类型集合的引用，
    //它表明该类是泛型的生产者，只出不进。
    var ints: MyList2<Int> = MyList2()
    var anys: MyList2<Any> = ints

}