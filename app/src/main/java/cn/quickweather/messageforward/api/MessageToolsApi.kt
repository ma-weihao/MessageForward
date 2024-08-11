package cn.quickweather.messageforward.api

import cn.quickweather.android.common.network.OkHttpInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.lang.Exception

/**
 * Created by maweihao on 8/11/24
 */
interface MessageToolsApi {

    @POST("isimportant")
    suspend fun isMsgImportant(@Body req: WritingReq): WritingToolsResponse<IsImportantResult>
}

private const val baseUrl = "https://writingtools-hk-jgvsuzcgqo.cn-hongkong.fcapp.run/"

class MessageToolsApiImpl: MessageToolsApi {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder().apply {
            addInterceptor(OkHttpInterceptor())
        }.build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .client(client)
            .build()
    }

    private val instance: MessageToolsApi by lazy {
        retrofit.create(MessageToolsApi::class.java)
    }

    override suspend fun isMsgImportant(req: WritingReq): WritingToolsResponse<IsImportantResult> = withContext(Dispatchers.IO) {
        return@withContext try {
            instance.isMsgImportant(req)
        } catch (e: Exception) {
            WritingToolsResponse.internalError(e.message)
        }
    }

}
