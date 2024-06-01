package cn.quickweather.messageforward.setting

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import cn.quickweather.android.common.data.DataSerializer
import kotlinx.coroutines.flow.Flow

/**
 * Created by maweihao on 5/24/24
 */
class SettingDataStore(context: Context) {

    private val Context.dataStore: DataStore<SettingData> by dataStore(
        fileName = "settings",
        serializer = DataSerializer(SettingData.serializer(), SettingData()),
    )

    private val dataStore = context.dataStore

    val settingData: Flow<SettingData> = dataStore.data

    suspend fun updateSetting(data: SettingData) {
        dataStore.updateData {
            data
        }
    }

}