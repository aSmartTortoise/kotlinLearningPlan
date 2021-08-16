package com.kotlin.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

/**
 *  18.8 通道
 *      和队列不同，一个通道可以通过被关闭来表明没有更多的元素进入通道。
 *  18.8.1 带缓冲的管道
 *      Channel构造方法和produce构建器通过一个可选的参数capacity来指定缓冲区的大小。缓冲允许通
 *  道的发送者在挂起前发送多个元素。
 *
 *
 */

fun CoroutineScope.produceSquares(): ReceiveChannel<Int> = produce {
    for (i in 1..5) send(i * i)
}

fun CoroutineScope.produceNumbers(): ReceiveChannel<Int> = produce {
    var x = 1
    while (true) send(x++)
}

fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
    for (i in numbers) send(i * i)
}

fun CoroutineScope.produceNumbers02(): ReceiveChannel<Int> = produce {
    var x = 1
    while (true) {
        send(x++)
        delay(100L)
    }
}

fun CoroutineScope.launchProcessors(id: Int, channel: ReceiveChannel<Int>) = launch {
    for (msg in channel) {
        println("processor #$id received $msg")
    }
}

suspend fun sendString(channel: SendChannel<String>, str: String, time: Long) {
    while (true) {
        delay(time)
        channel.send(str)
    }
}

data class Ball(var hits: Int)

suspend fun player(name: String, table: Channel<Ball>) {
    for (ball in table) {
        ball.hits++
        println("$name $ball")
        delay(300L)
        table.send(ball)
    }
}
fun main() {
//    channelStudy01()
//    channelStudy02Close()
//    channelStudy03ProduceConsume()
//    channelStudy04()
//    channelStudy05()
//    channelStudy06()
//    channelStudy07Capacity()
//    channelStudy08()
    channelStudy09TickerChannel()

}

private fun channelStudy09TickerChannel() {
    runBlocking {
        val ticker = ticker(delayMillis = 100L, initialDelayMillis = 0L)
        var nextElement = withTimeoutOrNull(1L) { ticker.receive() }
        println("initial element is $nextElement")
        nextElement = withTimeoutOrNull(50L) { ticker.receive() }
        println("next element in 50ms is $nextElement")
        nextElement = withTimeoutOrNull(60L) { ticker.receive() }
        println("next element in 110ms is $nextElement")
        println("consume pause for 150ms")
        delay(150L)
        nextElement = withTimeoutOrNull(1L) { ticker.receive() }
        println("next element after large consume delay is $nextElement")
        nextElement = withTimeoutOrNull(60L) { ticker.receive() }
        println("next element in 50ms after consume pause in 150ms is $nextElement")
        ticker.cancel()
    }
}

private fun channelStudy08() {
    runBlocking {
        val table = Channel<Ball>()
        launch {
            player("ping", table)
        }
        launch {
            player("pong", table)
        }
        table.send(Ball(0))
        delay(2000L)
        coroutineContext.cancelChildren()
    }
}

private fun channelStudy07Capacity() {
    runBlocking {
        val channel = Channel<Int>(4)
        val job = launch {
            repeat(10) {
                println("send $it")
                channel.send(it)
            }
        }
        delay(1000L)
        job.cancel()
    }
}

/**
 * 多个协程处理通道上游的发送事件
 */
private fun channelStudy06() {
    runBlocking {
        val channel = Channel<String>()
        launch {
            sendString(channel, "foo", 200L)
        }
        launch {
            sendString(channel, "bar", 300L)
        }

        repeat(6) {
            println(channel.receive())
        }
        coroutineContext.cancelChildren()
    }
}

/**
 * 不同的协程处理同一个通道流，
 */
private fun channelStudy05() {
    runBlocking {
        val channel = produceNumbers02()
        repeat(5) {
            launchProcessors(it, channel)
        }
        delay(950L)
        channel.cancel()
    }
}

private fun channelStudy04() {
    runBlocking {
        val numbers = produceNumbers()
        val squares = square(numbers)
        repeat(5) { println(squares.receive()) }
        println("done")
        coroutineContext.cancelChildren()
    }
}

/**
 * produce 通道构建器
 */
private fun channelStudy03ProduceConsume() {
    runBlocking {
        val squares = produceSquares()
        squares.consumeEach { println(it) }
        println("done.")
    }
}

/**
 * 关闭和迭代通道
 */
private fun channelStudy02Close() {
    runBlocking {
        val channel = Channel<Int>()
        launch {
            for (i in 1..5) {
                channel.send(i * i)
            }
            channel.close()
        }

        for (i in channel) println(i)
        println("done")
    }
}

private fun channelStudy01() {
    runBlocking {
        val channel = Channel<Int>()
        launch {
            for (i in 1..5) {
                channel.send(i * i)
            }
        }
        repeat(5) { println(channel.receive()) }
        println("done.")
    }
}