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
    val key: CheckKey,
    val tenantId: UUID,
    val pageUrl: String,
    val startDate: LocalDateTime,
    var endDate: LocalDateTime?,
    var executionStatus: ExecutionStatus,
    var checkResult: CheckResult?
) {

    /**
     * Marks the check as having failed execution (i.e. an unknown error happened and the check could not finish and
     * does not have a result.
     */
    fun setExecutionError() {
        this.executionStatus = ExecutionStatus.ERROR
        this.endDate = LocalDateTime.now()
    }

    /**
     * Marks the check as having run successfully and saves the given result.
     */
    fun setResult(result: CheckResult) {
        this.executionStatus = ExecutionStatus.SUCCESS
        this.endDate = LocalDateTime.now()
        this.checkResult = result
    }
}
