package com.kotlin.wanandroid.timemonitor

import android.util.Log

class TimeMonitor constructor(var monitorId: Int = -1){
    companion object {
        const val TAG = "TimeMonitor"
    }

    private var startTime: Long = 0L
    var timeTag: HashMap<String, Long> = hashMapOf()

    fun startMonitor() {
        if (timeTag.size > 0) {
            timeTag.clear()
        }

        startTime = System.currentTimeMillis()
    }

    fun recordingTimeTag(tag: String) {
        if (timeTag.get(tag) != null) {
            timeTag.remove(tag)
        }

        val time = System.currentTimeMillis() - startTime
        Log.d(TAG, "recordingTimeTag: $tag : $time")
        timeTag.put(tag, time)
    }

    fun end(tag: String, writeLog: Boolean) {
        recordingTimeTag(tag)
        end(writeLog)
    }

    fun end(writeLog: Boolean) {
        if (writeLog) {

        }
    }
}