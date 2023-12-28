package org.example.challenge.controllers

import org.example.challenge.entities.KubernetesResource
import org.example.challenge.repositories.KubernetesResourceRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/kubernetes")
class KubernetesController(
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