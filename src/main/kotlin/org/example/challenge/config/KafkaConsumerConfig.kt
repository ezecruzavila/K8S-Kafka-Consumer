package org.example.challenge.config

import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.StringDeserializer
import org.example.challenge.utils.MessageDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.util.backoff.BackOff
import org.springframework.util.backoff.FixedBackOff
import java.net.SocketTimeoutException


@Configuration
@EnableKafka
class KafkaConsumerConfig {

    @Value(value = "\${kafka.bootstrapAddress}")
    private lateinit var bootstrapAddress: String

    @Value(value = "\${kafka.groupId}")
    private lateinit var groupId: String

    private val logger = KotlinLogging.logger {}

    @Bean
    fun consumerFactory(): ConsumerFactory<String, Any> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        props[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = MessageDeserializer::class.java
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun errorHandler(): DefaultErrorHandler {
        val fixedBackOff: BackOff = FixedBackOff(1000L, 9)
        val errorHandler = DefaultErrorHandler({ record, exception ->
            logger.warn { "Exception: $exception" }
            logger.info { "Recovered: $record" }
        }, fixedBackOff)
        errorHandler.addRetryableExceptions(SocketTimeoutException::class.java)
        errorHandler.addNotRetryableExceptions(NullPointerException::class.java)
        errorHandler.addNotRetryableExceptions(SerializationException::class.java)
        return errorHandler
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL)
        factory.setCommonErrorHandler(errorHandler())
        factory.afterPropertiesSet()
        factory.setConcurrency(3)
        factory.consumerFactory = consumerFactory()
        return factory
    }

    @Bean
    fun k8sListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> {
        return kafkaListenerContainerFactory()
    }

}