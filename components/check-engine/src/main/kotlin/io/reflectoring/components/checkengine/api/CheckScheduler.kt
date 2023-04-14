package io.reflectoring.components.checkengine.api

interface CheckScheduler {

    /**
     * Schedules the given checks to be executed asynchronously.
     */
    fun requestChecks(checks: List<io.reflectoring.components.checkengine.api.CheckRequest>)
}
