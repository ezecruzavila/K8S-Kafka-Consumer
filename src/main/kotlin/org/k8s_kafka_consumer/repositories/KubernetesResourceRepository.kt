package org.k8s_kafka_consumer.repositories

import org.k8s_kafka_consumer.entities.KubernetesResource
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface KubernetesResourceRepository : CrudRepository<KubernetesResource, String> {

    @Query("SELECT a FROM KubernetesResource a")
    override fun findAll(): List<KubernetesResource>

    fun findByName(name: String): List<KubernetesResource>?
}