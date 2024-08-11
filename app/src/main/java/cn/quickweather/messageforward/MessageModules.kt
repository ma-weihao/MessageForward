package cn.quickweather.messageforward

import cn.quickweather.messageforward.api.MessageToolsApi
import cn.quickweather.messageforward.api.MessageToolsApiImpl
import cn.quickweather.messageforward.sms.SmsForwardManager
import cn.quickweather.messageforward.sms.MsgImportanceResolver
import cn.quickweather.messageforward.setting.SettingDataStore
import cn.quickweather.messageforward.setting.SettingViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Created by maweihao on 6/1/24
 */
val messageModules = module {
    singleOf(::SmsForwardManager)
    singleOf(::MsgImportanceResolver)
    singleOf(::SettingDataStore)
    viewModelOf(::SettingViewModel)
    singleOf<MessageToolsApi>(::MessageToolsApiImpl)
}