package cn.quickweather.messageforward.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import cn.quickweather.android.common.util.logI
import cn.quickweather.messageforward.sms.SmsForwardManager
import org.koin.android.ext.android.inject

class SmsDaemonService : Service() {

    private val smsForwardManager: SmsForwardManager by inject()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        logI(TAG, "onCreate")
        super.onCreate()
//        startForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logI(TAG, "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        logI(TAG, "onDestroy")
        super.onDestroy()
    }

    companion object {
        private const val TAG = "SmsDaemonService"

        private const val ID = 100
        fun enableService(context: Context, enable: Boolean) {

        }
    }
}