package org.k8s_kafka_consumer.services

import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.ObjectMeta
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.k8s_kafka_consumer.entities.KubernetesResource
import org.k8s_kafka_consumer.repositories.KubernetesResourceRepository
import java.util.*


class KubernetesResourceServiceTest {

    private val repository: KubernetesResourceRepository = mockk()
    private val service = KubernetesResourceService(repository)

    @Test
    fun `save should call repository save`() {
        val mockResource = mockDeploymentResource()
        val kubernetesResource = KubernetesResource(mockResource)
        every { repository.save(any()) } returns kubernetesResource
        service.save(mockResource)
        verify(exactly = 1) { repository.save(any()) }
    }

    @Test
    fun `save should return the saved resource`() {
        val mockResource = mockDeploymentResource()
        val savedResource = KubernetesResource(mockResource)
        every { repository.save(any()) } returns savedResource
        val result = service.save(mockResource)
        assertEquals(savedResource.resource, result.resource)
    }


    private fun mockDeploymentResource(): HasMetadata {
        val mockResource = Deployment()
        mockResource.metadata = ObjectMeta()
        mockResource.metadata.uid = UUID.randomUUID().toString()
        mockResource.metadata.name = "name"
        return mockResource
    }

}
