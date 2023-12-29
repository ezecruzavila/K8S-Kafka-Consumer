package org.example.challenge.utils

import com.fasterxml.jackson.databind.ObjectMapper
import io.fabric8.kubernetes.api.model.HasMetadata
import mu.KotlinLogging
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.json.JSONObject

// For a better abstraction, the Deserializer should be Deserializer<Payload> where Payload stores the important information
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
            val jsonObject = deserializeAsJson(data)
            // Here I would check the json for any prop that would let me know what kind of message it is
            return when {
                isK8SResource(jsonObject) -> deserializeAsK8SResource(data)
                // isAWSLambdaResource(jsonObject) -> deserializeAsAWSLambda(data)
                else -> {
                    throw SerializationException("Message doesn't match any specification")
                }
            }
        } catch (e: Exception) {
            throw SerializationException("Error when deserializing kafka message into class")
        }
    }

    override fun close() {}

    private fun deserializeAsJson(data: ByteArray): JSONObject {
        logger.info { "Deserializing message as K8S Resource..." }
        return JSONObject(String(data, charset("UTF-8")))
    }

    private fun isK8SResource(json: JSONObject): Boolean {
        //This could be a simpler check if the message format is known and expected
        return json.has("kind") && json.has("metadata")
    }

    private fun deserializeAsK8SResource(data: ByteArray): HasMetadata {
        try {
            return objectMapper.readValue(String(data, charset("UTF-8")), HasMetadata::class.java)
        } catch (e: Exception) {
            throw SerializationException("Error when deserializing kafka message into Kubernetes Resource")
        }

    }

    /*
    private fun isAWSLambdaResource(json: JSONObject): Boolean {
        // Here I would check for anything that lets me know it's an AWS Lambda
        return false
    }

    private fun deserializeAsAWSLambda(data: ByteArray?): Any {
        // Here I would deserialize the JSON into some AWS Lambda object
        return Any()
    }
    */
}