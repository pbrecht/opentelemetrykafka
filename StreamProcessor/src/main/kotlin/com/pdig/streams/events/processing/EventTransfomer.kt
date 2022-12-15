package com.pdig.streams.events.processing

import com.fasterxml.jackson.databind.JsonNode
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import java.util.UUID
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

class EventTransfomer(val spanRepository: SpanRepository) : Transformer<String?, JsonNode, KeyValue<String, JsonNode>> {

    private lateinit var store: KeyValueStore<String, JsonNode>

    override fun init(context: ProcessorContext) {
        store = context.getStateStore("store")
    }

    override fun transform(key: String?, value: JsonNode?): KeyValue<String, JsonNode> {
        //val newValue = (value as ObjectNode).set<JsonNode>()
        //store.get(key)
        /*val newValue = (value as ObjectNode).set<JsonNode>(
            "details", jacksonObjectMapper().valueToTree(
                VehicleDetails(
                    model = Model("Taycan"),
                    variant = Variant("4S")
                )
            )
        )*/
        val uuid = UUID.randomUUID().toString()
        spanRepository.save(Span(uuid, value.toString()))
        println("### repo size: " + spanRepository.findAll().size)
        return KeyValue.pair(uuid, value)
    }

    override fun close() {

    }
}

@Document
data class Span (
        @Id
        val id: String,
        val value: String
)
@Repository
interface SpanRepository: MongoRepository<Span, String> { }
