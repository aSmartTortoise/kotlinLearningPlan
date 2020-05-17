package com.kotlin.kt11

sealed class PlayerCmd {

    class Play(val url : String, val position: Long) : PlayerCmd()
    class Seek(val position: Long) : PlayerCmd()
    object Pause : PlayerCmd()
    object Resume : PlayerCmd()
    object Stop : PlayerCmd()
}