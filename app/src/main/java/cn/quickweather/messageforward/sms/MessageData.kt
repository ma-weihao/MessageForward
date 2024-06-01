package cn.quickweather.messageforward.sms

import android.telephony.SmsMessage

/**
 * Created by maweihao on 6/1/24
 */
data class MessageData(
    val originatingAddress: String?,
    val msgBody: String?,
    val receivedTime: Long = 0,
    val splitPartsSize: Int = 0,
)

fun List<SmsMessage>.toMessageData(): MessageData{
    return MessageData(
        originatingAddress = get(0).originatingAddress,
        msgBody = map {
            it.messageBody
        }.reduce { acc, smsMessage ->
            acc + smsMessage
        },
        receivedTime = get(0).timestampMillis,
        splitPartsSize = size
    )
}