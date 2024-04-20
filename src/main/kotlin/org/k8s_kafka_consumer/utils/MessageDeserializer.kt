package org.k8s_kafka_consumer.utils

import com.fasterxml.jackson.databind.ObjectMapper
import io.fabric8.kubernetes.api.model.HasMetadata
import mu.KotlinLogging
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer

class MessageDeserializer : Deserializer<Any?> {
    private val logger = KotlinLogging.logger {}
    private val objectMapper = ObjectMapper()
    override fun configure(configs: Map<String?, *>?, isKey: Boolean) {}
    override fun deserialize(topic: String?, data: ByteArray?): Any? {
        if (data == null) {
            logger.info { "Null received at deserializing" }
            return null
        }
        try {
            logger.info { "Deserializing message..." }
            return deserializeAsK8SResource(data)
        } catch (e: Exception) {
            throw SerializationException("Error when deserializing kafka message into class")
        }
    }

    override fun close() {}

    private fun deserializeAsK8SResource(data: ByteArray): HasMetadata {
        try {
            return objectMapper.readValue(String(data, charset("UTF-8")), HasMetadata::class.java)
        } catch (e: Exception) {
            throw SerializationException("Error when deserializing kafka message into Kubernetes Resource")
        }

    }
}