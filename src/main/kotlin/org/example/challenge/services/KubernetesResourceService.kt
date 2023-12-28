package org.example.challenge.services

import io.fabric8.kubernetes.api.model.HasMetadata
import org.example.challenge.entities.KubernetesResource
import org.example.challenge.repositories.KubernetesResourceRepository
import org.example.challenge.utils.K8SApplicationType
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class KubernetesResourceService(
    private val repository: KubernetesResourceRepository
) {

    @Transactional
    fun save(resource: HasMetadata): KubernetesResource {
        val kubernetesResource = repository.save(KubernetesResource(resource))
        saveOwnerReferences(kubernetesResource)
        saveServiceOwnerReference(kubernetesResource)
        return kubernetesResource
    }

    private fun saveOwnerReferences(kubernetesResource: KubernetesResource) {
        kubernetesResource.resource.metadata.ownerReferences.forEach {
            val owner = repository.findById(it.uid)
            if (owner.isPresent) {
                owner.get().childrenIds.add(it.uid)
                repository.save(owner.get())
            }
        }
    }

    private fun saveServiceOwnerReference(kubernetesResource: KubernetesResource) {
        if (kubernetesResource.resource.kind == K8SApplicationType.SERVICE.value) {
            val service = kubernetesResource.resource as io.fabric8.kubernetes.api.model.Service
            val name = service.spec.selector["name"]
            if (name != null) {
                val foundResources = repository.findByName(name)
                if (foundResources != null) {
                    val owners =
                        foundResources.filter { it.resource.kind == K8SApplicationType.DEPLOYMENT.value || it.resource.kind == K8SApplicationType.STATEFUL_SET.value }
                    if (owners.isNotEmpty()) {
                        owners.forEach {
                            it.childrenIds.add(kubernetesResource.id)
                            repository.save(it)
                        }

                    }
                }
            }
        }
    }

}