package io.diegorxramos.kafkademo.application

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class DemoListener(
    val kafkaTemplate: KafkaTemplate<String, String>
) {

    /*@PostConstruct
    fun init() {
        kafkaTemplate.send("kafka-demo-topic-1","kafka-demo", "hello world")
    }*/

    @KafkaListener(topics = ["kafka-demo-topic-1"], groupId = "kafka-demo")
    fun onListener(@Payload message: String) {
        throw IllegalArgumentException("throw exception message=${message}")
    }
}