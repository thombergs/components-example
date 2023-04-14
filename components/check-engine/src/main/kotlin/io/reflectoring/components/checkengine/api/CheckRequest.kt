package io.reflectoring.components.checkengine.api

import java.util.UUID

/**
 * A request to run a specific check on a web page.
 */
data class CheckRequest(
    val checkKey: CheckKey,

    /**
     * ID used to segregate checks per tenant.
     */
    val tenantId: UUID,
    val pageUrl: String
)
