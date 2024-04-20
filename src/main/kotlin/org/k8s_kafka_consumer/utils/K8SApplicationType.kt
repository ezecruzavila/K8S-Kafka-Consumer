package org.k8s_kafka_consumer.utils

enum class K8SApplicationType(val value: String) {
    DEPLOYMENT("Deployment"),
    STATEFUL_SET("StatefulSet"),
    REPLICA_SET("ReplicaSet"),
    POD("Pod"),
    SERVICE("Service")

}