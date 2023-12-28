package org.example.challenge.repositories

import io.fabric8.kubernetes.api.model.apps.Deployment
import org.example.challenge.entities.KubernetesResource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class KubernetesResourceRepositoryTest {

    @Autowired
    private lateinit var repository: KubernetesResourceRepository

    @Test
    fun testFindAll() {
        val resource1 = KubernetesResource("uid1", "name1", Deployment(), mutableSetOf())
        val resource2 = KubernetesResource("uid2", "name2", Deployment(), mutableSetOf())
        repository.saveAll(listOf(resource1, resource2))

        val allResources = repository.findAll()

        assertEquals(2, allResources.size)
    }

    @Test
    fun testFindByName() {
        val resourceName = "name"
        val resource = KubernetesResource("uid", resourceName, Deployment(), mutableSetOf())
        repository.save(resource)

        val resources = repository.findByName(resourceName)

        assertEquals(1, resources?.size)
        assertEquals(resourceName, resources?.get(0)?.name)
    }
}