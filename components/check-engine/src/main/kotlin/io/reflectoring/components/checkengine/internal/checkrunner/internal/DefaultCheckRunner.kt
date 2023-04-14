package io.reflectoring.components.checkengine.internal.checkrunner.internal

import io.reflectoring.components.checkengine.internal.database.api.CheckMutations
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DefaultCheckRunner(
    private val checkResolver: CheckResolver,
    private val checkMutations: CheckMutations
) : io.reflectoring.components.checkengine.internal.checkrunner.api.CheckRunner {

    private val logger = LoggerFactory.getLogger(DefaultCheckRunner::class.java)

    override fun runCheck(checkRequest: io.reflectoring.components.checkengine.api.CheckRequest): io.reflectoring.components.checkengine.api.Check {
        val check = checkMutations.initializeCheck(checkRequest)

        try {
            val executor = checkResolver.getExecutorFor(checkRequest)
            val result = executor.execute(checkRequest.tenantId, checkRequest.pageUrl)
            check.setResult(result)
            checkMutations.updateCheck(check)
        } catch (e: Exception) {
            logger.error("check with id ${check.id} failed: $checkRequest", e)
            check.setExecutionError()
            checkMutations.updateCheck(check)
        }
        return check
    }
}
