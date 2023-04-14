package io.reflectoring.components.checkengine.internal.database.internal

import io.reflectoring.components.checkengine.api.CheckId
import io.reflectoring.components.checkengine.internal.database.api.CheckMutations
import io.reflectoring.components.common.database.Tables.CE_CHECK
import io.reflectoring.components.common.database.Tables.CE_CHECK_FIX
import io.reflectoring.components.common.database.tables.records.CeCheckRecord
import org.jooq.DSLContext
import org.jooq.Record8
import org.jooq.Select
import org.jooq.impl.DSL.case_
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.max
import org.jooq.impl.DSL.sum
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class JooqCheckRepository(
    private val jooq: DSLContext
) : CheckMutations, io.reflectoring.components.checkengine.api.CheckQueries {

    override fun initializeCheck(checkRequest: io.reflectoring.components.checkengine.api.CheckRequest): io.reflectoring.components.checkengine.api.Check {
        val checkRecord = jooq.insertInto(CE_CHECK)
            .columns(
                CE_CHECK.KEY,
                CE_CHECK.SITE_ID,
                CE_CHECK.PAGE_URL,
                CE_CHECK.START_DATE,
                CE_CHECK.EXECUTION_STATUS
            )
            .values(
                checkRequest.checkKey.key,
                checkRequest.siteId.toString(),
                checkRequest.pageUrl,
                LocalDateTime.now(),
                io.reflectoring.components.checkengine.api.ExecutionStatus.IN_PROGRESS.name
            )
            .returning()
            .fetchOne()
            ?: throw java.lang.IllegalStateException("could not return row after insert!")

        return toDomainObject(checkRecord)
    }

    override fun updateCheck(check: io.reflectoring.components.checkengine.api.Check) {
        jooq.update(CE_CHECK)
            .set(CE_CHECK.END_DATE, LocalDateTime.now())
            .set(CE_CHECK.EXECUTION_STATUS, check.executionStatus.name)
            .set(CE_CHECK.RESULT_STATUS, check.checkResult?.status?.name)
            .where(CE_CHECK.ID.eq(check.id.id.toInt()))
            .execute()

        check.checkResult?.fixes?.forEach { fix ->
            jooq.insertInto(CE_CHECK_FIX, CE_CHECK_FIX.CHECK_ID, CE_CHECK_FIX.FIX, CE_CHECK_FIX.CONDITION_KEY)
                .values(check.id.id, fix.message, fix.failedConditionKey.toString())
                .execute()
        }
    }

    private fun toDomainObject(record: CeCheckRecord): io.reflectoring.components.checkengine.api.Check {

        val fixes = jooq.selectFrom(CE_CHECK_FIX)
            .where(CE_CHECK_FIX.CHECK_ID.eq(record.id.toLong()))
            .fetchStream()
            .map {
                io.reflectoring.components.checkengine.api.Fix(
                    io.reflectoring.components.checkengine.api.ConditionKey(it.conditionKey),
                    it.fix
                )
            }
            .toList()

        val result = record.resultStatus?.let {
            io.reflectoring.components.checkengine.api.CheckResult(
                io.reflectoring.components.checkengine.api.ResultStatus.valueOf(record.resultStatus),
                fixes
            )
        }

        return io.reflectoring.components.checkengine.api.Check(
            CheckId(record.id.toLong()),
            io.reflectoring.components.checkengine.api.CheckKey(record.key),
            UUID.fromString(record.siteId),
            record.pageUrl,
            record.startDate,
            record.endDate,
            io.reflectoring.components.checkengine.api.ExecutionStatus.valueOf(record.executionStatus),
            result
        )
    }

    private fun toDomainObject(record: Record8<Int, String, String, String, LocalDateTime, LocalDateTime, String, String>): io.reflectoring.components.checkengine.api.Check {
        val typedRecord = CeCheckRecord(
            record.value1(),
            record.value2(),
            record.value3(),
            record.value4(),
            record.value5(),
            record.value6(),
            record.value7(),
            record.value8()
        )
        return toDomainObject(typedRecord)
    }

    override fun getCheck(checkId: CheckId): io.reflectoring.components.checkengine.api.Check? {
        return jooq.selectFrom(CE_CHECK)
            .where(CE_CHECK.ID.eq(checkId.id.toInt()))
            .fetchOne()
            ?.let { toDomainObject(it) }
    }

    override fun getLatestChecksCountByStatus(siteId: UUID, status: io.reflectoring.components.checkengine.api.ResultStatus): Int {
        val latestChecks = latestSuccessfulChecks(siteId).asTable("latestChecks")

        return jooq.fetchCount(
            jooq.selectFrom(latestChecks)
                .where(field("result_status").eq(status.name))
        )
    }

    /**
     * Subquery that returns the most current check of each type for a given site.
     */
    private fun latestSuccessfulChecks(siteId: UUID): Select<Record8<Int, String, String, String, LocalDateTime, LocalDateTime, String, String>> {
        return jooq.select(
            CE_CHECK.ID,
            CE_CHECK.KEY,
            CE_CHECK.SITE_ID,
            CE_CHECK.PAGE_URL,
            CE_CHECK.START_DATE,
            CE_CHECK.END_DATE,
            CE_CHECK.EXECUTION_STATUS,
            CE_CHECK.RESULT_STATUS
        )
            .distinctOn(CE_CHECK.SITE_ID, CE_CHECK.PAGE_URL, CE_CHECK.KEY)
            .from(CE_CHECK)
            .where(CE_CHECK.SITE_ID.eq(siteId.toString()))
            .and(CE_CHECK.EXECUTION_STATUS.eq(io.reflectoring.components.checkengine.api.ExecutionStatus.SUCCESS.name))
            .orderBy(CE_CHECK.SITE_ID, CE_CHECK.PAGE_URL, CE_CHECK.KEY, CE_CHECK.END_DATE.desc())
    }

    override fun getLatestChecksSummaries(siteId: UUID): List<io.reflectoring.components.checkengine.api.ChecksSummary> {
        val latestChecks = latestSuccessfulChecks(siteId).asTable("latestChecks")

        return jooq.select(
            field("page_url", String::class.java),
            max(field("end_date", LocalDateTime::class.java)),
            sum(case_().`when`(field("result_status", String::class.java).eq(io.reflectoring.components.checkengine.api.ResultStatus.PASSED.name), 1).else_(0)).`as`("passedCount"),
            sum(case_().`when`(field("result_status", String::class.java).eq(io.reflectoring.components.checkengine.api.ResultStatus.FAILED.name), 1).else_(0)).`as`("failedCount")
        )
            .from(latestChecks)
            .groupBy(field("page_url", String::class.java))
            .fetch()
            .stream()
            .map {
                io.reflectoring.components.checkengine.api.ChecksSummary(
                    it.value1(),
                    it.value2(),
                    it.value3().toInt(),
                    it.value4().toInt()
                )
            }
            .toList()
    }

    override fun getLatestChecksByPage(siteId: UUID, pageUrl: String): List<io.reflectoring.components.checkengine.api.Check> {

        val latestChecks = latestSuccessfulChecks(siteId).asTable("latestChecks")

        return jooq.select(
            field("id", Int::class.java),
            field("key", String::class.java),
            field("site_id", String::class.java),
            field("page_url", String::class.java),
            field("start_date", LocalDateTime::class.java),
            field("end_date", LocalDateTime::class.java),
            field("execution_status", String::class.java),
            field("result_status", String::class.java)
        ).from(latestChecks)
            .where(field("page_url").eq(pageUrl))
            .fetch()
            .stream()
            .map { toDomainObject(it) }
            .toList()
    }
}
