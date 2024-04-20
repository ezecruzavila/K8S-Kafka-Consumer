package org.k8s_kafka_consumer.repositories

import io.fabric8.kubernetes.api.model.apps.Deployment
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.k8s_kafka_consumer.entities.KubernetesResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.util.*

@DataJpaTest
class KubernetesResourceRepositoryTest {

    @Autowired
    private lateinit var em: TestEntityManager

    @Autowired
    private lateinit var repository: KubernetesResourceRepository

    @Test
    fun testFindAll() {
        val resource1 = KubernetesResource(UUID.randomUUID().toString(), "name1", Deployment())
        val resource2 = KubernetesResource(UUID.randomUUID().toString(), "name2", Deployment())
        repository.saveAll(listOf(resource1, resource2))
        val allResources = repository.findAll()
        assertEquals(2, allResources.size)
    }

    @Test
    fun testFindByName() {
        val resourceName = "name"
        val resource = KubernetesResource(UUID.randomUUID().toString(), resourceName, Deployment())
        repository.save(resource)
        val resources = repository.findByName(resourceName)
        assertEquals(1, resources?.size)
        assertEquals(resourceName, resources?.get(0)?.name)
    }
}