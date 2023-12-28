package org.example.challenge.repositories

import org.example.challenge.entities.KubernetesResource
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface KubernetesResourceRepository : CrudRepository<KubernetesResource, String> {


    @Query("SELECT a FROM KubernetesResource a")
    override fun findAll(): List<KubernetesResource>

    fun findByName(name: String): List<KubernetesResource>?
}