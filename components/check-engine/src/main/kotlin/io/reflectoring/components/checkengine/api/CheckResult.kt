package io.reflectoring.components.checkengine.api

enum class ResultStatus {
    PASSED,
    FAILED
}

data class Fix(
    val failedConditionKey: ConditionKey,
    val message: String,
)

data class CheckResult(
    val status: ResultStatus = ResultStatus.PASSED,
    val fixes: List<Fix> = emptyList()
)
