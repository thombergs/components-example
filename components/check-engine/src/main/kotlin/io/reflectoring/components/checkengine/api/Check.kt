package io.reflectoring.components.checkengine.api

import java.time.LocalDateTime
import java.util.UUID

enum class ExecutionStatus {
    IN_PROGRESS,
    SUCCESS,
    ERROR
}

class Check(
    val id: CheckId,
    val key: io.reflectoring.components.checkengine.api.CheckKey,
    val tenantId: UUID,
    val pageUrl: String,
    val startDate: LocalDateTime,
    var endDate: LocalDateTime?,
    var executionStatus: io.reflectoring.components.checkengine.api.ExecutionStatus,
    var checkResult: io.reflectoring.components.checkengine.api.CheckResult?
) {

    /**
     * Marks the check as having failed execution (i.e. an unknown error happened and the check could not finish and
     * does not have a result.
     */
    fun setExecutionError() {
        this.executionStatus = io.reflectoring.components.checkengine.api.ExecutionStatus.ERROR
        this.endDate = LocalDateTime.now()
    }

    /**
     * Marks the check as having run successfully and saves the given result.
     */
    fun setResult(result: io.reflectoring.components.checkengine.api.CheckResult) {
        this.executionStatus = io.reflectoring.components.checkengine.api.ExecutionStatus.SUCCESS
        this.endDate = LocalDateTime.now()
        this.checkResult = result
    }
}
