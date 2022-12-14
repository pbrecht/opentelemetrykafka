package com.pdig.streams.events.config.serde

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer

class JacksonSerdeDeserializer<T>(private val objectMapper: ObjectMapper, private val type: Class<T>) : Deserializer<T> {

    override fun deserialize(topic: String?, data: ByteArray?): T? =
        if (data == null)
            null
        else
            runCatching {
                objectMapper.readValue(data, type)
            }.recoverCatching { ex ->
                throw SerializationException("Error on serialization", ex)
            }.getOrNull()
    }
