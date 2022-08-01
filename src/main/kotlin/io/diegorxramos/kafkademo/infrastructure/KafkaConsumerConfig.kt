package io.diegorxramos.kafkademo.infrastructure

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaOperations
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries


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
    @Bean
    fun defaultErrorHandler(): DefaultErrorHandler {
        val recovery = DeadLetterPublishingRecoverer(operations()) { cr: ConsumerRecord<*, *>, e: Exception? ->
            TopicPartition(cr.topic() + "-dlt", 0)
        }

        val exponentialBackOff = ExponentialBackOffWithMaxRetries(3)
        exponentialBackOff.multiplier = 2.0
        exponentialBackOff.maxInterval = 10000L
        exponentialBackOff.initialInterval = 1000L
        return DefaultErrorHandler(recovery, exponentialBackOff)
    }

    @Bean
    fun operations(): KafkaOperations<String, String> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        return KafkaTemplate(DefaultKafkaProducerFactory(configProps))
    }
}