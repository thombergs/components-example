package io.reflectoring.components.checkengine.internal.sqs.internal

import io.reflectoring.components.checkengine.api.CheckRequest
import io.reflectoring.components.checkengine.internal.checkrunner.api.CheckRunner
import io.reflectoring.sqs.api.SqsMessageHandler
import org.springframework.stereotype.Component

@Component
class CheckRequestListener(
    private val checkRunner: io.reflectoring.components.checkengine.internal.checkrunner.api.CheckRunner
) : SqsMessageHandler<io.reflectoring.components.checkengine.api.CheckRequest> {

    override fun handle(check: io.reflectoring.components.checkengine.api.CheckRequest) {
        checkRunner.runCheck(check)
    }

    override fun messageType(): Class<io.reflectoring.components.checkengine.api.CheckRequest> {
        return io.reflectoring.components.checkengine.api.CheckRequest::class.java
    }
}
