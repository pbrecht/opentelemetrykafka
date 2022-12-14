package com.pdig.streams.vehicle.config.serde

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer

class JacksonSerdeSerializer<T>(val objectMapper: ObjectMapper) : Serializer<T> {

    override fun serialize(topic: String?, data: T): ByteArray? =
        if (data == null)
            null
        else
            runCatching {
                objectMapper.writeValueAsBytes(data)
            }.recoverCatching { ex ->
                throw SerializationException("Error on serialization", ex)
            }.getOrNull()
}
