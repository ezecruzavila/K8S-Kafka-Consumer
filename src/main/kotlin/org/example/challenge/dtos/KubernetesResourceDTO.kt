package org.example.challenge.dtos

import io.fabric8.kubernetes.api.model.HasMetadata
import org.example.challenge.entities.KubernetesResource

data class KubernetesResourceDTO(
    val id: String,
    val name: String,
    val resource: HasMetadata,
    val childrenIds: MutableSet<String>
) {
    constructor(kubernetesResource: KubernetesResource) : this(
        kubernetesResource.id,
        kubernetesResource.name,
        kubernetesResource.resource,
        kubernetesResource.childrenIds
    )
}