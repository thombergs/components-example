package io.reflectoring.components.checkengine.internal.queue.internal

import io.reflectoring.components.checkengine.api.CheckRequest
import io.reflectoring.components.checkengine.api.CheckScheduler
import org.springframework.stereotype.Component

@Component
class SqsCheckScheduler(
    private val publisher: CheckRequestPublisher
) : CheckScheduler {

    override fun scheduleChecks(checks: List<CheckRequest>) {
        checks.forEach {
            publisher.publish(it)
        }
    }
}
