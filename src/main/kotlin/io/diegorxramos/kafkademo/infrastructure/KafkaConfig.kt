package io.diegorxramos.kafkademo.infrastructure

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.TopicBuilder

@EnableKafka
@Configuration
class KafkaConfig {

    @Bean
    fun topic1() =
        TopicBuilder.name("kafka-demo-topic-1")
            .partitions(10)
            .replicas(1)
            .compact()
            .build()
}