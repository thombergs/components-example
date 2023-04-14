package io.reflectoring.components.checkengine.internal.sqs.internal

import io.reflectoring.components.checkengine.api.CheckRequest
import io.reflectoring.components.checkengine.api.CheckScheduler
import org.springframework.stereotype.Component

@Component
class SqsCheckScheduler(
    private val publisher: CheckRequestPublisher
) : io.reflectoring.components.checkengine.api.CheckScheduler {

    override fun requestChecks(checks: List<io.reflectoring.components.checkengine.api.CheckRequest>) {
        checks.forEach {
            publisher.publish(it)
        }
    }
}
