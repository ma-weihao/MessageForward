package cn.quickweather.android.common.util

import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import cn.quickweather.android.common.BuildConfig

/**
 * Created by maweihao on 5/24/24
 */
fun showShortToast(@StringRes res: Int) {
    showShortToast(res.toResString())
}

fun showDebugToast(content: String?) {
    if (BuildConfig.DEBUG) {
        showShortToast(content)
    }
}

fun showShortToast(content: String?) {
    if (content.isNullOrBlank()) return
    logI("showShortToast", content)
    if (Looper.getMainLooper() == Looper.myLooper()) {
        Toast.makeText(applicationContext, content, Toast.LENGTH_SHORT).show()
    } else {
        TaskThread.postMain {
            Toast.makeText(applicationContext, content, Toast.LENGTH_SHORT).show()
        }
    }
}