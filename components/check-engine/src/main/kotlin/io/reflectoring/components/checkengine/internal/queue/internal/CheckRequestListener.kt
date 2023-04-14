package io.reflectoring.components.checkengine.internal.queue.internal

import io.reflectoring.components.checkengine.api.CheckRequest
import io.reflectoring.components.checkengine.internal.checkrunner.api.CheckRunner
import io.reflectoring.sqs.api.SqsMessageHandler
import org.springframework.stereotype.Component

@Component
class CheckRequestListener(
    private val checkRunner: CheckRunner
) : SqsMessageHandler<CheckRequest> {

    override fun handle(check: CheckRequest) {
        checkRunner.runCheck(check)
    }

    override fun messageType(): Class<CheckRequest> {
        return CheckRequest::class.java
    }
}
