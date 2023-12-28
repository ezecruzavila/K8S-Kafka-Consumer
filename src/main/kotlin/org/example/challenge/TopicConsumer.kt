package org.example.challenge

import io.fabric8.kubernetes.api.model.HasMetadata
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.example.challenge.services.KubernetesResourceService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class TopicConsumer(val service: KubernetesResourceService) {
    private val logger = KotlinLogging.logger {}

    @KafkaListener(topics = ["\${kafka.topic}"])
    fun consume(record: ConsumerRecord<Int, Any>) {
        service.save(record.value() as HasMetadata)
        logger.info { "Kafka Offset: ${record.offset()}" }
    }

}