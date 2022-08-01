package io.diegorxramos.kafkademo.infrastructure

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory


@Configuration
class KafkaConsumerConfig(
    @Value("\${spring.kafka.bootstrap-servers}") val bootstrapAddress: String

    ) {

    @Bean
    fun consumerFactory(): ConsumerFactory<String, Any> {
        val config = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapAddress,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java
        )
        return DefaultKafkaConsumerFactory(config)
    }

    @Bean
    fun concurrentListenerFactory(): ConcurrentKafkaListenerContainerFactory<*, *> {
        return ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            this.consumerFactory = consumerFactory()
        }
    }

    @Bean
    fun topic1() =
        TopicBuilder.name("kafka-demo-topic-1")
            .partitions(1)
            .replicas(1)
            .compact()
            .build()

    @Bean
    fun topic1Dlt() =
        TopicBuilder.name("kafka-demo-topic-1-dlt")
            .partitions(1)
            .replicas(1)
            .compact()
            .build()
}