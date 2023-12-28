package org.example.challenge.utils

enum class K8SApplicationType(val value: String) {
    DEPLOYMENT("Deployment"),
    STATEFUL_SET("StatefulSet"),
    REPLICA_SET("ReplicaSet"),
    POD("Pod"),
    SERVICE("Service")

}