package org.example.challenge.entities

import com.fasterxml.jackson.annotation.JsonInclude
import io.fabric8.kubernetes.api.model.HasMetadata
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.Id

/* Since all the message from the topic are from K8S I created an entity that's tightly related to the Kubernetes object
   for a better abstraction there are 2 options, creating different tables and storing the data based on the origin of
   the resource or creating an Object to replace the HasMetadata interface that store the desired values from
   the resource as not all the fields might of importance
*/
@Entity(name = "KubernetesResource")
class KubernetesResource(
    @Id
    val id: String,
    val name: String,
    val resource: HasMetadata,
    @ElementCollection
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var childrenIds: MutableSet<String>
) {
    constructor(kubernetesObject: HasMetadata) : this(
        kubernetesObject.metadata.uid,
        kubernetesObject.metadata.name,
        kubernetesObject,
        mutableSetOf()
    )
}