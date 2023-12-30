package org.example.challenge.integration

import org.awaitility.Awaitility.await
import org.example.challenge.repositories.KubernetesResourceRepository
import org.example.challenge.services.KubernetesResourceService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = [
        "listeners=PLAINTEXT://localhost:9092",
        "port=9092"
    ],
    topics = ["k8s-resources-test"]
)
class K8SConsumerIntegrationTest {

    @Value(value = "\${kafka.topic}")
    private lateinit var topic: String

    @Autowired
    private lateinit var repository: KubernetesResourceRepository

    lateinit var service: KubernetesResourceService

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @Autowired
    fun setRepository() {
        service = KubernetesResourceService(repository)
    }

    @Test
    fun `consume and store message on db`() {

        val uid = UUID.randomUUID().toString()
        val msg = messageWithoutOwner(uid)
        kafkaTemplate.send(topic, msg)

        await().pollInterval(Duration.ofSeconds(3)).atMost(10, TimeUnit.SECONDS)
            .untilAsserted {
                val kubernetesResource = service.findById(uid)
                assertTrue(kubernetesResource.isPresent)
                assertEquals(uid, kubernetesResource.get().id)
                assertEquals(1, service.findAll().size)
            }
    }

    @Test
    fun `consume and store 2 message, one with ownerReference`() {
        val uid1 = "uid1"
        val msg1 = messageWithoutOwner(uid1)
        val uid2 = "uid2"
        val msg2 = messageWithOwner(uid2, uid1)
        kafkaTemplate.send(topic, msg1)
        kafkaTemplate.send(topic, msg2)

        await().pollInterval(Duration.ofSeconds(3)).atMost(10, TimeUnit.SECONDS)
            .untilAsserted {
                val kubernetesResource = service.findById(uid1)
                assertTrue(kubernetesResource.isPresent)
                assertTrue(kubernetesResource.get().childrenIds.contains(uid2))
            }
    }

    private fun messageWithoutOwner(uid: String) = """
        {
            "apiVersion": "apps/v1",
            "kind": "Deployment",
            "metadata": {
                "annotations": {
                    "deployment.kubernetes.io/revision": "3",
                    "kubectl.kubernetes.io/last-applied-configuration": "{\"apiVersion\":\"apps/v1\",\"kind\":\"Deployment\",\"metadata\":{\"annotations\":{},\"labels\":{\"app\":\"chaos-controller\",\"part-of\":\"chaos-services\"},\"name\":\"chaos-controller\",\"namespace\":\"chaos-paths\"},\"spec\":{\"replicas\":1,\"selector\":{\"matchLabels\":{\"app\":\"chaos-controller\"}},\"template\":{\"metadata\":{\"labels\":{\"app\":\"chaos-controller\",\"part-of\":\"chaos-services\"}},\"spec\":{\"containers\":[{\"image\":\"public.ecr.aws/g9k7c6g2/chaos-path-controller:main\",\"imagePullPolicy\":\"Always\",\"name\":\"chaos-controller\",\"resources\":{\"limits\":{\"memory\":\"256Mi\"},\"requests\":{\"cpu\":\"250m\",\"memory\":\"256Mi\"}}}]}}}}\n"
                },
                "creationTimestamp": "2023-05-25T05:37:17Z",
                "generation": 3,
                "labels": {
                    "app": "chaos-controller",
                    "part-of": "chaos-services"
                },
                "name": "chaos-controller",
                "namespace": "chaos-paths",
                "resourceVersion": "64274569",
                "uid": "$uid"
            }
        }
    """.trimIndent()

    private fun messageWithOwner(uid: String, ownerUid: String) = """
     {
        "apiVersion": "apps/v1",
        "kind": "ReplicaSet",
        "metadata": {
            "annotations": {
                "deployment.kubernetes.io/desired-replicas": "1",
                "deployment.kubernetes.io/max-replicas": "2",
                "deployment.kubernetes.io/revision": "1"
            },
            "creationTimestamp": "2023-04-17T07:37:33Z",
            "generation": 1,
            "labels": {
                "name": "carts",
                "pod-template-hash": "7bbf9dc945"
            },
            "name": "carts-7bbf9dc945",
            "namespace": "sock-shop",
            "ownerReferences": [
                {
                    "apiVersion": "apps/v1",
                    "blockOwnerDeletion": true,
                    "controller": true,
                    "kind": "Deployment",
                    "name": "carts",
                    "uid": "$ownerUid"
                }
            ],
            "resourceVersion": "106725187",
            "uid": "$uid"
        }
    }""".trimIndent()

}


