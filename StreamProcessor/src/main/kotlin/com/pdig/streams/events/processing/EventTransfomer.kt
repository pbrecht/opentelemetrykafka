package com.pdig.streams.events.processing

import com.fasterxml.jackson.databind.JsonNode
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import java.util.UUID

class EventTransfomer : Transformer<String?, JsonNode, KeyValue<String, JsonNode>> {

    private lateinit var store: KeyValueStore<String, JsonNode>

    override fun init(context: ProcessorContext) {
        store = context.getStateStore("store")
    }

    override fun transform(key: String?, value: JsonNode?): KeyValue<String, JsonNode> {
        //store.get(key)
        /*val newValue = (value as ObjectNode).set<JsonNode>(
            "details", jacksonObjectMapper().valueToTree(
                VehicleDetails(
                    model = Model("Taycan"),
                    variant = Variant("4S")
                )
            )
        )*/
        return KeyValue.pair(UUID.randomUUID().toString(), value)
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}
