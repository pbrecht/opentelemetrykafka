package com.pdig.streams.events.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pdig.streams.events.config.serde.JacksonSerde
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.KafkaStreamsConfiguration

@Configuration
class Properties {

    @Bean
    fun defaultKafkaStreamsConfig(): KafkaStreamsConfiguration {
        val props: MutableMap<String, Any> = HashMap()
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG] =
            LogAndContinueExceptionHandler::class.java
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "events-stream"
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = JacksonSerde::class.java
        props[ConsumerConfig.GROUP_ID_CONFIG] = "events-stream-group"
        return KafkaStreamsConfiguration(props)
    }

    @Bean
    fun objectMapper() = jacksonObjectMapper()

}
