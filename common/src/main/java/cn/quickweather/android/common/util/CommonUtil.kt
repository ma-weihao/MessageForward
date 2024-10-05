package cn.quickweather.android.common.util

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.Closeable
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

fun <T> lazyUnsafe(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

fun ifNotNull(vararg obj: Any?, callback: () -> Unit) {
    if (!obj.any{ it == null}) {
        callback.invoke()
    }
}

fun Closeable?.safeClose() {
    this?.let {
        close()
    }
}

fun Any?.toJsonStr(): String {
    return try {
        Gson().toJson(this)
    } catch (ignore: Exception) {
        "PARSE EXCEPTION"
    }
}

infix fun Int?.moreThan(other: Int): Boolean {
    return if (this == null) {
        false
    } else {
        this - other > 0
    }
}

infix fun Long?.moreThan(other: Long): Boolean {
    return if (this == null) {
        false
    } else {
        this - other > 0
    }
}

infix fun Int?.lessThan(other: Int): Boolean {
    return if (this == null) {
        false
    } else {
        this - other < 0
    }
}

infix fun Long?.lessThan(other: Long): Boolean {
    return if (this == null) {
        false
    } else {
        this - other < 0
    }
}

fun <T> deserializeOrNull(text: String?, clazz: Class<T>): T? {
    if (text.isNullOrBlank()) {
        return null
    }
    return try {
        Gson().fromJson(text, clazz)
    } catch (e: JsonSyntaxException) {
        logE("parseObjectOrNull", "parse object error", e)
        null
    }
}

inline fun <reified T> deserializeOrNull(text: String?): T? {
    return deserializeOrNull(text, T::class.java)
}

private val hmFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
fun formatTimeHM(time: Long = System.currentTimeMillis()): String {
    return hmFormat.format(time)
}

fun String.subStringAtMost(maxLen: Int): String {
    if (maxLen <= 0) return ""
    if (length <= maxLen) {
        return this
    }
    return substring(maxLen)
}