package cn.quickweather.android.common.network

import android.os.SystemClock
import cn.quickweather.android.common.BuildConfig
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.charset.UnsupportedCharsetException

import cn.quickweather.android.common.util.logV

class OkHttpInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val requestBody: RequestBody? = request.body
        var body: String? = null
        if (requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            var charset: Charset = UTF8
            val contentType: MediaType? = requestBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8) ?: UTF8
            }
            body = buffer.readString(charset)
        }
        if (BuildConfig.DEBUG) logV(TAG,
            "HTTP REQUEST: method: ${request.method}, url: ${request.url}, head: ${request.headers}, param : $body"
        )

        val startMs = SystemClock.elapsedRealtime()
        val response: Response = chain.proceed(request)
        val tookMs = SystemClock.elapsedRealtime() - startMs

        val responseBody: ResponseBody? = response.body
        val rBody: String

        val source: BufferedSource = responseBody!!.source()
        source.request(Long.MAX_VALUE)
        val buffer = source.buffer

        var charset: Charset = UTF8
        val contentType: MediaType? = responseBody.contentType()
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8) ?: UTF8
            } catch (e: UnsupportedCharsetException) {
                e.printStackTrace()
            }
        }
        rBody = buffer.clone().readString(charset)

        if (BuildConfig.DEBUG) logV(TAG,
            "HTTP RESPONSE: code: ${response.code}, cost: ${tookMs}ms, url: ${response.request.url}, body: $body, Response: $rBody"
        )

        return response
    }
}

private val TAG = OkHttpInterceptor::class.java.simpleName
private val UTF8: Charset = StandardCharsets.UTF_8