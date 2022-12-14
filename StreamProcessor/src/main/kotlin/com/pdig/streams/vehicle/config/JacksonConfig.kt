package com.pdig.streams.vehicle.config

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {

    @Bean
    fun jackson() = jacksonObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .findAndRegisterModules()
}
