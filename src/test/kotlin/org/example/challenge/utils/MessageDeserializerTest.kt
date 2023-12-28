package org.example.challenge.utils

import io.fabric8.kubernetes.api.model.Pod
import org.json.JSONException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class MessageDeserializerTest {

    private val deserializer = MessageDeserializer()

    @Test
    fun `deserialize should return null for null data`() {
        val result = deserializer.deserialize("topic", null)
        assertEquals(null, result)
    }

    @Test
    fun `deserialize should throw SerializationException for invalid JSON data`() {
        val invalidData = "invalidJsonData".toByteArray()
        assertThrows(JSONException::class.java) {
            deserializer.deserialize("topic", invalidData)
        }
    }

    @Test
    fun `deserialize should return K8S resource for valid K8S JSON data`() {
        val kind = K8SApplicationType.POD.value
        val k8sData = """
            {
              "kind": "$kind",
              "apiVersion": "v1",
              "metadata": {}
            }
        """.trimIndent().toByteArray()

        val result = deserializer.deserialize("topic", k8sData)

        assertEquals(Pod::class.java, result?.javaClass)
    }
}