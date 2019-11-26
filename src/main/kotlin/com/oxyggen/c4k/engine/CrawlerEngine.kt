package com.oxyggen.c4k.engine

import com.oxyggen.c4k.analyzer.CrawlTargetAnalyzer
import com.oxyggen.c4k.analyzer.HttpTargetAnalyzer
import com.oxyggen.c4k.target.CrawlTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import org.apache.logging.log4j.kotlin.Logging
import kotlin.math.max
import kotlin.reflect.KClass

class CrawlerEngine(val config: CrawlerEngine.Config = CrawlerEngine.Config()) : Logging {

    data class Config(
        val politenessDelay: Int = 200,
        val maxDepth: Int = -1
    )

    private var startTimeMillis: Long = 0L

    private val jobHandler: CrawlerJobHandler by lazy {
        CrawlerJobHandler(this)
    }

    private fun startTimer() {
        startTimeMillis = System.currentTimeMillis()
    }

    private fun getElapsedTimeMillis() = System.currentTimeMillis() - startTimeMillis
    private fun getEarliestStartTimeMillis() = (jobHandler.getExecutedJobCount() + 1L) * config.politenessDelay

    fun registerTargetAnalyzer(analyzer: CrawlTargetAnalyzer) =
        jobHandler.registerTargetAnalyzer(analyzer)

    fun addTarget(target: CrawlTarget) = jobHandler.pushTargets(setOf(target))

    fun addTargets(targets: Set<CrawlTarget>) = jobHandler.pushTargets(targets)

    suspend fun execute(scope: CoroutineScope) {
        // Register at least analyzer for HttpTarget
        jobHandler.registerTargetAnalyzer(HttpTargetAnalyzer(), replace = false)

        // Remember start time
        startTimer()

        // Write info
        logger.info { "Crawler started in coroutine context $scope..." }

        // While job is active or targets are waiting
        while (jobHandler.isJobActive() || jobHandler.isTargetWaiting()) {

            val waitTimeMillis = getEarliestStartTimeMillis() - getElapsedTimeMillis()

            //logger.info { "EPST: ${getEarliestStartTimeMillis()}, ET: ${getElapsedTimeMillis()}, TargetWaiting: ${jobHandler.isTargetWaiting()}, JobActive ${jobHandler.isJobActive()}" }

            // Start new jobs if possible
            if (jobHandler.isTargetWaiting()) {
                if (waitTimeMillis <= 0) {
                    // Earliest possible start time reached, so start next job
                    jobHandler.startJobForNextWaitingTarget(scope)
                    // Write log
                    logger.debug { ">> New job scheduled. Total executed jobs: ${jobHandler.getExecutedJobCount()}, active jobs: ${jobHandler.getActiveJobCount()}" }
                }
            }

            // Get result from jobs
            if (jobHandler.isJobActive()) {
                val newTargets = jobHandler.receiveNewTargets(maxDepth = config.maxDepth)
                if (newTargets != null) {
                    logger.debug { "<< Job finished. ${newTargets.size} new targets received, active jobs: ${jobHandler.getActiveJobCount()}" }
                    addTargets(newTargets)
                } else if (waitTimeMillis > 0) {
                    logger.debug { "Job was not finished, waiting $waitTimeMillis milliseconds" }
                    delay(waitTimeMillis)
                } else if (!jobHandler.isTargetWaiting()) {
                    delay(max(20, waitTimeMillis / 2))
                }
            }
        }

        val elapsedTimeMillis = getElapsedTimeMillis()
        val jobTimeMillis =
            if (jobHandler.getExecutedJobCount() > 0) elapsedTimeMillis / jobHandler.getExecutedJobCount() else 0

        logger.info { "Crawler finished. ${jobHandler.getExecutedJobCount()} finished jobs, elapsed time $elapsedTimeMillis ms, $jobTimeMillis ms / job" }
    }

}