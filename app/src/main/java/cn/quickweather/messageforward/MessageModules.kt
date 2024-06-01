package cn.quickweather.messageforward

import cn.quickweather.messageforward.sms.SmsForwardManager
import cn.quickweather.messageforward.sms.VerificationCodeResolver
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
    singleOf(::VerificationCodeResolver)
    singleOf(::SettingDataStore)
    viewModelOf(::SettingViewModel)
}