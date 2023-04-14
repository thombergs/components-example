package io.reflectoring.components.checkengine.internal.queue.internal

import com.amazonaws.services.sqs.AmazonSQS
import com.fasterxml.jackson.databind.ObjectMapper
import io.reflectoring.components.checkengine.api.CheckRequest
import io.reflectoring.sqs.api.SqsMessagePublisher

class CheckRequestPublisher(
    sqsQueueUrl: String?,
    sqsClient: AmazonSQS?,
    objectMapper: ObjectMapper?
) : SqsMessagePublisher<io.reflectoring.components.checkengine.api.CheckRequest?>(sqsQueueUrl, sqsClient, objectMapper)
