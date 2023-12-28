package org.example.challenge.services

import io.fabric8.kubernetes.api.model.ObjectMeta
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.challenge.entities.KubernetesResource
import org.example.challenge.repositories.KubernetesResourceRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional

@Transactional
class KubernetesResourceServiceTest {

    private val repository = mockk<KubernetesResourceRepository>()
    private val service = KubernetesResourceService(repository)

    @Test
    fun `save should call repository save`() {
        val mockResource = Deployment()
        mockResource.metadata = ObjectMeta()
        mockResource.metadata.uid = "uid"
        mockResource.metadata.name = "name"
        val kubernetesResource = KubernetesResource(mockResource)
        every { repository.save(any()) } returns kubernetesResource
        service.save(mockResource)
        verify(exactly = 1) { repository.save(any()) }
    }

    @Test
    fun `save should return the saved resource`() {
        val mockResource = Deployment()
        mockResource.metadata = ObjectMeta()
        mockResource.metadata.uid = "uid"
        mockResource.metadata.name = "name"
        val savedResource = KubernetesResource(mockResource)
        every { repository.save(any()) } returns savedResource
        val result = service.save(mockResource)
        assertEquals(savedResource, result)
    }

}
