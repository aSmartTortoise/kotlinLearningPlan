package com.kotlin.kt11

import kotlin.properties.Delegates

class Player {
    private var state: State by Delegates.observable(State.IDLE, { prop, old, new ->
        println("$old -> $new")
        onPlayerStateChangedListener?.onStateChanged(old, new)
    })

    private fun sendCmd(cmd: PlayerCmd) {
        when (cmd) {
            is PlayerCmd.Play -> {
                println("\nPlay ${cmd.url} from ${cmd.position}ms")
                state = State.PLAYING
                doPlay(cmd.url, cmd.position)
            }
            is PlayerCmd.Resume -> {
                println("\nResume. ")
                state = State.PLAYING
                doResume()
            }
            is PlayerCmd.Pause -> {
                println("\nPause. ")
                state = State.PAUSED
                doPause()
            }
            is PlayerCmd.Stop -> {
                println("\nStop.")
                state = State.IDLE
                doStop()
            }
            is PlayerCmd.Seek -> {
                println("\nSeek to ${cmd.position}ms, state: $state")
            }
        }
    }

    private fun doPlay(url: String, position: Long) {
        println("url: $url  position: $position")
    }

    private fun doResume(){
        println("onResume")
    }

    private fun doPause() {
        println("doPause")
    }

    private fun doStop() {
        println("doStop")
    }

    //region api
    interface OnPlayerStateChangedListener {
        fun onStateChanged(oldState: State, new: State)
    }

    var onPlayerStateChangedListener: OnPlayerStateChangedListener? = null

    fun play(url: String, position: Long = 0) {
        sendCmd(PlayerCmd.Play(url, position))
    }

    fun resume() {
        sendCmd(PlayerCmd.Resume)
    }

    fun pause() {
        sendCmd(PlayerCmd.Pause)
    }

    fun stop() {
        sendCmd(PlayerCmd.Stop)
    }

    fun seekTo(position: Long) {
        sendCmd(PlayerCmd.Seek(position))
    }
}