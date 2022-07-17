package com.kotlin.wanandroid.timemonitor

/**
 *  采用单例管理各个耗时统计的数据。
 *
 *  参考：https://juejin.cn/post/6844903590545326088
 */
class TimeMonitorManager private constructor(){
    private var timeMonitorMap: HashMap<Int, TimeMonitor>? = null

    init {
        timeMonitorMap = hashMapOf()
    }

    companion object {
        private var instance: TimeMonitorManager? = null
            get() {
                if (field == null) {
                    field = TimeMonitorManager()
                }

                return field
            }

        @Synchronized
        fun get(): TimeMonitorManager {
            return instance!!
        }
    }

    fun resetTimeMonitor(id: Int) {
        if (timeMonitorMap?.get(id) != null) {
            timeMonitorMap?.remove(id)
        }
        getTimeMonitor(id).startMonitor()
    }

    fun getTimeMonitor(id: Int): TimeMonitor {
        var timeMonitor = timeMonitorMap?.get(id)
        if (timeMonitor == null) {
            timeMonitor = TimeMonitor(id)
            timeMonitorMap?.put(id, timeMonitor)
        }
        return timeMonitor
    }
}