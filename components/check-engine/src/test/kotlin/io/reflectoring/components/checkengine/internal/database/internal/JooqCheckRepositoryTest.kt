package io.reflectoring.components.checkengine.internal.database.internal

import io.reflectoring.components.checkengine.api.CheckKey
import io.reflectoring.components.checkengine.api.CheckRequest
import io.reflectoring.components.checkengine.api.CheckResult
import io.reflectoring.components.checkengine.api.ResultStatus
import io.reflectoring.components.testcontainers.PostgreSQLTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import java.util.UUID

@PostgreSQLTest
@Import(CheckEngineDatabaseConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
internal class JooqCheckRepositoryTest {

    @Autowired
    lateinit var repository: JooqCheckRepository

    @Test
    fun getCheckCountByStatus() {
        val tenantId = UUID.randomUUID()
        val check1 = repository.initializeCheck(CheckRequest(CheckKey("ABC-123"), tenantId, "https://page1.url"))
        val check2 = repository.initializeCheck(CheckRequest(CheckKey("ABC-123"), tenantId, "https://page2.url"))
        val check3 = repository.initializeCheck(CheckRequest(CheckKey("SEO-42"), tenantId, "https://page1.url"))
        val check4 = repository.initializeCheck(CheckRequest(CheckKey("SEO-42"), tenantId, "https://page2.url"))

        check1.setResult(CheckResult(ResultStatus.PASSED))
        check2.setResult(CheckResult(ResultStatus.PASSED))
        check3.setResult(CheckResult(ResultStatus.FAILED))
        check4.setResult(CheckResult(ResultStatus.FAILED))

        repository.updateCheck(check1)
        repository.updateCheck(check2)
        repository.updateCheck(check3)
        repository.updateCheck(check4)

        assertThat(repository.getLatestChecksCountByStatus(tenantId, ResultStatus.PASSED)).isEqualTo(2)
        assertThat(repository.getLatestChecksCountByStatus(tenantId, ResultStatus.FAILED)).isEqualTo(2)
    }

    @Test
    fun getChecksByPage() {
        val tenantId = UUID.randomUUID()
        val check1 = repository.initializeCheck(CheckRequest(CheckKey("ABC-123"), tenantId, "https://page1.url"))
        val check2 = repository.initializeCheck(CheckRequest(CheckKey("ABC-123"), tenantId, "https://page2.url"))
        val check3 = repository.initializeCheck(CheckRequest(CheckKey("SEO-42"), tenantId, "https://page1.url"))
        val check4 = repository.initializeCheck(CheckRequest(CheckKey("SEO-42"), tenantId, "https://page2.url"))
        val check5 = repository.initializeCheck(CheckRequest(CheckKey("SEO-42"), tenantId, "https://page1.url"))
        val check6 = repository.initializeCheck(CheckRequest(CheckKey("SEO-42"), tenantId, "https://page2.url"))

        check1.setResult(CheckResult(ResultStatus.PASSED))
        check2.setResult(CheckResult(ResultStatus.PASSED))
        check3.setResult(CheckResult(ResultStatus.FAILED))
        check4.setResult(CheckResult(ResultStatus.FAILED))
        check5.setExecutionError()
        check6.setExecutionError()

        repository.updateCheck(check1)
        repository.updateCheck(check2)
        repository.updateCheck(check3)
        repository.updateCheck(check4)
        repository.updateCheck(check5)
        repository.updateCheck(check6)

        val pages = repository.getLatestChecksByPage(tenantId, "https://page1.url")
        assertThat(pages).hasSize(2)

        val pages2 = repository.getLatestChecksByPage(tenantId, "https://page2.url")
        assertThat(pages2).hasSize(2)
    }

    @Test
    fun getChecksSummaries() {
        val tenantId = UUID.randomUUID()
        val check1 = repository.initializeCheck(CheckRequest(CheckKey("ABC-123"), tenantId, "https://page1.url"))
        val check2 = repository.initializeCheck(CheckRequest(CheckKey("ABC-123"), tenantId, "https://page2.url"))
        val check3 = repository.initializeCheck(CheckRequest(CheckKey("SEO-42"), tenantId, "https://page1.url"))
        val check4 = repository.initializeCheck(CheckRequest(CheckKey("SEO-42"), tenantId, "https://page2.url"))
        val check5 = repository.initializeCheck(CheckRequest(CheckKey("SEO-42"), tenantId, "https://page1.url"))
        val check6 = repository.initializeCheck(CheckRequest(CheckKey("SEO-42"), tenantId, "https://page2.url"))

        check1.setResult(CheckResult(ResultStatus.PASSED))
        check2.setResult(CheckResult(ResultStatus.PASSED))
        check3.setResult(CheckResult(ResultStatus.FAILED))
        check4.setResult(CheckResult(ResultStatus.FAILED))
        check5.setExecutionError()
        check6.setExecutionError()

        repository.updateCheck(check1)
        repository.updateCheck(check2)
        repository.updateCheck(check3)
        repository.updateCheck(check4)
        repository.updateCheck(check5)
        repository.updateCheck(check6)

        val summaries = repository.getLatestChecksSummaries(tenantId)
        assertThat(summaries).hasSize(2)
        assertThat(summaries).filteredOn("pageUrl", "https://page1.url").hasSize(1)
        assertThat(summaries).filteredOn("pageUrl", "https://page2.url").hasSize(1)
        assertThat(summaries).filteredOn("passedChecks", 1).hasSize(2)
        assertThat(summaries).filteredOn("failedChecks", 1).hasSize(2)
    }
}
