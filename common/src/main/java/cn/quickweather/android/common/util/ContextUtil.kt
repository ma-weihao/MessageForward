package cn.quickweather.android.common.util

import android.content.Context
import cn.quickweather.android.common.app.BaseApplication
import kotlinx.coroutines.CoroutineScope

/**
 * Created by maweihao on 5/24/24
 */

var appApplicationContext: Context? = null

val applicationContext: Context by lazyUnsafe {
    appApplicationContext ?: BaseApplication.application
}

val globalMainScope: CoroutineScope
    get() {
        return applicationContext as BaseApplication
    }

