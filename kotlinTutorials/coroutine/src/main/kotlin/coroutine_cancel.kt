import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 *  https://www.jianshu.com/p/2857993af646
 */
fun main(args: Array<String>): Unit = runBlocking {
    val job1 = launch(Dispatchers.Default) {
        repeat(5) {
            println("job1 sleep ${it + 1} times")
            // job1.cancel之后，协程的状态是Cancelled，delay会坚持协程（Job）的状态，从而不再继续执行下去。
            delay(500)
        }
    }
    delay(700)
    job1.cancel()
    val job2 = launch(Dispatchers.Default) {
        var nextPrintTime = 0L
        var i = 1
        // job2.cancel调用之后，没有判断协程（Job）的状态，所以循环体的代码仍然会执行，
        // 可以使用isActive获取协程的状态来作为条件。
        while (i <= 5) {
            val currentTime = System.currentTimeMillis()
            if (currentTime >= nextPrintTime) {
                println("job2 sleep ${i++} ...")
                nextPrintTime = currentTime + 500L
            }
        }
    }
    delay(700)
    job2.cancel()
}
