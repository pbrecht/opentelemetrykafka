package com.pdig.streams.events.processing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pdig.streams.events.config.serde.JacksonSerde
import mu.KotlinLogging
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.state.Stores
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin


@Configuration
class StreamProcessor {

    private val logger = KotlinLogging.logger {}

    @Bean
    fun appTopics(): KafkaAdmin.NewTopics {
        return KafkaAdmin.NewTopics(
            TopicBuilder.name(TRACE_TOPIC).build(),
            TopicBuilder.name(TRACKING_TOPIC).compact().build()
        )
    }

    companion object {
        val TRACE_TOPIC = "otlp_spans"
        val TRACKING_TOPIC = "tracking_events"
    }

    @Bean
    fun topology(streamsBuilder: StreamsBuilder): KStream<String, JsonNode> {

        val serdeKey = Serdes.String()
        val serdeValue = JacksonSerde(jacksonObjectMapper(), JsonNode::class.java)

        val store = Stores.keyValueStoreBuilder(
            Stores.persistentKeyValueStore("store"),
            serdeKey,
            serdeValue,
        )
        streamsBuilder.addStateStore(store)


        val consumedWith: Consumed<String, JsonNode> = Consumed.with(serdeKey, serdeValue)
        val input = streamsBuilder.stream(TRACE_TOPIC, consumedWith)

        input.transform({ EventTransfomer() })
        input.peek { key, value ->  logger.info("Receive msg with key $key and value $value")}
        input.to(TRACKING_TOPIC)
        return input
    }
}
