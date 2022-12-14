package com.pdig.vehicle.streams.vehiclestreamprocessor

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pdig.streams.vehicle.config.serde.JacksonSerde
import com.pdig.streams.vehicle.processing.StreamProcessor
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TestOutputTopic
import org.apache.kafka.streams.TopologyTestDriver
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehicleStreamProcessorApplicationTests {

    private val objectMapper = jacksonObjectMapper()
    private lateinit var testDriver: TopologyTestDriver
    private lateinit var masterTopic: TestInputTopic<String, JsonNode>
    private lateinit var vehicleTopic: TestInputTopic<String, JsonNode>
    private lateinit var outputTopic: TestOutputTopic<String, JsonNode>

    @BeforeAll
    fun before() {

        val streamsBuilder = StreamsBuilder()
        StreamProcessor().topology(streamsBuilder)
        testDriver = TopologyTestDriver(streamsBuilder.build())

        masterTopic = testDriver.createInputTopic(
            StreamProcessor.VEHIVLE_MASTER,
            Serdes.String().serializer(),
            JacksonSerde(objectMapper, JsonNode::class.java).serializer()
        )
        vehicleTopic = testDriver.createInputTopic(
            StreamProcessor.VEHICLE_TOPIC,
            Serdes.String().serializer(),
            JacksonSerde(objectMapper, JsonNode::class.java).serializer()
        )
        outputTopic = testDriver.createOutputTopic(
            StreamProcessor.OUTPUT,
            Serdes.String().deserializer(),
            JacksonSerde(objectMapper, JsonNode::class.java).deserializer()
        )
    }

    @AfterEach
    fun afterEach() {
        testDriver.close()
    }

    @Test
    fun `expected output`() {

        val key = "1"
        val factory = JsonNodeFactory.instance
        val masterData = factory.textNode("""{"model":{"value":"Taycan"},"variant":{"value":"4S"}}""")
        val streamData = factory.objectNode()

        masterTopic.pipeInput(key, masterData)
        vehicleTopic.pipeInput(key, streamData)
        outputTopic.readKeyValue().let { keyValue ->
            assertEquals(keyValue.key, key)
            assertEquals(
                keyValue.value,
                factory.textNode("""{"details":{"model":{"value":"Taycan"},"variant":{"value":"4S"}}}""")
            )
        }

    }

}
