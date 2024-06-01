package cn.quickweather.android.common.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Created by maweihao on 5/24/24
 */
open class BaseApplication: Application(), CoroutineScope by applicationMainScope() {

    companion object {
        lateinit var application: BaseApplication
    }

    override fun onCreate() {
        application = this
        super.onCreate()
    }
}

private fun applicationMainScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)