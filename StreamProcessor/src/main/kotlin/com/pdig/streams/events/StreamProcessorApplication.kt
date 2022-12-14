package com.pdig.streams.events

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafkaStreams

@EnableKafkaStreams
@SpringBootApplication
class StreamProcessorApplication

fun main(args: Array<String>) {
    runApplication<StreamProcessorApplication>(*args)
}
