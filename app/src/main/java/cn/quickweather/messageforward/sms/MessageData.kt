package cn.quickweather.messageforward.sms

import android.net.Uri
import android.telephony.SmsMessage
import cn.quickweather.android.common.util.applicationContext
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
    val idInSmsDB: Long = -1,
)

fun List<SmsMessage>.toMessageData(): MessageData {
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
        idInSmsDB = getMessageIdFromSms(get(0))
    )
}

private fun getMessageIdFromSms(sms: SmsMessage): Long {
    val smsUri = Uri.parse("content://sms/inbox")

    val cursor = applicationContext.contentResolver.query(
        smsUri, arrayOf("_id"), "address=?",
        arrayOf(sms.originatingAddress), "date DESC"
    )

    val messageId = if (cursor != null && cursor.moveToFirst()) {
        cursor.getLong(cursor.getColumnIndexOrThrow("_id"))
    } else {
        -1
    }

    cursor?.close()
    return messageId
}