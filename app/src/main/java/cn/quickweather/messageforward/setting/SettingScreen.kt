@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package cn.quickweather.messageforward.setting

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.quickweather.messageforward.R
import cn.quickweather.messageforward.sms.ForwardStatus
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
        Box(modifier = Modifier.padding(padding)) {
            val shownSettingData = viewModel.shownSettingDataFlow.collectAsStateWithLifecycle().value
            val smsPermissionState = rememberMultiplePermissionsState(
                listOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS)
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
                }
            )
        }
    }
}

@Composable
private fun SettingContent(
    shownSettingData: ShownSettingData,
    onSwitchChanged: (Boolean) -> Unit,
    onPhoneNumberChanged: (String?) -> Unit,
    onFilterSwitchChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val settingData = shownSettingData.settingData
    val shownError = shownSettingData.shownError
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .scrollable(scrollState, orientation = Orientation.Vertical)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        AnimatedVisibility(visible = shownError != null) {
            ErrorCard(
                message = shownError?.errString?.let {
                    stringResource(id = it)
                } ?: ""
            )
        }

        MainSwitch(
            checked = settingData.enabled,
            onCheckedChange = onSwitchChanged,
        )

        MainSettingItems(
            settingData = settingData,
            onPhoneNumberChanged = onPhoneNumberChanged,
            onFilterSwitchChanged = onFilterSwitchChanged,
        )
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
    ContentCard {
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
private fun ColumnScope.MainSettingItems(
    settingData: SettingData,
    onPhoneNumberChanged: (String?) -> Unit,
    onFilterSwitchChanged: (Boolean) -> Unit,
) {
    AnimatedVisibility(visible = settingData.enabled) {
        ContentCard {
            ForwardToNumberContent(
                number = settingData.smsToNumber,
                onPhoneNumberChanged = onPhoneNumberChanged,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            OnlyForwardPriorityContent(
                checked = settingData.onlyVerificationCode,
                onCheckedChange = onFilterSwitchChanged,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
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
private fun ForwardHistoryList(

) {

}

@Composable
private fun ForwardHistoryItem(
    content: String,
    from: String,
    forwardStatus: ForwardStatus,
) {
    Row {

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
                OnlyForwardPriorityContent(false, {}, modifier = Modifier.padding(vertical = 8.dp))
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