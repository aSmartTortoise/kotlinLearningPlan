import kotlinx.coroutines.*

fun main(args: Array<String>) = runBlocking<Unit> {
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