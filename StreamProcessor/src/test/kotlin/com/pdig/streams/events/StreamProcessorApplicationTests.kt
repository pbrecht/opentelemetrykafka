package com.pdig.streams.events

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pdig.streams.events.config.serde.JacksonSerde
import com.pdig.streams.events.processing.Span
import com.pdig.streams.events.processing.SpanRepository
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
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StreamProcessorApplicationTests {

    private val objectMapper = jacksonObjectMapper()
    private lateinit var testDriver: TopologyTestDriver
    private lateinit var inputTopic: TestInputTopic<String, JsonNode>
    private lateinit var outputTopic: TestOutputTopic<String, JsonNode>

    private val spanRepository: SpanRepository = Mockito.mock(SpanRepository::class.java)

    @BeforeAll
    fun before() {

        val streamsBuilder = StreamsBuilder()
        StreamProcessor().topology(streamsBuilder, spanRepository)
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

        val resourceName = "files/event.json"

        val classLoader = javaClass.classLoader
        val file = File(classLoader.getResource(resourceName)!!.file)

        given(spanRepository.save(any())).willReturn(Span("1", ""))
        given(spanRepository.findAll()).willReturn(emptyList())

        val key = "1"
        val masterData = objectMapper.readTree(file)
        inputTopic.pipeInput(key, masterData)
        outputTopic.readKeyValue().let { keyValue ->
            assertEquals(keyValue.key, key)
            assertEquals(
                keyValue.value,
                masterData)
        }
    }

}
