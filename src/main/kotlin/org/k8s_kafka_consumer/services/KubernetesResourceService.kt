package org.k8s_kafka_consumer.services

import io.fabric8.kubernetes.api.model.HasMetadata
import org.k8s_kafka_consumer.dtos.KubernetesResourceDTO
import org.k8s_kafka_consumer.entities.KubernetesResource
import org.k8s_kafka_consumer.repositories.KubernetesResourceRepository
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional


@Service
class KubernetesResourceService(
    private val repository: KubernetesResourceRepository
) {

    @Transactional
    fun save(resource: HasMetadata): KubernetesResourceDTO {
        val kubernetesResource = repository.save(KubernetesResource(resource))
        return KubernetesResourceDTO(kubernetesResource)
    }

    fun findAll(): List<KubernetesResourceDTO> {
        return repository.findAll().map { KubernetesResourceDTO(it) }
    }

    fun findById(id: String): Optional<KubernetesResourceDTO> {
        return repository.findById(id).map { KubernetesResourceDTO(it) }
    }

}