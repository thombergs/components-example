package io.reflectoring.components.checkengine.internal.checkrunner.internal

import org.springframework.stereotype.Component

/**
 * Collects all CheckExecutor beans from the application context and provides a method to get the right check
 * for a given CheckRequest.
 */
@Component
class CheckResolver(executors: List<io.reflectoring.components.checkengine.api.CheckExecutor>) {

    private val executors: Map<io.reflectoring.components.checkengine.api.CheckKey, io.reflectoring.components.checkengine.api.CheckExecutor>

    init {
        this.executors = executors.associateBy { it.supportedCheck() }
    }

    /**
     * Returns the CheckExecutor for the given CheckRequest.
     * Throws an IllegalStateException if there is no CheckExecutor for the given CheckRequest.
     */
    fun getExecutorFor(checkRequest: io.reflectoring.components.checkengine.api.CheckRequest): io.reflectoring.components.checkengine.api.CheckExecutor {
        return executors[checkRequest.checkKey]
            ?: throw io.reflectoring.components.checkengine.internal.checkrunner.internal.ExecutorNotFoundException(
                checkRequest.checkKey
            )
    }
}

class ExecutorNotFoundException(checkKey: io.reflectoring.components.checkengine.api.CheckKey) :
    java.lang.RuntimeException("could not find CheckExecutor for check with key $checkKey")
