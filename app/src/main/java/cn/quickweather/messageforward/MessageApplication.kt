package cn.quickweather.messageforward

import cn.quickweather.android.common.app.BaseApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Created by maweihao on 5/20/24
 */
class MessageApplication: BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MessageApplication)
            modules(messageModules)
        }
    }
}