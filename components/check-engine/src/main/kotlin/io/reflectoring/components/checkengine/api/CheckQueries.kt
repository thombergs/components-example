package io.reflectoring.components.checkengine.api

import java.util.UUID

interface CheckQueries {

    fun getCheck(checkId: CheckId): io.reflectoring.components.checkengine.api.Check?

    /**
     * Returns the number of checks for a given site and status. Only the latest checks of each type are considered.
     */
    fun getLatestChecksCountByStatus(tenantId: UUID, status: io.reflectoring.components.checkengine.api.ResultStatus): Int

    /**
     * Returns a ChecksSummary for each page of a given site.
     */
    fun getLatestChecksSummaries(tenantId: UUID): List<io.reflectoring.components.checkengine.api.ChecksSummary>

    /**
     * Returns the latest checks of each type for a given site and page.
     */
    fun getLatestChecksByPage(tenantId: UUID, pageUrl: String): List<io.reflectoring.components.checkengine.api.Check>
}
