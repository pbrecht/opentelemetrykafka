package com.pdig.streams.vehicle

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafkaStreams

@EnableKafkaStreams
@SpringBootApplication
class VehicleStreamProcessorApplication

fun main(args: Array<String>) {
    runApplication<VehicleStreamProcessorApplication>(*args)
}
