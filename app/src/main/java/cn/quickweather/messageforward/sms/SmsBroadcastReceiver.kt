package cn.quickweather.messageforward.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import cn.quickweather.android.common.util.logI
import org.koin.core.component.KoinComponent
import org.koin.java.KoinJavaComponent.inject

class SmsBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val manager: SmsForwardManager by inject(SmsForwardManager::class.java)

    override fun onReceive(context: Context, intent: Intent) {
        val data = intent.extras

        // creating an object on below line.
        val pdus = data!!["pdus"] as? Array<Any> ?: return
        val format = data.getString("format")

        val messageData = pdus.map {
            SmsMessage.createFromPdu(it as ByteArray, format)
        }.toMessageData()
        logI(TAG, "$messageData")
        manager.onNewSmsReceived(messageData)
    }
}
private const val TAG = "SmsBroadcastReceiver"