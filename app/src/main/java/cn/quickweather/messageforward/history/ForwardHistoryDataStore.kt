package cn.quickweather.messageforward.history

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import cn.quickweather.android.common.data.DataSerializer
import cn.quickweather.messageforward.sms.ForwardStatus
import cn.quickweather.messageforward.sms.MessageData
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.builtins.ListSerializer

/**
 * Created by maweihao on 6/25/24
 */
class ForwardHistoryDataStore(context: Context) {

    private val Context.dataStore: DataStore<List<HistoryData>> by dataStore(
        fileName = "history",
        serializer = DataSerializer(ListSerializer(HistoryData.serializer()), emptyList()),
    )

    private val dataStore = context.dataStore

    val historyData: Flow<List<HistoryData>> = dataStore.data

    suspend fun updateHistory(data: HistoryData) {
        dataStore.updateData { list ->
            list.toMutableList().apply {
                replaceAll {
                    if (it.id == data.id) {
                        data
                    } else {
                        it
                    }
                }
            }
        }
    }

    suspend fun addHistory(data: MessageData) {
        dataStore.updateData { list ->
            list.toMutableList().apply {
                add(
                    0, HistoryData(
                        data,
                        ForwardStatus.Pending.ordinal,
                    )
                )
            }
        }
    }

    suspend fun updateHistoryList(data: List<HistoryData>) {
        dataStore.updateData {
            data
        }
    }

}