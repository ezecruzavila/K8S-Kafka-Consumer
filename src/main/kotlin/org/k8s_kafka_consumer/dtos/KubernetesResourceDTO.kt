package org.k8s_kafka_consumer.dtos

import io.fabric8.kubernetes.api.model.HasMetadata
import org.k8s_kafka_consumer.entities.KubernetesResource

data class KubernetesResourceDTO(
    val id: String,
    val name: String,
    val resource: HasMetadata,
) {
    constructor(kubernetesResource: KubernetesResource) : this(
        kubernetesResource.id,
        kubernetesResource.name,
        kubernetesResource.resource,
    )
}