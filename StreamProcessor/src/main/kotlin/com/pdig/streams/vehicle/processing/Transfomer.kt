package com.pdig.streams.vehicle.processing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pdig.streams.vehicle.processing.domain.VehicleDetails
import com.pdig.streams.vehicle.processing.domain.primitives.Model
import com.pdig.streams.vehicle.processing.domain.primitives.Variant
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore

class Transfomer() : Transformer<String, JsonNode, JsonNode> {

    private lateinit var store: KeyValueStore<String, JsonNode>

    override fun init(context: ProcessorContext) {
        store = context.getStateStore("store")
    }

    override fun transform(key: String?, value: JsonNode?): JsonNode {
        store.get(key)
        return (value as ObjectNode).set(
            "details", jacksonObjectMapper().valueToTree(
                VehicleDetails(
                    model = Model("Taycan"),
                    variant = Variant("4S")
                )
            )
        )
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}
