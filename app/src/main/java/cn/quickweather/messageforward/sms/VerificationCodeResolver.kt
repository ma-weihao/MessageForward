package cn.quickweather.messageforward.sms


/**
 * Created by maweihao on 5/29/24
 */
class VerificationCodeResolver {

    fun containsVerificationCode(content: String?): Boolean {
        if (content.isNullOrBlank()) return false
        return true
    }
}