package com.kotlin.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce

/**
 *  18.8 通道
 *      和队列不同，一个通道可以通过被关闭来表明没有更多的元素进入通道。
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
fun main() {
//    channelStudy01()
//    channelStudy02Close()
//    channelStudy03ProduceConsume()
//    channelStudy04()
    channelStudy05()
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