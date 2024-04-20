package org.k8s_kafka_consumer.controllers

import org.k8s_kafka_consumer.entities.KubernetesResource
import org.k8s_kafka_consumer.repositories.KubernetesResourceRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/kubernetes")
class TestController(
    private val repository: KubernetesResourceRepository
) {

    @GetMapping("/resources")
    fun getAllResources(): List<KubernetesResource> {
        return repository.findAll().toList()
    }

    @GetMapping("/resourcesCount")
    fun count(): Long {
        return repository.count()
    }

}