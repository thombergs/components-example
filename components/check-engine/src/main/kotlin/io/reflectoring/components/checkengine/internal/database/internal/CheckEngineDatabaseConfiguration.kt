package io.reflectoring.components.checkengine.internal.database.internal

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

import io.reflectoring.components.common.database.JooqAutoConfiguration

@Configuration
@ComponentScan
@Import(JooqAutoConfiguration::class)
class CheckEngineDatabaseConfiguration
