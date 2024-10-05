package cn.quickweather.messageforward.history

import cn.quickweather.messageforward.sms.MessageData
import kotlinx.serialization.Serializable

@Serializable
data class HistoryData(
    val message: MessageData,
    // reference to [ForwardStatus]
    val status: Int,
) {
    val id: String
        get() = message.id
}
