package cn.quickweather.messageforward.service

import cn.quickweather.android.common.util.logI
import cn.quickweather.android.common.util.toResString
import cn.quickweather.messageforward.R
import cn.quickweather.messageforward.setting.SettingDataStore
import cn.quickweather.messageforward.sms.MessageData
import cn.quickweather.messageforward.sms.MessageType
import cn.quickweather.messageforward.sms.SmsForwardManager
import kotlinx.coroutines.flow.first
import java.util.UUID

/**
 * Created by maweihao on 10/5/24
 */
class LowBatteryHandler(
    private val smsForwardManager: SmsForwardManager,
    private val settingDataStore: SettingDataStore,
) {

    fun isLowBattery(level: Int): Boolean {
        return level <= BATTERY_LOW_LEVEL
    }

    suspend fun handleLowBattery(level: Int) {
        if (level > BATTERY_LOW_LEVEL) {
            return
        }
        val settingData = smsForwardManager.settingData.first()
        if (!settingData.enabled || !settingData.sendBatteryNotification) {
            return
        }

        val lastSentTime = settingData.lastBatteryNotificationTime
        val interval = (System.currentTimeMillis() - lastSentTime) / 1000
        if (interval < 60 * 60 * 24) {
            logI(TAG, "Battery notification already sent ${interval / 60}min ago")
            return
        }

        settingDataStore.updateSetting(settingData.copy(lastBatteryNotificationTime = System.currentTimeMillis()))

        val msg = createLowBatteryNotification(level)
        logI(TAG, "Sending low battery notification $msg")
        smsForwardManager.onNewSmsReceived(msg)
    }

    private fun createLowBatteryNotification(level: Int): MessageData {
        return MessageData(
            originatingAddress = R.string.send_dead_notification_title.toResString(),
            msgBody = R.string.send_dead_notification_msg_content.toResString(level.toString()),
            receivedTime = System.currentTimeMillis(),
            splitPartsSize = 1,
            id = UUID.randomUUID().toString(),
            messageOrder = MessageType.LOW_BATTERY.ordinal,
        )
    }

}
private const val TAG = "LowBatteryHandler"
private const val BATTERY_LOW_LEVEL = 5