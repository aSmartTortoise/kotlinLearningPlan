package com.kotlin.kt11

fun main() {
    val player: Player = Player()
    player.play("http://ws.stream.qqmusic.qq.com/C2000012Ppbd3hjGOK.m4a", 0L)
    player.pause()
    player.resume()
    player.seekTo(30000L)
    player.stop()
}