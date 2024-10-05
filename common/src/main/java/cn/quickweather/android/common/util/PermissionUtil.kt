package cn.quickweather.android.common.util

import android.content.Context

/**
 * Created by maweihao on 10/3/24
 */
fun Context.hasPermission(permission: String): Boolean {
    return checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
}

fun Context.hasPermissions(permissions: List<String>): Boolean {
    return permissions.all { hasPermission(it) }
}

fun Context.hasSmsPermissions(): Boolean {
    return hasPermissions(
        listOf(
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.READ_SMS,
        )
    )
}