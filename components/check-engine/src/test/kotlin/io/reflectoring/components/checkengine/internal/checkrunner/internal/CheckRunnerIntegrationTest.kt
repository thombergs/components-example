package io.reflectoring.components.checkengine.internal.checkrunner.internal

import io.reflectoring.components.checkengine.api.CheckExecutor
import io.reflectoring.components.checkengine.api.CheckKey
import io.reflectoring.components.checkengine.api.CheckQueries
import io.reflectoring.components.checkengine.api.CheckRequest
import io.reflectoring.components.checkengine.api.CheckResult
import io.reflectoring.components.checkengine.api.ConditionKey
import io.reflectoring.components.checkengine.api.ExecutionStatus
import io.reflectoring.components.checkengine.api.Fix
import io.reflectoring.components.checkengine.api.ResultStatus
import io.reflectoring.components.checkengine.internal.checkrunner.api.CheckRunner
import io.reflectoring.components.checkengine.internal.database.api.CheckMutations
import io.reflectoring.components.checkengine.internal.database.internal.CheckEngineDatabaseConfiguration
import io.reflectoring.components.testcontainers.PostgreSQLTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import java.util.UUID

@PostgreSQLTest
@Import(CheckEngineDatabaseConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = ["debug=true"])
internal class CheckRunnerIntegrationTest {

    @Autowired
    lateinit var checkMutations: CheckMutations

    @Autowired
    lateinit var checkQueries: CheckQueries

    lateinit var checkResolver: CheckResolver

    lateinit var checkRunner: CheckRunner

    @BeforeEach
    fun init() {
        checkResolver = Mockito.mock(CheckResolver::class.java)
        checkRunner = DefaultCheckRunner(checkResolver, checkMutations)
    }

    @Test
    fun testPassedCheck() {
        // given
        val checkRequest = checkRequest()
        val passedResult = passedResult()
        val executor = Mockito.mock(CheckExecutor::class.java)
        given(checkResolver.getExecutorFor(any())).willReturn(executor)
        given(executor.execute(any(), anyString())).willReturn(passedResult)

        // when we run the check
        val check = checkRunner.runCheck(checkRequest)

        // then the check is updated in the database
        val checkInDatabase = checkQueries.getCheck(check.id)
        assertThat(checkInDatabase?.pageUrl).isEqualTo(checkRequest.pageUrl)
        assertThat(checkInDatabase?.key).isEqualTo(checkRequest.checkKey)
        assertThat(checkInDatabase?.executionStatus).isEqualTo(ExecutionStatus.SUCCESS)
        assertThat(checkInDatabase?.checkResult).isEqualTo(passedResult)
        assertThat(checkInDatabase?.startDate).isNotNull
        assertThat(checkInDatabase?.endDate).isNotNull
    }

    @Test
    fun testFailedCheck() {
        // given
        val checkRequest = checkRequest()
        val failedResult = failedResult()
        val executor = Mockito.mock(CheckExecutor::class.java)
        given(checkResolver.getExecutorFor(any())).willReturn(executor)
        given(executor.execute(any(), anyString())).willReturn(failedResult)

        // when we run the check
        val check = checkRunner.runCheck(checkRequest)

        // then the check is updated in the database
        val checkInDatabase = checkQueries.getCheck(check.id)
        assertThat(checkInDatabase?.pageUrl).isEqualTo(checkRequest.pageUrl)
        assertThat(checkInDatabase?.key).isEqualTo(checkRequest.checkKey)
        assertThat(checkInDatabase?.executionStatus).isEqualTo(ExecutionStatus.SUCCESS)
        assertThat(checkInDatabase?.checkResult).isEqualTo(failedResult)
        assertThat(checkInDatabase?.checkResult?.fixes?.size).isEqualTo(failedResult.fixes.size)
        assertThat(checkInDatabase?.startDate).isNotNull
        assertThat(checkInDatabase?.endDate).isNotNull
    }

    @Test
    fun testErroredCheck() {
        // given
        val checkRequest = checkRequest()
        val passedResult = passedResult()
        val executor = Mockito.mock(CheckExecutor::class.java)
        given(checkResolver.getExecutorFor(any())).willThrow(java.lang.IllegalStateException("BWAAAAH!"))
        given(executor.execute(any(), anyString())).willReturn(passedResult)

        // when we run the check
        val check = checkRunner.runCheck(checkRequest)

        // then the check is updated in the database
        val checkInDatabase = checkQueries.getCheck(check.id)
        assertThat(checkInDatabase?.pageUrl).isEqualTo(checkRequest.pageUrl)
        assertThat(checkInDatabase?.key).isEqualTo(checkRequest.checkKey)
        assertThat(checkInDatabase?.executionStatus).isEqualTo(ExecutionStatus.ERROR)
        assertThat(checkInDatabase?.checkResult).isNull()
        assertThat(checkInDatabase?.startDate).isNotNull
        assertThat(checkInDatabase?.endDate).isNotNull
    }

    private fun checkRequest(): CheckRequest {
        return CheckRequest(CheckKey("ABC-123"), UUID.randomUUID(), "https://page.url")
    }

    private fun passedResult(): CheckResult {
        return CheckResult(ResultStatus.PASSED)
    }

    private fun failedResult(): CheckResult {
        return CheckResult(
            ResultStatus.FAILED,
            listOf(
                Fix(ConditionKey("ABC-123-1"), "fix this"),
                Fix(ConditionKey("ABC-123-2"), "fix that")
            )
        )
    }
}
