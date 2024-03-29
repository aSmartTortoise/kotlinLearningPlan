package com.kotlin.kt06

/**
 *  8 数据类
 *      我们经常创建一些只保存数据的类。在这些类中，一些标准的函数是又数据机械推导出来的。
 *      这样的类叫做数据类。
 *      8.1 数据类具有以下特点
 *          (1)主构造函数至少有一个参数，且这些参数是该类的属性。
 *          （2）数据类不能是抽象、开放、密封或者内部类。
 *      8.2 在jvm中，如果生成的类需要有一个无参的构造函数，则说有的属性需要指定默认值。
 *   9 密封类
 *      密封类用来表示受限的类继承结构：当一个值为有限的几种类型，而不能有其他类型。在某种
 *      意义上它们时枚举类的扩展：枚举类型的值集合是受限的，但每个枚举常量只存在一个实例，
 *      而密封类的一个子类可以有可包含状态的多个实例。
 *      9.1 枚举类用sealed修饰符声明。密封类的直接子类必须在于该密封类的同一个文件中声明。
 *      9.2 密封类是自身抽象的，它不能直接实例化，可以有抽象成员。
 *      9.3 密封类的构造函数的可见性是private。
 *      9.4 扩展密封类的子类的类可以声明在任何位置，而无需和密封类在同一个文件中。
 *  10 泛型
 *      10.1 型变
 *          10.1.1 声明处型变
 *              我们可以标注Source的类型参数T，使它只能从Source<T>的成员中返回-生产，
 *              不能作为任意成员方法中的参数-消费，这样我们可以用out修饰符标记该类型参数。
 *              一般地，当一个类C的类型参数声明为out时，它就只能出现在类C的成员的输出位置
 *              这样的类的一个好处是Class<Base>可以完全地作为Class<Derived>的超类。
 *              这样C在参数类型T上是协变的，或者说T是一个协变类型参数，可以认为C是T的生产者
 *              不是T的消费者。
 *              out被称为型变注解，并且由于它在类型参数的声明处提供，所以这种型变为声明处
 *              型变。与out相对应的型变注解是in，它使得一个类型参数逆变：只可以被C消费，而
 *              不能被C所生产。
 *      10.2 类型投影
 *          可以在一个方法中的参数列表中的一个参数的类型参数使用型变注解out、in，来限制该参数
 *          可以使用的方法，如标注为out，则在此方法中只能使用该参数的只生产该类型参数的方法。
 *          这样可以保证在该方法中不会出现异常。
 *          这就是类型投影。如下面例子的copy方法，from是一个受限制的数组，我们只可以调用生产
 *          类型参数Int的方法，这就是使用处型变。
 *      10.3 泛型函数
 *          泛型函数的类型参数在函数名称之前。
 *      10.4 泛型约束
 *          能够替换给定类型参数的所有类型的集合可以由泛型约束限制。
 *  11 嵌套类与内部类
 *      可以在类中嵌套类，嵌套接口；可以在接口中嵌套类、嵌套接口
 *      11.1 标记为inner修饰符的嵌套类被称为内部类，内部类可以访问外部类的成员，并持有一个
 *          外部类对象的引用。
 *  12 枚举类
 *      每个枚举常量都是一个对象。
 *      枚举常量还可以声明其带有相应的函数以及覆盖基类成员函数的匿名类。
 *      如果枚举类定义其他成员，那么使用分号将其与定义的枚举常量分割开来。
 *      枚举类可以实现接口（但是不能从类继承）。
 *      12.1 使用枚举常量
 *          枚举类中有合成方法获取定义的枚举常量列表和通过名称获取枚举常量。
 *          如果枚举类的名称为EnumClss，
 *          那么EnumClass.valuesOf()---获取定义的枚举常量的列表；
 *          EnumClass.valueOf(str: String)--获取指定名称的枚举常量；
 *          valueOf(str: String)如果指定的名称与定义中的枚举常量均不符合则会抛出
 *          IllegalArgumentException。
 *      枚举类常量有定义在枚举类名字和位置的属性:name, ordinal。枚举类常量还实现了
 *      Comparable接口，其中自然顺序是它们在类中定义的顺序。
 *
 *
 *
 *
 *      
 *
 **/
/**
 * 声明处型变
 */
interface Source<out T> {
    fun nextT(): T
}

fun demo(str: Source<String>) {
    val any: Source<Any> = str
}

/**
 * 使用处型变
 */
fun copy(from: Array<out Int>, to: Array<Any>) {
    assert(from.size == to.size)

    for (i in from.indices) {
        to[i] = from[i]
    }

//    from.set(0, 1); 使用消费类型参数T的方法会编译错误
}

class Outer {
    private val bar: Int = 1
    inner class Inner {
        fun foo() = bar
    }
}

enum class ProtocolState {
    WAITTING {
        override fun signal() = TALKING

        fun howWait() {

        }
    },
    TALKING {
        override fun signal() = WAITTING
    };

    abstract fun signal(): ProtocolState
}

fun main(args: Array<String>) {
    println(Outer().Inner().foo())

    for (it in ProtocolState.values()) {
        println("$it name is ${it.name} and ordinal is ${it.ordinal}")
    }

    try {
        ProtocolState.valueOf("READING")
    } catch (e: Exception) {
        println(e)
    }
}