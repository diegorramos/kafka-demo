package io.diegorxramos.kafkademo.infrastructure

import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
class KafkaConsumerConfig(private val properties: KafkaProperties) {

    @Bean
    fun consumerFactory(): ConsumerFactory<String?, Any?> {
        val jsonDeserializer: JsonDeserializer<Any> = JsonDeserializer()
        jsonDeserializer.addTrustedPackages("*")
        return DefaultKafkaConsumerFactory(
            properties.buildConsumerProperties(), StringDeserializer(), jsonDeserializer
        )
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any>? {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.consumerFactory = consumerFactory()
        return factory
    }

    @Bean
    fun stringConsumerFactory(): ConsumerFactory<String?, String?> {
        return DefaultKafkaConsumerFactory(
            properties.buildConsumerProperties(), StringDeserializer(), StringDeserializer()
        )
    }

    @Bean
    fun kafkaListenerStringContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String>? {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = stringConsumerFactory()
        return factory
    }

    @Bean
    fun topic1() =
        TopicBuilder.name("kafka-demo-topic-1")
            .partitions(1)
            .replicas(1)
            .compact()
            .build()
}