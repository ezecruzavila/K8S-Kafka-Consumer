package org.example.challenge.entities

import io.fabric8.kubernetes.api.model.HasMetadata
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KubernetesResourceTest {

    @Test
    fun testPrimaryConstructor() {
        val kubernetesObject = mockk<HasMetadata>()

        every { kubernetesObject.metadata.uid } returns "uid123"
        every { kubernetesObject.metadata.name } returns "TestResource"

        val kubernetesResource = KubernetesResource(kubernetesObject)

        assertEquals("uid123", kubernetesResource.id)
        assertEquals("TestResource", kubernetesResource.name)
        assertEquals(kubernetesObject, kubernetesResource.resource)
        assertEquals(emptySet<String>(), kubernetesResource.childrenIds)
    }

    @Test
    fun testSecondaryConstructor() {
        val kubernetesObject = mockk<HasMetadata>()

        every { kubernetesObject.metadata.uid } returns "uid456"
        every { kubernetesObject.metadata.name } returns "AnotherResource"

        val kubernetesResource =
            KubernetesResource("uid456", "AnotherResource", kubernetesObject, mutableSetOf("child1", "child2"))

        assertEquals("uid456", kubernetesResource.id)
        assertEquals("AnotherResource", kubernetesResource.name)
        assertEquals(kubernetesObject, kubernetesResource.resource)
        assertEquals(setOf("child1", "child2"), kubernetesResource.childrenIds)
    }
}