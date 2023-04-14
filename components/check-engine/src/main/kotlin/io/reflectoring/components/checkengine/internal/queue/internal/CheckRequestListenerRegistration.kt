package io.reflectoring.components.checkengine.internal.queue.internal

import com.amazonaws.services.sqs.AmazonSQS
import com.fasterxml.jackson.databind.ObjectMapper
import io.reflectoring.components.checkengine.api.CheckRequest
import io.reflectoring.sqs.api.DefaultExceptionHandler
import io.reflectoring.sqs.api.DefaultSqsMessageHandlerRegistration
import io.reflectoring.sqs.api.SqsMessageHandler
import io.reflectoring.sqs.api.SqsMessagePollerProperties
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class CheckRequestListenerRegistration(
    private val eventListener: CheckRequestListener,
    @Value("\${check-engine.sqs.check-requests.queueUrl}") private val queueUrl: String,
    @Value("\${check-engine.sqs.check-requests.pollDelay}") private val pollDelay: Duration,
    @Qualifier("checkRequestSqsListenerClient") private val sqsClient: AmazonSQS,
    private val objectMapper: ObjectMapper
) : DefaultSqsMessageHandlerRegistration<io.reflectoring.components.checkengine.api.CheckRequest>() {

    override fun messageHandler(): SqsMessageHandler<io.reflectoring.components.checkengine.api.CheckRequest> {
        return eventListener
    }

    override fun name(): String {
        return "checkRequests"
    }

    override fun messagePollerProperties(): SqsMessagePollerProperties {
        return SqsMessagePollerProperties(queueUrl)
            .withExceptionHandler(DefaultExceptionHandler())
            .withPollDelay(pollDelay)
    }

    override fun sqsClient(): AmazonSQS {
        return sqsClient
    }

    override fun objectMapper(): ObjectMapper {
        return objectMapper
    }
}
