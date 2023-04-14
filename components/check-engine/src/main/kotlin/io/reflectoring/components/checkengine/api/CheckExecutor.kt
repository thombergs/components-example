package io.reflectoring.components.checkengine.api

import java.util.UUID

/**
 * Executes a certain check on a given web page and returns a CheckResult.
 * All CheckExecutors in the Spring application context will automatically be made available to the check engine.
 */
interface CheckExecutor {

    /**
     * Returns the key of the check this executor covers.
     */
    fun supportedCheck(): io.reflectoring.components.checkengine.api.CheckKey

    /**
     * Executes the check for the given page.
     * Any exception that is thrown by this method will mark the check as unsuccessfully executed.
     */
    fun execute(tenantId: UUID, pageUrl: String): io.reflectoring.components.checkengine.api.CheckResult
}
