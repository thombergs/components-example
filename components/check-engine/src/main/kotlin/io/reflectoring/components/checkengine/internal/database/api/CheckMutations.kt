package io.reflectoring.components.checkengine.internal.database.api

import io.reflectoring.components.checkengine.api.Check
import io.reflectoring.components.checkengine.api.CheckRequest

interface CheckMutations {

    /**
     * Inserts a new check in the database.
     */
    fun initializeCheck(checkRequest: io.reflectoring.components.checkengine.api.CheckRequest): io.reflectoring.components.checkengine.api.Check

    fun updateCheck(check: io.reflectoring.components.checkengine.api.Check)
}
