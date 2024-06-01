package cn.quickweather.android.common.util

import android.util.Log
import cn.quickweather.android.common.BuildConfig


/**
 * Priority constant for the println method; use Log.v.
 */
private const val VERBOSE = 2

/**
 * Priority constant for the println method; use Log.d.
 */
private const val DEBUG = 3

/**
 * Priority constant for the println method; use Log.i.
 */
private const val INFO = 4

/**
 * Priority constant for the println method; use Log.w.
 */
private const val WARN = 5

/**
 * Priority constant for the println method; use Log.e.
 */
private const val ERROR = 6

/**
 * Priority constant for the println method.
 */
private const val ASSERT = 7

/**
 * Send a [.VERBOSE] log message.
 * @param tag Used to identify the source of a log message.  It usually identifies
 * the class or activity where the log call occurs.
 * @param msg The message you would like logged.
 */
fun logV(tag: String, msg: String): Int {
    if (!BuildConfig.DEBUG) {
        return 0
    }
    return Log.v(tag, msg)
}

/**
 * Send a [.VERBOSE] log message and log the exception.
 * @param tag Used to identify the source of a log message.  It usually identifies
 * the class or activity where the log call occurs.
 * @param msg The message you would like logged.
 * @param tr An exception to log
 */
fun logV(tag: String, msg: String, tr: Throwable?): Int {
    if (!BuildConfig.DEBUG) {
        return 0
    }
    return Log.v(tag, msg, tr)
}

/**
 * Send a [.DEBUG] log message.
 * @param tag Used to identify the source of a log message.  It usually identifies
 * the class or activity where the log call occurs.
 * @param msg The message you would like logged.
 */
fun logD(tag: String?, msg: String?): Int {
    if (!BuildConfig.DEBUG) {
        return 0
    }
    return Log.d(tag, msg!!)
}

/**
 * Send a [.DEBUG] log message and log the exception.
 * @param tag Used to identify the source of a log message.  It usually identifies
 * the class or activity where the log call occurs.
 * @param msg The message you would like logged.
 * @param tr An exception to log
 */
fun logD(tag: String?, msg: String?, tr: Throwable?): Int {
    if (!BuildConfig.DEBUG) {
        return 0
    }
    return Log.d(tag, msg, tr)
}

/**
 * Send an [.INFO] log message.
 * @param tag Used to identify the source of a log message.  It usually identifies
 * the class or activity where the log call occurs.
 * @param msg The message you would like logged.
 */
fun logI(tag: String?, msg: String?): Int {
    return Log.i(tag, msg!!)
}

/**
 * Send a [.INFO] log message and log the exception.
 * @param tag Used to identify the source of a log message.  It usually identifies
 * the class or activity where the log call occurs.
 * @param msg The message you would like logged.
 * @param tr An exception to log
 */
fun logI(tag: String?, msg: String?, tr: Throwable?): Int {
    return Log.i(tag, msg, tr)
}

/**
 * Send a [.WARN] log message.
 * @param tag Used to identify the source of a log message.  It usually identifies
 * the class or activity where the log call occurs.
 * @param msg The message you would like logged.
 */
fun logW(tag: String?, msg: String?): Int {
    return Log.w(tag, msg!!)
}

/**
 * Send a [.WARN] log message and log the exception.
 * @param tag Used to identify the source of a log message.  It usually identifies
 * the class or activity where the log call occurs.
 * @param msg The message you would like logged.
 * @param tr An exception to log
 */
fun logW(tag: String?, msg: String?, tr: Throwable?): Int {
    return Log.w(tag, msg, tr)
}

/*
 * Send a {@link #WARN} log message and log the exception.
 * @param tag Used to identify the source of a log message.  It usually identifies
 *        the class or activity where the log call occurs.
 * @param tr An exception to log
 */
fun logW(tag: String?, tr: Throwable?): Int {
    return Log.w(tag, tr)
}

/**
 * Send an [.ERROR] log message.
 * @param tag Used to identify the source of a log message.  It usually identifies
 * the class or activity where the log call occurs.
 * @param msg The message you would like logged.
 */
fun logE(tag: String?, msg: String): Int {
    return Log.e(tag, msg)
}

/**
 * Send a [.ERROR] log message and log the exception.
 * @param tag Used to identify the source of a log message.  It usually identifies
 * the class or activity where the log call occurs.
 * @param msg The message you would like logged.
 * @param tr An exception to log
 */
fun logE(tag: String?, msg: String?, tr: Throwable?): Int {
    return Log.e(tag, msg, tr)
}

fun logE(tag: String?, tr: Throwable?): Int {
    return Log.e(tag, "", tr)
}

fun safeAssert(tag: String?, msg: String, tr: Throwable? = null) {
    Log.e(tag, msg, tr)
    if (BuildConfig.DEBUG) {
        throw RuntimeException("$tag $msg")
    }
}
