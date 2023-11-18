

# 15 委托
## 15.1 參考文章
[Kotlin委托](https://www.runoob.com/kotlin/kotlin-delegated.html)
[有关Kotlin属性代理你需要知道的一切](https://juejin.cn/post/6844903683214278670)

委托模式是软件设计中的一项基本技巧。在委托模式中有两个对象处理同一个请求，接收请求的对象将请求委托给另
一个对象处理。Kotlin使用by标识符来实现委托。

委托分为类委托、属性委托。
## 15.2 类委托
类的委托即委托的类中的方法实际上是由受委托的类的对象的方法实现的。

如下方例子中的by子句表示将b保存在Derived的对象内部，编译器会生成接口Base的所有方法，并将调用转发给b。
如果Derived中重写对应的函数，则实际上这个函数的执行不会转发给b。
## 15.2 属性委托
概念：类中的某个属性的值不是在类中定义的，而是委托给一个代理类的对象，从而实现对该类的指定属性的统一管理。
语法：val/var <属性名>: <类型> by <表达式>
by 标识符后的表达式就是委托。该属性的getter和setter函数将委托给指定对象的getValue()和setValue()函数。

Kotlin标准库中为几种有用的委托提供了工厂方法。

## 15.3 延迟属性 Lazy
将属性委托给lazy函数的返回值Lazy接口的实例，该函数接收一个函数类型initializer作为参数，返回一个Lazy接口的实例。
该实例作为延迟属性的委托。当第一次调用属性的getter函数（访问该属性）时，会调用initializer函数，initializer的结果，并记录结果，
结果作为延迟属性的值。后续调用get只是返回记录的结果。

延迟属性是只读的。默认情况下对lazy属性的求值是线程安全的：该值只会在一个线程中计算，并且所有线程会看到相同的值。
如果初始化延迟属性的委托的同步不是必须的，这样多个线程可以同时执行，那么将
LazyThreadSafetyMode.PUBLICATION作为参数传递给lazy()函数。
如果确定初始化工作和属性的使用总是位于同一个线程，那么可以使用LazyThreadSafetyMode.None模式，它不会有
任何线程安全的保证和任何开销。

## 15.4 可观察属性 ObservableProperty
Delegates.observable()函数接受两个参数：第一个是初始化值、第二个是属性值变化事件的回调。把属性赋值
后会执行回调，回调函数有三个参数：被赋值的属性、旧值、新值。

## 15.5 将属性值存储在映射中
一个常见的用例是将属性的值存储在映射中。这经常出现在像解析json或做其他动态事件的应用中。 在这种情况下可以使用
map实例来作为委托实现委托属性。 委托属性的值会从这个map中取值。

## 15.6 NOT NULL
not null使用于那些一个类的对象在初始化阶段尚不能确定某个属性的值的场合，且要求该属性不为null。如果该属性在初始化前
就去访问该属性就会抛出异常。
## 15.7 局部属性委托
可以对方法中的局部变量进行委托。      

## 15.8 属性委托要求
对于只读属性，被委托的类需要提供一个操作符函数getValue。该函数接受两个个参数，第一个thisRef为该属性的接受者类型
或其超类，第二个属性property为KProperty<>类型或其超类。 getValue必须返回该属性的类型或其子类型。

对于可写的属性，还需要额外提供一个操作符函数setValue。该函数接受三个参数。第一个thisRef
为该属性的接受者类型或其超类型；第二个property为KProperty<>类型或其超类型；第三个value
为该属性类型或其超类型。

getValue/setValue函数可以有被委托的类的成员函数或其扩展函数来提供。



# 16 函数
## 16.1 返回Unit的函数
如果一个函数不返回任何有用的值，它的返回类型是Unit。
## 16.2 单表达式函数
当函数的返回值为非Unit时，且函数体中只有一行代码，则可以省略花括号并且在=号之后直接书写代码
接口。

当返回值类型可以由编译推断出类型时，可以省略返回值类型的显示声明。
## 16.3 中缀函数
标有infix关键字的函数可以使用中缀表示法（忽略调用时候的点和圆括号）。中缀函数满足的条件
：该函数需是成员函数或扩展函数；必须只有一个参数；其参数不能有默认值。
## 16.4 高阶函数
概念：是将函数作为参数或返回值的函数。
### 16.4.1 函数类型
http://www.hudroid.cn/kotlin/kotlin-FunctionType/

所有函数类型的形式为有一个圆括号括起来的参数列表和一个返回类型:(A, B) -> C 表示接受类型是
A、B的两个参数和返回一个C类型的函数类型。参数列表可以为空。函数类型可以有一个额外的接受者类型，
例如A.(B) -> C。

函数类型在表示的时候可以选择性地在参数列表中包含参数名，例如(x: Int, y: Int) -> Point 
如需将函数类型指定为可空类型则：((x: Int, y: Int) -> Int)? 函数类型对应的类为FunctionN，
FunctionN中定义了invoke方法，其中N表示函数类型 中的参数的个数。函数类型的实例可以直接
当函数调用-其实质是调用invoke函数。
在kotlin-stdlib-xxx.jar包中的kotlin.jvm.functions路径下
              
函数类型和String、Int类型一样，是一种独立类型。lambda只是实现函数类型实例化的一种方式。

Unit返回类型不能省略。函数类型和普通类型一样，可以被继承。
          
https://juejin.cn/post/7078207132453044238#heading-6
高阶函数在编译为Java代码之后，高阶函数的函数类型参数的数据类型为FunctionN，调用函数的地方
实际参数为匿名内部类。 

将Kotlin代码反编译为Java代码的步骤如下：
Tools-Kotlin-Show Kotlin Bytecode-Decompile

### 16.4.2 函数类型的实例化
(1)使用lambda表达式或匿名函数进行实例化。

(2)使用实现函数类型接口的自定义类的实例。

(3)函数引用及属性引用。https://www.jianshu.com/p/10358883455c
默认情况下，函数声明中的返回值为函数类型是不带接受者的。
### 16.4.3 匿名函数
匿名函数的参数只能在括号内传递，这与普通函数声明时一样的。匿名函数的形式如下：

`val f: (int) -> String = fun(x: Int) {
    return "结果:$x"
}
`

匿名函数是函数类型实例化的一种方式，其实际上对象。

### 16.4.4 lambda表达式
https://www.cnblogs.com/Jetictors/p/8647888.html

lambda表达式的本质其实是匿名函数，底层是通过匿名函数实现的。特点：
              
(1)总是被大括号扩着。
              
(2)参数在->之前声明，参数类型可以省略。
              
(3)函数体在->的后面。
              
it 不是Kotlin中的一个关键字。lambda表达式中的参数只有一个的时候，可以用it来代表此参数。
it可表示为单个参数的隐式名称，是Kotlin语言约定的。

如果一个Lambada表达式中只有一个参数，那么可以省略这个参数和->，用it代表这个参数

https://blog.csdn.net/u011288271/article/details/108385586

在lambda表达式中，使用_表示未使用的参数，表示不处理这个参数。

### 16.4.5 带接收者的函数字面值
在Kotlin中提供了指定的接受者对象调用Lambda表达式的功能。在函数字面值的函数体中，可以调用
接受者对象的方法而无需任何额外的限定符，它允许在函数体中访问接受者对象的成员。

16.4.6 闭包

[学习Javascript闭包（Closure）](https://www.ruanyifeng.com/blog/2009/08/learning_javascript_closures.html)

闭包可以理解为函数内部的函数，闭包可以访问外部函数定义的局部变量。Java是不支持闭包的，Kotlin支持闭包。

Kotlin闭包的形式如下：

Kotlin闭包的形式如下：

```K
private fun foo(): (Int) -> Int {
    val a: Int = 2
    retrun fun (b: Int): Int {
        return a + b
    } 
}

上述代码中

fun (b: Int): Int {
return a + b
}
就是闭包。
```
```K
fun foo2(): Int {
    val a = 2
    
    fun f1(): Int {
        return a + 10
    }
    
    return f1()
}

上述代码中,
    fun f1(): Int {
        return a + 10
    }
就是闭包。
```
### 16.4.5 函数引用
https://juejin.cn/post/6930978099987398670
https://www.jianshu.com/p/10358883455c

由域作用符 :: + 函数名 构成函数引用，实现函数类型的实例化。

具体有 类名::函数名（对象声明中的函数或者伴生对象的函数）
对象引用::函数名（成员函数）； ::函数名（顶层函数、局部函数）

如果在一个lambda中使用函数，而且这个这个函数的参数全是lambda的输入，那么就完全可以使用函数引用
来取代函数调用。

```K
    private fun updateUI(data: Data?) {
        if(data != null){
            applyUiChanges(data)
        }
    }
    private fun applyUiChanges(data: Data) {
        // Do cool stuff in UI
    }
```
如上的updateUI方法可以使用函数引用加以简化，如下：
```K
    private fun updateUI(data: Data?) {
        data?.let {
            ::applyUiChanges
        }
    }
```
### 16.4.5 属性引用
域作用符不仅可以引用函数，也可以引用属性。

## 16.5 内联函数 
https://segmentfault.com/a/1190000038996559

使用高阶函数会带来一些性能上的损失：高阶函数的函数体中有关函数类型的对象带来的内存分配和高阶函数
的虚拟调用会带来运行时间的开销。而将高阶函数内联化后可减少了高阶函数的调用（直接
调用的是函数体）、减少了函数引用的对象的创建。 其原理：在编译期，把调用这个函数的地方用这个函数的方法
体进行替换。

```K
               val ints = intArrayOf(1, 2, 3, 4, 5)
               ints.forEach {
                   println("Hello $it")
               }

               forEach函数的定义为：
               public inline fun forEach(action: (Int) -> Unit): Unit {
                   for(element in this) action(element)
               }
```

forEach函数被inline修饰，它是一个内联函数。
### 16.5.1 内联函数的定义
内联函数的定义很简单，就是用inline修饰的函数。
虽然说内联函数可以减少函数的调用来优化性能，但是并不是每一个函数前加inline修饰就可以优化性能。
我们一般将高阶函数定义为内联函数。inline修饰符影响函数本身和传递给它的Lambda表达式：
所有这些都将内联到调用处。
### 16.5.2 禁用内联
https://www.kotlincn.net/docs/reference/inline-functions.html
如果只希望内联一部分传递给内联函数的函数类型的参数，那么可以使用noinline修饰符修饰不希望内联的
函数类型的参数。
```K
inline fun foo(inlined: () -> Unit, noinline noinlined: () -> Unit): Unit {}
```
可以内联的函数类型的参数，只能在内联函数的内部调用或者作为可以内联的参数传递，但是被noinline修饰
的函数类型的参数，可以以任何我们喜欢的方式操作：存储在字段中、传递它等。
### 16.5.3 非局部返回
https://www.kotlincn.net/docs/reference/inline-functions.html

[Kotlin Lambda详解及非局部返回是啥意思？](https://blog.csdn.net/u011288271/article/details/108385586)

在Kotlin中，我们只能对具名函数或匿名函数使用正常的、非限定的return返回。这意味着要
退出一个Lambda表达式，我们必须使用标签，并且在Lambda表达式内部禁止使用裸return，因为Lambada
表达式不能使包含它的函数返回。

Lambda禁止使用return关键字，但是可以使用限定的返回语法：return@函数名 显示返回一个值，否则
则隐式返回最后一个表达式的值。

一个不带标签的return语句总是在使用fun关键字声明的函数中返回。
内联函数中的Lambda表达式可以使用非限定的return语句，返回的是外部那个调用内联函数的函数，而不是
内联函数本身。这就叫内联函数的Lambda表达式的非局部返回。

内联函数的函数类型，在内联函数调用的时候不希望Lambda表达式中有return全局返回而影响外部函数的执行流程，
可以用crossInline修饰函数类型。
### 16.5.7 公有api内联函数的限制
当一个函数被public或者protected修饰的时候，它就是一个模块级的公有api，可以在其他模块中调用它。
这样对于公有api的内联函数来说，当本模块的的api发生变更时导致其他调用这个内联函数的模块
发生二进制兼容的风险。声明一个内联函数但调用它的模块在它发生改变时并没有重新编译。
为了消除由非公有api变更引起的二进制兼容的风险，公有api的内联函数的函数体内不允许使用非公有
的声明，即不允许使用由private或internal修饰的声明或其他组件。一个internal声明，可以由
@PublishedApi标注，这允许在公有api的内联函数的函数体内部使用该api。当一个internal修饰的
内联函数被标记为@PublishedApi时，它会像公有函数一样检测其函数体。

# 17 相等性
## 参考文章
[相等性](https://legacy.kotlincn.net/docs/reference/equality.html)

Kotlin中有两种类型的相等性：结构相等；引用相等。

## 17.1 结构相等
结构相等由 ==(以及其否定形式!=)来判断。按照惯例像 a==b这样的表达式会翻译成
a?.equals(b) ?: (b===null)

也就是如果a不等于null，则调用equals(Any?)函数，否则检测b引用是否与null相等。
# 17.2 引用相等
引用相等由 ===(以及其否定形式!==)来判断。a===b，当且仅当a和b引用同一个对象返回true，当
基本数据类型比较引用相等是比较的值。

