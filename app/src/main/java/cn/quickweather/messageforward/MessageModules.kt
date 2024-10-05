package cn.quickweather.messageforward

import cn.quickweather.android.common.util.globalMainScope
import cn.quickweather.messageforward.api.MessageToolsApi
import cn.quickweather.messageforward.api.MessageToolsApiImpl
import cn.quickweather.messageforward.sms.SmsForwardManager
import cn.quickweather.messageforward.history.ForwardHistoryDataStore
import cn.quickweather.messageforward.sms.MsgImportanceResolver
import cn.quickweather.messageforward.setting.SettingDataStore
import cn.quickweather.messageforward.setting.SettingViewModel
import cn.quickweather.messageforward.service.LowBatteryHandler
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Created by maweihao on 6/1/24
 */
val messageModules = module {
    single {
        SmsForwardManager(
            get(),
            get(),
            get(),
            globalMainScope,
        )
    }
    singleOf(::MsgImportanceResolver)
    singleOf(::SettingDataStore)
    singleOf(::ForwardHistoryDataStore)
    viewModelOf(::SettingViewModel)
    singleOf<MessageToolsApi>(::MessageToolsApiImpl)
    factoryOf(::LowBatteryHandler)
}