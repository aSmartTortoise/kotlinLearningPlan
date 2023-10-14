import kotlinx.coroutines.*

fun main(args: Array<String>): Unit = runBlocking {
    launch(Dispatchers.Unconfined) {
        println("${Thread.currentThread().name} : launch start")
        async(Dispatchers.Default) {
            println("${Thread.currentThread().name} : async start")
            delay(100)
            println("${Thread.currentThread().name} : async end")
        }.await()
        println("${Thread.currentThread().name} : launch end")
    }
}