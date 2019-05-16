package com.oxyggen.c4k.engine

import com.oxyggen.c4k.analyzer.CrawlTargetAnalyzer
import com.oxyggen.c4k.analyzer.HttpTargetAnalyzer
import com.oxyggen.c4k.target.CrawlTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import org.apache.logging.log4j.kotlin.Logging
import kotlin.math.max

class CrawlerEngine(val config: CrawlerConfig = CrawlerConfig()) : Logging {

    private var startTimeMillis: Long = 0L

    private val jobHandler: CrawlerJobHandler by lazy {
        CrawlerJobHandler()
    }

    private fun getElapsedTimeMillis() = System.currentTimeMillis() - startTimeMillis
    private fun getEarliestStartTimeMillis() = (jobHandler.getExecutedJobCount() + 1L) * config.politenessDelay

    fun registerTargetAnalyzer(analyzer: CrawlTargetAnalyzer) = jobHandler.registerTargetAnalyzer(HttpTargetAnalyzer())

    fun addTarget(target: CrawlTarget) = jobHandler.pushTargets(setOf(target))

    fun addTargets(targets: Set<CrawlTarget>) = jobHandler.pushTargets(targets)


    suspend fun execute(scope: CoroutineScope) {
        // Register at least analyzer for HttpTarget
        jobHandler.registerTargetAnalyzer(HttpTargetAnalyzer(), replace = false)

        // Remember start time
        startTimeMillis = System.currentTimeMillis()

        // Write info
        logger.info { "Crawler started in coroutine context ${scope}..." }

        // While job is active or targets are waiting
        while (jobHandler.isJobActive() || jobHandler.isTargetWaiting()) {

            val waitTimeMillis = getEarliestStartTimeMillis() - getElapsedTimeMillis()

            logger.info { "EPST: ${getEarliestStartTimeMillis()}, ET: ${getElapsedTimeMillis()}, TargetWaiting: ${jobHandler.isTargetWaiting()}, JobActive ${jobHandler.isJobActive()}" }

            // Start new jobs if possible
            if (jobHandler.isTargetWaiting()) {
                if (waitTimeMillis <= 0) {
                    // Earliest possible start time reached, so start next job
                    jobHandler.startJobForNextWaitingTarget(scope)
                    // Write log
                    logger.info { "New job scheduled. Total executed jobs: ${jobHandler.getExecutedJobCount()}" }
                }
            }

            // Get result from jobs
            if (jobHandler.isJobActive()) {
                val newTargets = jobHandler.receiveNewTargets()
                if (newTargets != null) {
                    logger.info { "Job finished. ${newTargets.size} new targets received." }
                    addTargets(newTargets)
                } else if (waitTimeMillis > 0) {
                    logger.info { "Job was not finished, waiting ${waitTimeMillis} milliseconds" }
                    delay(waitTimeMillis)
                } else if (!jobHandler.isTargetWaiting()) {
                    delay(max(20, waitTimeMillis / 2))
                }
            }

        }
        logger.info { "Crawler finished." }
    }

}