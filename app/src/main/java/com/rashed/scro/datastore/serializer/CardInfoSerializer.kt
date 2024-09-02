package com.rashed.scro.datastore.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.rashed.scro.datastore.model.CardInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object CardInfoSerializer : Serializer<CardInfo> {
    override val defaultValue: CardInfo
        get() = CardInfo()

    override suspend fun readFrom(input: InputStream): CardInfo {
        return try {
            val jsonString = input.readBytes().decodeToString()
            Json.decodeFromString(
                deserializer = CardInfo.serializer(),
                string = jsonString
            )
        } catch (e: SerializationException) {
            throw CorruptionException("Unable to read preferences", e)
        }
    }

    override suspend fun writeTo(t: CardInfo, output: OutputStream) {
        withContext(Dispatchers.IO) {
            val jsonString = Json.encodeToString(
                serializer = CardInfo.serializer(),
                value = t
            )
            output.write(jsonString.encodeToByteArray())
        }
    }
}
