package cn.quickweather.messageforward.setting

import android.R.attr.phoneNumber
import kotlinx.serialization.Serializable
import java.util.regex.Pattern


@Serializable
data class SettingData(
    val enabled: Boolean = false,
    val smsToNumber: String? = null,
    val onlyVerificationCode: Boolean = false,
)

private val CHINA_PHONE_NUMBER_PATTERN = Pattern.compile("^1[3-9]\\d{9}$")

val SettingData.phoneNumberValid: Boolean
    get() = smsToNumber.phoneNumberValid

val String?.phoneNumberValid: Boolean
    get() {
        if (this.isNullOrBlank()) return false
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