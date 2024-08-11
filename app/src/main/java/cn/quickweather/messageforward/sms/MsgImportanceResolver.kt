package cn.quickweather.messageforward.sms

import cn.quickweather.messageforward.api.MessageToolsApi
import cn.quickweather.messageforward.api.WritingReq
import cn.quickweather.messageforward.api.isSuccess


/**
 * Created by maweihao on 5/29/24
 */
class MsgImportanceResolver(
    private val messageToolsApi: MessageToolsApi,
) {

    private fun containsVerificationCode(content: String?): Boolean {
        if (content.isNullOrBlank()) return false
        val matched = keywords.any {
            content.contains(it, true)
        }
        return matched
    }

    suspend fun isMessageImportant(content: String?): Boolean {
        if (content.isNullOrBlank()) return false
        if (containsVerificationCode(content)) {
            return true
        }
        val response = messageToolsApi.isMsgImportant(WritingReq(content))
        if (!response.isSuccess) {
            return false
        }
        return response.data?.res ?: false
    }

    companion object {
        private val keywords = listOf("验证码", "动态密码", "verification", "code", "代码", "인증")
    }
}