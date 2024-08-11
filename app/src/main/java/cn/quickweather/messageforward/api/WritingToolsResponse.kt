package cn.quickweather.messageforward.api

/**
 * Created by maweihao on 8/2/24
 */
data class WritingToolsResponse<T> (
    val code: Int,
    val data: T?,
    val errMsg: String?,
) {
    companion object {

        const val CODE_SUCCESS = 0
        const val CODE_ERROR = 1

        fun <T> internalError(errMsg: String?): WritingToolsResponse<T> {
            return WritingToolsResponse(
                code = CODE_ERROR,
                data = null,
                errMsg = errMsg,
            )
        }
    }
}

val WritingToolsResponse<*>.isSuccess: Boolean
    get() = code == WritingToolsResponse.CODE_SUCCESS && data != null