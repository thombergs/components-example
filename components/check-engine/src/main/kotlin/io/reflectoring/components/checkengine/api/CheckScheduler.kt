package io.reflectoring.components.checkengine.api

interface CheckScheduler {

    /**
     * Schedules the given checks to be executed asynchronously.
     */
    fun scheduleChecks(checks: List<CheckRequest>)
}
