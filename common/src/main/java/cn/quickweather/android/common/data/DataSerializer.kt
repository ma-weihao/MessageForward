package cn.quickweather.android.common.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by maweihao on 5/24/24
 */
open class DataSerializer<Data>(
    private val serializer: KSerializer<Data>,
    override val defaultValue: Data,
) : Serializer<Data> {

    override suspend fun readFrom(input: InputStream): Data {
        try {
            return Json.decodeFromString(
                serializer, input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Prefs", serialization)
        }
    }

    override suspend fun writeTo(t: Data, output: OutputStream) {
        output.write(
            Json.encodeToString(serializer, t)
                .encodeToByteArray()
        )
    }

}