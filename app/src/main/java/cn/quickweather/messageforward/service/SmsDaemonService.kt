package cn.quickweather.messageforward.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cn.quickweather.android.common.util.logI
import cn.quickweather.messageforward.MainActivity
import cn.quickweather.messageforward.R
import cn.quickweather.messageforward.sms.SmsForwardManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SmsDaemonService : Service() {

    private val smsForwardManager: SmsForwardManager by inject()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        logI(TAG, "onCreate")
        running = true
        finishing = false
        super.onCreate()
        createNotificationChannel()
        startForeground(ID_SERVICE, createNotification())
        scope.launch {
            smsForwardManager.settingData.collect {
                if (!it.enabled) {
                    finishing = true
                    stopSelf()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        running = true
        finishing = false
        logI(TAG, "onStartCommand")
        return START_STICKY
    }

    override fun onDestroy() {
        logI(TAG, "onDestroy")
        running = false
        finishing = false
        cancelNotification()
        scope.coroutineContext.cancel()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel.
        val name = "Background Task"
        val descriptionText = "Forward service is on as long as this notification exists"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, OPEN_SETTING_ACTIVITY_ID, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_lightbulb)
            .setContentTitle(getString(R.string.title_daemon_service))
            .setContentText(getString(R.string.content_daemon_service))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        return builder.build()
    }

    private fun cancelNotification() {
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
    }

    companion object {
        private const val TAG = "SmsDaemonService"

        private const val CHANNEL_ID = "BackgroundTask"

        private const val ID_SERVICE = 100

        private const val OPEN_SETTING_ACTIVITY_ID = 200

        private const val NOTIFICATION_ID = 300

        private var running = false
        private var finishing = false

        fun enableService(context: Context, enable: Boolean) {
            Log.i(TAG, "enableService: enable:$enable running:$running finishing:$finishing")
            if (enable && !running && !finishing) {
                val intent = Intent(context, SmsDaemonService::class.java)
                context.startForegroundService(intent)
            }
            if (!enable && !finishing && running) {
                val intent = Intent(context, SmsDaemonService::class.java)
                context.stopService(intent)
            }
        }
    }
}