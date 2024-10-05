package cn.quickweather.messageforward.sms

import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import cn.quickweather.android.common.util.applicationContext
import cn.quickweather.android.common.util.logI
import cn.quickweather.messageforward.history.ForwardHistoryDataStore
import cn.quickweather.messageforward.service.SmsDaemonService
import cn.quickweather.messageforward.setting.SettingDataStore
import cn.quickweather.messageforward.setting.phoneNumberValid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


/**
 * Created by maweihao on 5/21/24
 */
class SmsForwardManager(
    settingDataStore: SettingDataStore,
    private val verificationCodeResolver: MsgImportanceResolver,
    private val historyDataStore: ForwardHistoryDataStore,
    private val scope: CoroutineScope,
) {

    val settingData = settingDataStore.settingData
    private val msgChannel = Channel<MessageData>(Channel.BUFFERED)

    private val smsManager: SmsManager by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            applicationContext.getSystemService(SmsManager::class.java).createForSubscriptionId(SmsManager.getDefaultSmsSubscriptionId())
        } else {
            SmsManager.getDefault()
        }
    }

    init {
        scope.launch(Dispatchers.IO) {
            checkServiceState()
            while (true) {
                val element = msgChannel.receive()
                forwardMessage(element)
            }
        }
    }

    private suspend fun forwardMessage(sms: MessageData) {
        val history = historyDataStore.historyData.first().first { it.id == sms.id }
        val data = settingData.first()
        if (!data.enabled || !data.phoneNumberValid) {
            return
        }
        if (data.onlyVerificationCode && sms.isSms) {
            historyDataStore.updateHistory(history.copy(status = ForwardStatus.DetectingPriority.ordinal))
            val important = verificationCodeResolver.isMessageImportant(sms.msgBody)
            if (!important) {
                historyDataStore.updateHistory(history.copy(status = ForwardStatus.NotForwardDueToUnimportant.ordinal))
                return
            }
        }

        Log.i(TAG, "forwardMessage: send to ${data.smsToNumber}")
        smsManager.sendMultipartTextMessage(
            data.smsToNumber,
            null,
            smsManager.divideMessage(sms.msgBody),
            null, null,
        )
        historyDataStore.updateHistory(history.copy(status = ForwardStatus.ForwardSucceed.ordinal))
    }

    suspend fun checkServiceState() {
        val data = settingData.first()
        logI(TAG, "checkServiceState $data")
        enableForwardService(applicationContext, data.enabled)
    }

    fun onNewSmsReceived(sms: MessageData) {
        scope.launch {
            historyDataStore.addHistory(sms)
            msgChannel.trySend(sms)
        }
    }

    fun enableForwardService(context: Context, enable: Boolean) {
        SmsDaemonService.enableService(context, enable)
    }

}
private const val TAG = "SmsForwardManager"