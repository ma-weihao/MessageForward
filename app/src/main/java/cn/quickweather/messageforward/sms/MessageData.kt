package cn.quickweather.messageforward.sms

import android.telephony.SmsMessage
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Created by maweihao on 6/1/24
 */
@Serializable
data class MessageData(
    val originatingAddress: String?,
    val msgBody: String?,
    val receivedTime: Long = 0,
    val splitPartsSize: Int = 0,
    val id: String = "",
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
        splitPartsSize = size,
        id = UUID.randomUUID().toString(),
    )
}