package org.k8s_kafka_consumer.entities

import io.fabric8.kubernetes.api.model.HasMetadata
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class KubernetesResource(
    @Id
    val id: String,
    val name: String,
    val resource: HasMetadata,

    ) {
    constructor(kubernetesObject: HasMetadata) : this(
        kubernetesObject.metadata.uid,
        kubernetesObject.metadata.name,
        kubernetesObject
    )
}