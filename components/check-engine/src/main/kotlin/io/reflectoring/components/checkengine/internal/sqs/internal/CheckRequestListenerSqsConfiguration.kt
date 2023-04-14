package io.reflectoring.components.checkengine.internal.sqs.internal

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import io.reflectoring.sqs.api.SqsQueueInitializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@ComponentScan
@Configuration
@EnableConfigurationProperties(CheckRequestListenerSqsProperties::class)
open class CheckRequestListenerSqsConfiguration {

    @Bean("checkRequestSqsListenerClient")
    fun checkRequestSqsClient(sqsProperties: CheckRequestListenerSqsProperties): AmazonSQS? {
        val builder = AmazonSQSClientBuilder.standard()
        if (sqsProperties.endpoint != null) {
            builder.withEndpointConfiguration(
                EndpointConfiguration(
                    sqsProperties.endpoint, sqsProperties.region
                )
            )
        } else {
            builder.withRegion(sqsProperties.region)
        }
        return builder.build()
    }

    @Bean
    fun checkRequestPublisher(
        sqsProperties: CheckRequestListenerSqsProperties,
        @Qualifier("checkRequestSqsListenerClient") sqsClient: AmazonSQS,
        objectMapper: ObjectMapper
    ): CheckRequestPublisher {
        return CheckRequestPublisher(sqsProperties.queueUrl, sqsClient, objectMapper)
    }

    @Bean
    @ConditionalOnProperty(name = ["check-engine.sqs.check-requests.init"], havingValue = "true")
    fun checkRequestsQueueInitializer(
        @Qualifier("checkRequestSqsListenerClient") sqsClient: AmazonSQS
    ): SqsQueueInitializer? {
        return SqsQueueInitializer(sqsClient, "checkRequests")
    }
}
