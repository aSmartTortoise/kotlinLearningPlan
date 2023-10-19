
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

