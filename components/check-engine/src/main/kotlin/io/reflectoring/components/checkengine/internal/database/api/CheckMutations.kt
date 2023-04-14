package io.reflectoring.components.checkengine.internal.database.api

import io.reflectoring.components.checkengine.api.Check
import io.reflectoring.components.checkengine.api.CheckRequest

interface CheckMutations {

    /**
     * Inserts a new check in the database.
     */
    fun initializeCheck(checkRequest: CheckRequest): Check

    fun updateCheck(check: Check)
}
