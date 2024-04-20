package org.k8s_kafka_consumer.entities

import io.fabric8.kubernetes.api.model.HasMetadata
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class KubernetesResourceTest {

    @Test
    fun testConstructor() {
        val kubernetesObject = mockk<HasMetadata>()

        val uid = UUID.randomUUID().toString()
        every { kubernetesObject.metadata.uid } returns uid
        every { kubernetesObject.metadata.name } returns "TestResource"

        val kubernetesResource = KubernetesResource(kubernetesObject)

        assertEquals(uid, kubernetesResource.id)
        assertEquals("TestResource", kubernetesResource.name)
        assertEquals(kubernetesObject, kubernetesResource.resource)
    }

}