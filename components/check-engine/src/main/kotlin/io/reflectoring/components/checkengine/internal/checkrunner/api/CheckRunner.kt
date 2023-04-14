package io.reflectoring.components.checkengine.internal.checkrunner.api

import io.reflectoring.components.checkengine.api.Check
import io.reflectoring.components.checkengine.api.CheckRequest

interface CheckRunner {

    fun runCheck(checkRequest: io.reflectoring.components.checkengine.api.CheckRequest): io.reflectoring.components.checkengine.api.Check
}
