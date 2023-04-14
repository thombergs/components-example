package io.reflectoring.components.checkengine.api

enum class ResultStatus {
    PASSED,
    FAILED
}

data class Fix(
    val failedConditionKey: io.reflectoring.components.checkengine.api.ConditionKey,
    val message: String,
)

data class CheckResult(
    val status: io.reflectoring.components.checkengine.api.ResultStatus = io.reflectoring.components.checkengine.api.ResultStatus.PASSED,
    val fixes: List<io.reflectoring.components.checkengine.api.Fix> = emptyList()
)
