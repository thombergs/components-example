package io.reflectoring.components.checkengine.internal.checkrunner.internal

import io.reflectoring.components.checkengine.api.CheckExecutor
import io.reflectoring.components.checkengine.api.CheckKey
import io.reflectoring.components.checkengine.api.CheckRequest
import org.springframework.stereotype.Component

/**
 * Collects all CheckExecutor beans from the application context and provides a method to get the right check
 * for a given CheckRequest.
 */
@Component
class CheckResolver(executors: List<CheckExecutor>) {

    private val executors: Map<CheckKey, CheckExecutor>

    init {
        this.executors = executors.associateBy { it.supportedCheck() }
    }

    /**
     * Returns the CheckExecutor for the given CheckRequest.
     * Throws an IllegalStateException if there is no CheckExecutor for the given CheckRequest.
     */
    fun getExecutorFor(checkRequest: CheckRequest): CheckExecutor {
        return executors[checkRequest.checkKey]
            ?: throw ExecutorNotFoundException(
                checkRequest.checkKey
            )
    }
}

class ExecutorNotFoundException(checkKey: CheckKey) :
    java.lang.RuntimeException("could not find CheckExecutor for check with key $checkKey")
