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

        // running for loop to read the sms on below line.
        for (i in pdus.indices) {
            // getting sms message on below line.
            val smsMessage: SmsMessage = SmsMessage.createFromPdu(pdus[i] as ByteArray, format)
            // extracting the sms from sms message and setting it to string on below line.
            val message = ("Sender : " + smsMessage.displayOriginatingAddress
                    ) + " Message: " + smsMessage.messageBody
            // adding the message to listener on below line.
            logI(TAG, message)
        }
    }
}
private const val TAG = "SmsBroadcastReceiver"