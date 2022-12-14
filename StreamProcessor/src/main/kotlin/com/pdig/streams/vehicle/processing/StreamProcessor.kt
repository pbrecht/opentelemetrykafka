package com.pdig.streams.vehicle.processing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pdig.streams.vehicle.config.serde.JacksonSerde
import com.pdig.streams.vehicle.processing.domain.VehicleDetails
import com.pdig.streams.vehicle.processing.domain.primitives.Model
import com.pdig.streams.vehicle.processing.domain.primitives.Variant
import mu.KotlinLogging
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.ValueJoiner
import org.apache.kafka.streams.state.Stores
import org.apache.kafka.streams.state.internals.KeyValueStoreBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin
import java.time.Clock


@Configuration
class StreamProcessor {

    private val logger = KotlinLogging.logger {}

    @Bean
    fun appTopics(): KafkaAdmin.NewTopics {
        return KafkaAdmin.NewTopics(
            TopicBuilder.name(VEHICLE_TOPIC).build(),
            TopicBuilder.name(VEHIVLE_MASTER).compact().build(),
            TopicBuilder.name(OUTPUT).build(),
        )
    }

    companion object {
        val VEHICLE_TOPIC = "vehicle-stream"
        val VEHIVLE_MASTER = "vehicle-master"
        val OUTPUT = "output"
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
        val master = streamsBuilder.stream(VEHIVLE_MASTER, consumedWith)
        val vehicle = streamsBuilder.stream(VEHICLE_TOPIC, consumedWith)

        val table = master.toTable()

        vehicle.leftJoin(table) { vehicleRecord, masterRecord ->
            (vehicleRecord as ObjectNode).set<JsonNode>("details", JsonNodeFactory.instance.pojoNode(
                VehicleDetails(
                    model = Model("Taycan"), variant = Variant("4S")
                )
            ))
        }
        vehicle.peek { key, value ->  logger.info("Receive msg with key $key and value $value")}
        vehicle.to(OUTPUT)
        vehicle.peek { key, value ->  logger.info("Write msg with key $key and value $value")}
        return vehicle
    }
}

