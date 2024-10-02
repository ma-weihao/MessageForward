package cn.quickweather.messageforward.setting

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.quickweather.messageforward.R
import cn.quickweather.messageforward.history.ForwardHistoryDataStore
import cn.quickweather.messageforward.history.HistoryData
import cn.quickweather.messageforward.sms.SmsForwardManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Created by maweihao on 5/21/24
 */
internal class SettingViewModel(
    private val smsForwardManager: SmsForwardManager,
    private val settingDataStore: SettingDataStore,
    private val historyDataStore: ForwardHistoryDataStore,
): ViewModel() {

    private val settingDataFlow: Flow<SettingData> = settingDataStore.settingData
    private val permissionFlow = MutableStateFlow(PermissionState(
        smsPermissionEnabled = true,
        notificationPermissionEnabled = true
    ))

    private val _shownSettingDataFlow: Flow<ShownSettingData> = combine(
        settingDataFlow,
        permissionFlow,
        historyDataStore.historyData,
        ) { settingData, permission, historyList ->
        if (!settingData.enabled) {
            ShownSettingData(settingData)
        } else if (!settingData.phoneNumberValid) {
            ShownSettingData(settingData, ShownError.InvalidPhoneNumber, historyList)
        } else if (!permission.smsPermissionEnabled) {
            ShownSettingData(settingData, ShownError.LackSmsPermission, historyList)
        } else if (!permission.notificationPermissionEnabled) {
            ShownSettingData(settingData, ShownError.LackNotificationPermission, historyList)
        } else {
            ShownSettingData(settingData, history = historyList)
        }
    }

    val shownSettingDataFlow = _shownSettingDataFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        ShownSettingData(SettingData()),
    )

    fun refreshSmsPermissionState(enabled: Boolean) {
        permissionFlow.value = permissionFlow.value.copy(smsPermissionEnabled = enabled)
    }

    fun refreshNotificationPermissionState(enabled: Boolean) {
        permissionFlow.value = permissionFlow.value.copy(notificationPermissionEnabled = enabled)
    }

    fun changeSetting(context: Context, enabled: Boolean) {
        viewModelScope.launch {
            settingDataStore.updateSetting(
                settingDataFlow.first().copy(
                    enabled = enabled,
                )
            )
            smsForwardManager.enableForwardService(context, enabled)
        }
    }

    fun changePhoneNumber(s: String?) {
        viewModelScope.launch {
            settingDataStore.updateSetting(
                settingDataFlow.first().copy(
                    smsToNumber = s,
                )
            )
        }
    }

    fun changeOnlyForwardVerificationCode(enabled: Boolean) {
        viewModelScope.launch {
            settingDataStore.updateSetting(
                settingDataFlow.first().copy(
                    onlyVerificationCode = enabled,
                )
            )
        }
    }

    fun changeMarkAsRead(enabled: Boolean) {
        viewModelScope.launch {
            settingDataStore.updateSetting(
                settingDataFlow.first().copy(
                    markAsRead = enabled,
                )
            )
        }
    }

    fun changeBatteryNotification(enabled: Boolean) {
        viewModelScope.launch {
            settingDataStore.updateSetting(
                settingDataFlow.first().copy(
                    sendBatteryNotification = enabled,
                )
            )
        }
    }

}

private const val TAG = "SettingViewModel"

internal data class ShownSettingData(
    val settingData: SettingData,
    val shownError: ShownError? = null,
    val history: List<HistoryData> = emptyList(),
)

private data class PermissionState(
    val smsPermissionEnabled: Boolean,
    val notificationPermissionEnabled: Boolean,
)

internal enum class ShownError(
    @StringRes val errString: Int,
) {
    InvalidPhoneNumber(R.string.error_invalid_phone_number),
    LackSmsPermission(R.string.error_LackSmsPermission),
    LackNotificationPermission(R.string.error_LackNotificationPermission),
    ;
}

@OptIn(ExperimentalPermissionsApi::class)
internal object GrantedPermissionState : com.google.accompanist.permissions.PermissionState  {
    override val permission: String = ""
    override val status: PermissionStatus
        get() = PermissionStatus.Granted

    override fun launchPermissionRequest() {}

}
