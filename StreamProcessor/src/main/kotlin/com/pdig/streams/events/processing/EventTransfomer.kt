package com.pdig.streams.events.processing

import com.fasterxml.jackson.databind.JsonNode
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository

class EventTransfomer : Transformer<String, JsonNode, KeyValue<String, JsonNode>> {

    private lateinit var store: KeyValueStore<String, JsonNode>
    private lateinit var spanRepository: SpanRepository

    override fun init(context: ProcessorContext) {
        store = context.getStateStore("store")
    }

    override fun transform(key: String?, value: JsonNode?): KeyValue<String, JsonNode> {
        store.get(key)
        spanRepository.save(Span("key", "value"))
        /*
           if (key != null) {
            spanRepository.save(Span(key, value.toString()))
        }

        val newValue = (value as ObjectNode).set<JsonNode>(
            "details", jacksonObjectMapper().valueToTree(
                VehicleDetails(
                    model = Model("Taycan"),
                    variant = Variant("4S")
                )
            )
        )*/
        return KeyValue.pair(key, value)
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}

@Document
data class Span (
        @Id
        val id: String,
        val value: String
)
interface SpanRepository : MongoRepository<Span, String> { }
