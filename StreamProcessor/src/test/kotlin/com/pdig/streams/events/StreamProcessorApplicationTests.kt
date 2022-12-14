package com.pdig.streams.events

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pdig.streams.events.config.serde.JacksonSerde
import com.pdig.streams.events.processing.StreamProcessor
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
class StreamProcessorApplicationTests {

    private val objectMapper = jacksonObjectMapper()
    private lateinit var testDriver: TopologyTestDriver
    private lateinit var inputTopic: TestInputTopic<String, JsonNode>
    private lateinit var outputTopic: TestOutputTopic<String, JsonNode>

    @BeforeAll
    fun before() {

        val streamsBuilder = StreamsBuilder()
        StreamProcessor().topology(streamsBuilder)
        testDriver = TopologyTestDriver(streamsBuilder.build())

        inputTopic = testDriver.createInputTopic(
            StreamProcessor.TRACE_TOPIC,
            Serdes.String().serializer(),
            JacksonSerde(objectMapper, JsonNode::class.java).serializer()
        )
        outputTopic = testDriver.createOutputTopic(
            StreamProcessor.TRACKING_TOPIC,
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

        inputTopic.pipeInput(key, masterData)
        outputTopic.readKeyValue().let { keyValue ->
            assertEquals(keyValue.key, key)
            assertEquals(
                keyValue.value,
                factory.textNode("""{"details":{"model":{"value":"Taycan"},"variant":{"value":"4S"}}}""")
            )
        }

    }

}
