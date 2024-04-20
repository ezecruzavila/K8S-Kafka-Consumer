package org.k8s_kafka_consumer.consumers

import io.fabric8.kubernetes.api.model.apps.Deployment
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.k8s_kafka_consumer.dtos.KubernetesResourceDTO
import org.k8s_kafka_consumer.services.KubernetesResourceService
import org.springframework.kafka.support.Acknowledgment

class K8SConsumerTest {

    @MockK
    lateinit var service: KubernetesResourceService

    @MockK
    lateinit var acknowledgment: Acknowledgment

    private lateinit var consumer: K8SConsumer

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        consumer = K8SConsumer(service)
    }

    @Test
    fun `should consume and acknowledge message successfully`() {
        val record = createConsumerRecord()

        every { service.save(any()) } returns mockk<KubernetesResourceDTO>()
        every { acknowledgment.acknowledge() } returns Unit
        consumer.consume(record, acknowledgment)

        verify(exactly = 1) { service.save(any()) }
        verify(exactly = 1) { acknowledgment.acknowledge() }
    }

    @Test
    fun `should handle exception and acknowledge message`() {
        val record = createConsumerRecord()

        every { service.save(any()) } throws RuntimeException("Simulated error")
        every { acknowledgment.acknowledge() } returns Unit
        consumer.consume(record, acknowledgment)

        verify(exactly = 1) { service.save(any()) }
        verify(exactly = 1) { acknowledgment.acknowledge() }
    }


    private fun createConsumerRecord(): ConsumerRecord<Int, Any> {
        return ConsumerRecord("k8s-test", 1, 0, 1, Deployment())
    }
}