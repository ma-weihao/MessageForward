@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package cn.quickweather.messageforward.setting

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.quickweather.android.common.util.showShortToast
import cn.quickweather.messageforward.R
import cn.quickweather.messageforward.history.HistoryData
import cn.quickweather.messageforward.sms.ForwardStatus
import cn.quickweather.messageforward.sms.MessageData
import cn.quickweather.messageforward.ui.theme.ContentCard
import cn.quickweather.messageforward.ui.theme.ErrorCard
import cn.quickweather.messageforward.ui.theme.MessageForwardTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by maweihao on 5/20/24
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: SettingViewModel = koinViewModel()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_forward_to_inbox),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Text(text = stringResource(id = R.string.display_app_name), color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        Box(modifier = Modifier.padding(top = padding.calculateTopPadding())) {
            val shownSettingData = viewModel.shownSettingDataFlow.collectAsStateWithLifecycle().value
            val smsPermissionState = rememberMultiplePermissionsState(
                listOf(
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS,
                )
            )
            LaunchedEffect(smsPermissionState.allPermissionsGranted) {
                viewModel.refreshSmsPermissionState(smsPermissionState.allPermissionsGranted)
            }
            val notificationPermissionState = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
            } else {
                GrantedPermissionState
            }
            LaunchedEffect(notificationPermissionState.status) {
                viewModel.refreshNotificationPermissionState(notificationPermissionState.status == PermissionStatus.Granted)
            }
            val context = LocalContext.current
            SettingContent(
                shownSettingData = shownSettingData,
                history = shownSettingData.history,
                onSwitchChanged = { on ->
                    if (on) {
                        requestPermission(smsPermissionState, notificationPermissionState)
                        viewModel.changeSetting(context, true)
                    } else {
                        viewModel.changeSetting(context, false)
                    }
                },
                onPhoneNumberChanged = {
                    viewModel.changePhoneNumber(it)
                },
                onFilterSwitchChanged = {
                    viewModel.changeOnlyForwardVerificationCode(it)
                },
                onBatteryNotificationChanged = {
                    viewModel.changeBatteryNotification(it)
                },
                bottomPadding = padding.calculateBottomPadding(),
            )
            if (shownSettingData.showConsentDialog) {
                ConsentDialog(
                    onConfirm = {
                        viewModel.onAgreeConsent()
                    },
                    onDismiss = {
                        viewModel.onDisagreeConsent()
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingContent(
    shownSettingData: ShownSettingData,
    history: List<HistoryData>,
    onSwitchChanged: (Boolean) -> Unit,
    onPhoneNumberChanged: (String?) -> Unit,
    onFilterSwitchChanged: (Boolean) -> Unit,
    onBatteryNotificationChanged: (Boolean) -> Unit,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val settingData = shownSettingData.settingData
    val shownError = shownSettingData.shownError
    LazyColumn(
        contentPadding = PaddingValues(bottom = bottomPadding),
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp),
    ) {
        if (shownError != null) {
            item {
                ErrorCard(
                    message = stringResource(id = shownError.errString)
                )
            }
        }

        item {
            MainSwitch(
                checked = settingData.enabled,
                onCheckedChange = onSwitchChanged,
            )
        }

        if (settingData.enabled) {
            item {
                MainSettingItems(
                    settingData = settingData,
                    onPhoneNumberChanged = onPhoneNumberChanged,
                    onFilterSwitchChanged = onFilterSwitchChanged,
                    onBatteryNotificationChanged = onBatteryNotificationChanged,
                )
            }
        }


        if (settingData.enabled) {
            forwardHistoryList(
                history = history
            )
        }
    }

}

private fun requestPermission(
    smsPermissionState: MultiplePermissionsState,
    notificationPermissionState: PermissionState
) {
    if (!smsPermissionState.allPermissionsGranted) {
        smsPermissionState.launchMultiplePermissionRequest()
    }
    if (notificationPermissionState.status != PermissionStatus.Granted) {
        notificationPermissionState.launchPermissionRequest()
    }
}

@Composable
private fun MainSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ContentCard(
        outerPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .padding(end = 4.dp, top = 8.dp, bottom = 8.dp)) {
                Text(
                    text = stringResource(id = R.string.title_enable_forward),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = stringResource(id = R.string.description_enable_forward),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}

@Composable
private fun MainSettingItems(
    settingData: SettingData,
    onPhoneNumberChanged: (String?) -> Unit,
    onFilterSwitchChanged: (Boolean) -> Unit,
    onBatteryNotificationChanged: (Boolean) -> Unit,
) {
    ContentCard(
        outerPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = stringResource(
                id = R.string.title_setting
            ),
            style = MaterialTheme.typography.titleLarge,
        )
        ForwardToNumberContent(
            number = settingData.smsToNumber,
            onPhoneNumberChanged = onPhoneNumberChanged,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        BatteryNotificationContent(
            checked = settingData.sendBatteryNotification,
            onCheckedChange = onBatteryNotificationChanged,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        OnlyForwardPriorityContent(
            checked = settingData.onlyVerificationCode,
            onCheckedChange = onFilterSwitchChanged,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

@Composable
private fun ForwardToNumberContent(
    number: String?,
    onPhoneNumberChanged: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember {
        mutableStateOf(false)
    }
    val textColor = if (number.isNullOrBlank() || number.phoneNumberValid) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.error
    }
    Row(
        modifier = modifier
            .clickable {
                showDialog = true
            }
            .padding(top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_outward),
            modifier = Modifier
                .size(32.dp)
                .padding(end = 8.dp),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
        )
        Text(
            text = stringResource(id = R.string.title_forward_to_number),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = if (number.isNullOrBlank()) {
                stringResource(id = R.string.title_forward_unset)
            } else {
                number
            },
            style = MaterialTheme.typography.bodyMedium.copy(color = textColor),
        )
    }
    if (showDialog) {
        NumberInputDialog(
            number = number,
            onPhoneNumberChanged = onPhoneNumberChanged,
            dismissDialog = {
                showDialog = false
            }
        )
    }
}

@Composable
private fun MarkAsReadContent(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
                .padding(end = 4.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_mark_email_read_24),
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
            )
            Text(
                text = stringResource(id = R.string.title_mark_as_read_title),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun OnlyForwardPriorityContent(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            Modifier
                .weight(1f)
                .wrapContentHeight()
                .padding(end = 4.dp, top = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_intelligence_56),
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 8.dp),
                    contentDescription = null,
                )
                Text(
                    text = stringResource(id = R.string.title_only_forward_priority_messages_title),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Text(
                text = stringResource(id = R.string.title_only_forward_priority_messages_desc),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun BatteryNotificationContent(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            Modifier
                .weight(1f)
                .wrapContentHeight()
                .padding(end = 4.dp, top = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_battery_1_bar_24),
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                )
                Text(
                    text = stringResource(id = R.string.send_dead_notification_title),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Text(
                text = stringResource(id = R.string.send_dead_notification_desc),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

private fun LazyListScope.forwardHistoryList(
    history: List<HistoryData>,
) {
    item {
        if (history.isNotEmpty()) {
            ContentCard(
                bottomCornerSize = 0.dp,
                outerPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp),
            ) {
                Text(
                    text = stringResource(
                        id = R.string.title_forward_history
                    ),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        } else {
            ContentCard(
                outerPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                innerPadding = PaddingValues(vertical = 16.dp, horizontal = 12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.title_no_forward_history),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }

    if (history.isNotEmpty()) {
        items(history.size, key = {
            history[it].id
        }) { index ->
            ForwardHistoryItem(
                time = history[index].message.receivedTime,
                status = ForwardStatus.parse(history[index].status),
                from = history[index].message.originatingAddress ?: "",
                content = history[index].message.msgBody ?: "",
                withBottomDivider = index < history.size - 1,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(8.dp)
            )
        }
    }

    item {
        ContentCard(
            topCornerSize = 0.dp,
            innerPadding = PaddingValues(bottom = 16.dp),
            outerPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 4.dp),
        ) {
        }
    }
}

@Composable
private fun ForwardHistoryItem(
    time: Long,
    status: ForwardStatus,
    from: String,
    content: String,
    modifier: Modifier = Modifier,
    withBottomDivider: Boolean = true,
) {
    val shownTime = remember(key1 = time) {
        val date = Date(time)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        if (today == dateStr) {
            "Today " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
        } else {
            SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(date)
        }
    }
    Column(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = from,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(end = 12.dp, start = 4.dp),
                )
                Text(
                    text = shownTime,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Icon(
                painter = painterResource(id = status.icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(24.dp)
                    .clickable {
                        showShortToast(status.label)
                    },
            )
        }
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (withBottomDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
private fun NumberInputDialog(
    number: String?,
    onPhoneNumberChanged: (String?) -> Unit,
    dismissDialog: () -> Unit,
) {
    var shownNumber by remember {
        mutableStateOf(number ?: "")
    }
    Dialog(
        onDismissRequest = dismissDialog,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = stringResource(id = R.string.title_forward_to_number),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(16.dp)
            )

            OutlinedTextField(
                value = shownNumber,
                onValueChange = {
                    shownNumber = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Row {
                    TextButton(
                        onClick = dismissDialog,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = {
                            onPhoneNumberChanged(shownNumber)
                            dismissDialog()
                        },
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsentDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    cn.quickweather.messageforward.ui.theme.Dialog(
        title = stringResource(id = R.string.warning_title_only_forward_priority_messages),
        content = {
                Text(
                    text = stringResource(id = R.string.warning_content_only_forward_priority_messages),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
        },
        actions = {
            Row {
                TextButton(
                    onClick = onDismiss,
                ) {
                    Text(stringResource(id = R.string.warning_negative_button_only_forward_priority_messages))
                }
                TextButton(
                    onClick = onConfirm,
                ) {
                    Text(stringResource(id = R.string.warning_positive_button_only_forward_priority_messages))
                }
            }
        },
        dismissDialog = onDismiss,
    )
}

@Preview(showSystemUi = true)
@Composable
private fun SettingScreenPreview() {
    MessageForwardTheme {
        Column {
            ErrorCard(message = stringResource(id = R.string.phone_number_invalid))
            MainSwitch(true) {

            }
            ContentCard {
                ForwardToNumberContent(
                    "15952033659",
                    {},
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                MarkAsReadContent(false, {}, modifier = Modifier.padding(vertical = 8.dp))
                BatteryNotificationContent(false, {}, modifier = Modifier.padding(vertical = 8.dp))
                OnlyForwardPriorityContent(false, {}, modifier = Modifier.padding(vertical = 8.dp))
            }
            LazyColumn {
                forwardHistoryList(
                    history = previewHistoryList
                )
            }
        }
    }
}

@Preview
@Composable
private fun NumberInputDialogPreview() {
    MessageForwardTheme {
        NumberInputDialog("123", {}) {

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForwardHistoryItemPreview() {
    MessageForwardTheme {
        ForwardHistoryItem(
            1632192000000,
            ForwardStatus.ForwardSucceed,
            "15952032659",
            "亲爱的居民朋友：2024年9月21日是我国第24个全民国防教育日，也是上海市第17个全市防空警报试鸣日。您可通过高德、百度地图搜索“民防工程”，查询身边的民防工程；打开微信小程序“民防在我身边”，了解浦东新区范围内的民防教育基地、应急避难场所和民防工程。【浦东新区国动办】",
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ConsentDialogPreview() {
    MessageForwardTheme {
        ConsentDialog({}, {})
    }
}

private val previewHistoryList = listOf(
    HistoryData(
        MessageData(
            "15952033659",
            "亲爱的居民朋友：2024年9月21日是我国第24个全民国防教育日，也是上海市第17个全市防空警报试鸣日。您可通过高德、百度地图搜索“民防工程”，查询身边的民防工程；打开微信小程序“民防在我身边”，了解浦东新区范围内的民防教育基地、应急避难场所和民防工程。【浦东新区国动办】",
            1632192000000L,
            id = "1",
        ),
        ForwardStatus.ForwardSucceed.ordinal
    ),
//    HistoryData(
//        MessageData(
//            "15952033659",
//            "【充值提醒】尊敬的客户，您已成功充值30.00元，查询余额请登录中国电信APP http://a.189.cn/JJLkBW 或关注“吉林电信”微信公众号查询 。邀您领取1-100元随机话费福利，限量福利先到先得，点击 http://a.189.cn/JJLkBW。【好服务 更随心】中国电信",
//            1632192000000L,
//            id = "2",
//        ),
//        ForwardStatus.ForwardFailedDueToSms.ordinal
//    ),
)