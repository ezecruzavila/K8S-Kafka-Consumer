package org.k8s_kafka_consumer.integration

import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.k8s_kafka_consumer.repositories.KubernetesResourceRepository
import org.k8s_kafka_consumer.services.KubernetesResourceService
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

}


