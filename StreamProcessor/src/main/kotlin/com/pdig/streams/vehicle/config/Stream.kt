package com.pdig.streams.vehicle.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer

@Configuration
class Stream(
    val stateListener: KafkaStreamsHealthIndicator
) {

    @Bean
    fun configurer(): StreamsBuilderFactoryBeanConfigurer {
        return StreamsBuilderFactoryBeanConfigurer { fb: StreamsBuilderFactoryBean ->
            fb.setStateListener(stateListener)
        }
    }
}
