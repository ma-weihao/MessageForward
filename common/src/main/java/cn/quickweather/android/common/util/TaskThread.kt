package cn.quickweather.android.common.util

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper

/**
 * Created by maweihao on 3/13/21
 */
object TaskThread {

    private val mainHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val backgroundHandler: Handler by lazy {
        newHandlerThread("TaskThread-background", HandlerThread.NORM_PRIORITY)
    }

    fun postMain(delay: Long = 0, task: Runnable) {
        if (delay > 0) {
            mainHandler.postDelayed(task, delay)
        } else {
            mainHandler.post(task)
        }
    }

    fun removeMain(task: Runnable) {
        mainHandler.removeCallbacks(task)
    }

    fun postBackground(delay: Long = 0, task: Runnable) {
        if (delay > 0) {
            backgroundHandler.postDelayed(task, delay)
        } else {
            backgroundHandler.post(task)
        }
    }

    fun removeBackground(task: Runnable) {
        backgroundHandler.removeCallbacks(task)
    }

    private fun newHandlerThread(name: String, priority: Int = HandlerThread.NORM_PRIORITY): Handler {
        val ht = HandlerThread(name, priority)
        ht.start()
        return Handler(ht.looper)
    }
}