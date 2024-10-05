package cn.quickweather.messageforward.setting

import kotlinx.serialization.Serializable
import java.util.regex.Pattern


@Serializable
data class SettingData(
    val enabled: Boolean = false,
    val smsToNumber: String? = null,
    val onlyVerificationCode: Boolean = false,
    val markAsRead: Boolean = false,
    val sendBatteryNotification: Boolean = false,
    val lastBatteryNotificationTime: Long = 0,
)

private val CHINA_PHONE_NUMBER_PATTERN = Pattern.compile("^1[3-9]\\d{9}$")

val SettingData.phoneNumberValid: Boolean
    get() = smsToNumber.phoneNumberValid

val String?.phoneNumberValid: Boolean
    get() {
        if (this.isNullOrBlank()) return false
        if (this == "10000" || this == "10086" || this == "10010") {
            return true
        }
        var number = this
        if (this.startsWith("+")) {
            if (!this.startsWith("+86")) {
                return false
            }
            number = number.replaceFirst("+86", "").replace("-", "")
        }
        val matcher = CHINA_PHONE_NUMBER_PATTERN.matcher(number)
        return matcher.matches()
    }