package com.pdig.streams.events

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.kafka.annotation.EnableKafkaStreams

@EnableKafkaStreams
@SpringBootApplication
@EnableMongoRepositories
class StreamProcessorApplication

fun main(args: Array<String>) {
    runApplication<StreamProcessorApplication>(*args)
}
