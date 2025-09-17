package com.kotlin.coroutine.practise

import kotlinx.coroutines.*

class Token

class Item

class Post

suspend fun requestToken(): Token {
    println("requestToken")
    return Token()
}

suspend fun createPost(token: Token, item: Item): Post {
    println("createPost")
    return Post()
}

fun processPost(post: Post) {
    println("postItem")
}

@OptIn(DelicateCoroutinesApi::class)
fun postItem(item: Item) {
    runBlocking {
//        val token = requestToken()
//        val post = createPost(token, item)
//        processPost(post)
        val job = launch(context = Dispatchers.Unconfined, start = CoroutineStart.LAZY) {
            println("run.")

        }
        job.start()
    }
}

fun main() {
    postItem(Item())
}