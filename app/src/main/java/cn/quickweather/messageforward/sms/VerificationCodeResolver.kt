package cn.quickweather.messageforward.sms


/**
 * Created by maweihao on 5/29/24
 */
class VerificationCodeResolver {

    fun containsVerificationCode(content: String?): Boolean {
        if (content.isNullOrBlank()) return false
        val matched = keywords.any {
            content.contains(it, true)
        }
        return matched
    }

    companion object {
        private val keywords = listOf("验证码", "动态密码", "verification", "code", "代码", "인증")
    }
}