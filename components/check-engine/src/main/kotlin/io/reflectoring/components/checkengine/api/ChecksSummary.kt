package io.reflectoring.components.checkengine.api

import java.time.LocalDateTime

data class ChecksSummary(
    val pageUrl: String,
    val lastScanned: LocalDateTime,
    val passedChecks: Int,
    val failedChecks: Int
)
