package cn.quickweather.messageforward.sms

import cn.quickweather.messageforward.setting.SettingDataStore


/**
 * Created by maweihao on 5/21/24
 */
class SmsForwardManager(
    settingDataStore: SettingDataStore,
    verificationCodeResolver: VerificationCodeResolver,
) {

    private val settingData = settingDataStore.settingData

    fun onNewSmsReceived() {

    }



}