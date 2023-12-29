package org.example.challenge

import io.fabric8.kubernetes.api.model.HasMetadata
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.protocol.Message
import org.example.challenge.services.KubernetesResourceService
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component


@Component
class K8SKafkaConsumer(val service: KubernetesResourceService) {
    private val logger = KotlinLogging.logger {}

    @KafkaListener(topics = ["\${kafka.topic}"], groupId = "k8s", containerFactory = "k8sListenerContainerFactory")
    fun consume(record: ConsumerRecord<Int, Any>, ack: Acknowledgment) {
        logger.info { "Received message with offset: ${record.offset()}" }
        try {
            service.save(record.value() as HasMetadata)
            logger.info { "Successfully processed message with offset: ${record.offset()}" }
        } catch (e: Exception) {
            logger.error { "Failed to proccess message with offset: ${record.offset()}" }
            logger.error { "Failed message: ${record.value()}" }
        }
        ack.acknowledge()
    }

    @DltHandler
    fun dltHandler(
        msg: Message,
        @Header(KafkaHeaders.ORIGINAL_OFFSET) offset: Long,
        @Header(KafkaHeaders.EXCEPTION_FQCN) ex: String,
        @Header(KafkaHeaders.EXCEPTION_STACKTRACE) exStackTrace: String,
        @Header(KafkaHeaders.EXCEPTION_MESSAGE) exMessage: String
    ) {
        logger.error { "Message sent to DLT: $msg" }
        logger.error { "Exception: $ex" }
        logger.error { "Exception StackTrace: $exStackTrace" }
        logger.error { "Exception: $exMessage" }

    }

}