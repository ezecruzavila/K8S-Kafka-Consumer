package org.example.challenge.services

import io.fabric8.kubernetes.api.model.*
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.challenge.entities.KubernetesResource
import org.example.challenge.repositories.KubernetesResourceRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*
import javax.transaction.Transactional
import kotlin.collections.HashMap


class KubernetesResourceServiceTest {

    private val repository: KubernetesResourceRepository = mockk()
    private val service= KubernetesResourceService(repository)

    @Test
    fun `test save owner and children resource, invokes repository 3 times`() {
        val resourceOwner = Deployment()
        resourceOwner.metadata = ObjectMeta()
        resourceOwner.metadata.uid = "uid"
        resourceOwner.metadata.name = "name1"

        val resourceChildren = Deployment()
        resourceChildren.metadata = ObjectMeta()
        resourceChildren.metadata.uid = "uid2"
        resourceChildren.metadata.name = "name2"
        val ownerReference = OwnerReference()
        ownerReference.uid = resourceOwner.metadata.uid
        ownerReference.name = resourceOwner.metadata.name
        resourceChildren.metadata.ownerReferences = listOf(ownerReference)
        mockk<KubernetesResource>()
        every { repository.save(any()) } returns KubernetesResource(resourceOwner)
        service.save(resourceOwner)
        every { repository.save(any()) } returns KubernetesResource(resourceChildren)
        every { repository.findById(any()) } returns Optional.of(KubernetesResource(resourceOwner))
        service.save(resourceChildren)

        verify(exactly = 3) { repository.save(any()) }
    }

    @Test
    fun `test save owner and service children resource, invokes repository 3 times`() {
        val resourceOwner = Deployment()
        resourceOwner.metadata = ObjectMeta()
        resourceOwner.metadata.uid = "uid"
        resourceOwner.metadata.name = "name1"

        val resourceChildren = Service()
        resourceChildren.metadata = ObjectMeta()
        resourceChildren.metadata.uid = "uid2"
        resourceChildren.metadata.name = "name2"
        resourceChildren.spec = ServiceSpec()
        resourceChildren.spec.selector = HashMap<String,String>()
        resourceChildren.spec.selector.put("name",resourceOwner.metadata.name)
        mockk<KubernetesResource>()
        every { repository.save(any()) } returns KubernetesResource(resourceOwner)
        service.save(resourceOwner)
        every { repository.save(any()) } returns KubernetesResource(resourceChildren)
        every { repository.findByName(any()) } returns listOf(KubernetesResource(resourceOwner))
        service.save(resourceChildren)

        verify(exactly = 3) { repository.save(any()) }

    }

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
        assertEquals(savedResource, result)
    }


    private fun mockDeploymentResource(): HasMetadata {
        val mockResource = Deployment()
        mockResource.metadata = ObjectMeta()
        mockResource.metadata.uid = "uid"
        mockResource.metadata.name = "name"
        return mockResource
    }

}
