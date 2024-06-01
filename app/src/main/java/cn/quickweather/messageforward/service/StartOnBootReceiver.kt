package cn.quickweather.messageforward.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cn.quickweather.messageforward.sms.SmsForwardManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.java.KoinJavaComponent

/**
 * Created by maweihao on 6/1/24
 */
private const val TAG = "StartOnBootReceiver"
class StartOnBootReceiver: BroadcastReceiver(), KoinComponent {

    private val manager: SmsForwardManager by KoinJavaComponent.inject(SmsForwardManager::class.java)

    override fun onReceive(context: Context?, intent: Intent?) {
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            manager.checkServiceState()
            pendingResult.finish()
            coroutineContext.cancel()
        }
    }

}