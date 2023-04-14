package io.reflectoring.components.checkengine.internal.queue.internal

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("check-engine.sqs.check-requests")
class CheckRequestListenerSqsProperties(
    val endpoint: String? = null,
    val region: String? = "us-east",
    val queueUrl: String
)
