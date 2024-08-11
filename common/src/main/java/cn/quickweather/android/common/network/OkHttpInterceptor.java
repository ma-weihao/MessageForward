package cn.quickweather.android.common.network;

import android.os.SystemClock;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

import cn.quickweather.android.common.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

import static cn.quickweather.android.common.util.LogUtilKt.logV;

/**
 * Created by maweihao on 12/13/20
 */
public class OkHttpInterceptor implements Interceptor {
    private static final String TAG = OkHttpInterceptor.class.getSimpleName();
    private final Charset UTF8 = StandardCharsets.UTF_8;

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody requestBody = request.body();
        String body = null;
        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            body = buffer.readString(charset);
        }
        if (BuildConfig.DEBUG) logV(TAG,
                "HTTP REQUEST: method: " + request.method()
                        + ", url: " + request.url()
                        + ", head: " + request.headers()
                        + ", param : " + body);

        long startMs = SystemClock.elapsedRealtime();
        Response response = chain.proceed(request);
        long tookMs = SystemClock.elapsedRealtime() - startMs;

        ResponseBody responseBody = response.body();
        String rBody;

        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE);
        Buffer buffer = source.buffer();

        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8);
            } catch (UnsupportedCharsetException e) {
                e.printStackTrace();
            }
        }
        rBody = buffer.clone().readString(charset);

        if (BuildConfig.DEBUG) logV(TAG,
                "HTTP RESPONSE: code:" + response.code()
                        + ", cost: " + tookMs
                        + "ms, url：" + response.request().url()
                        + ", body：" + body
                        + ", Response: " + rBody);

        return response;
    }

}
