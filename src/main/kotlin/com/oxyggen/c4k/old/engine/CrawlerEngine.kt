package com.oxyggen.c4k.old.engine

import com.oxyggen.c4k.old.analyzer.CrawlTargetAnalyzer
import com.oxyggen.c4k.old.analyzer.HttpTargetAnalyzer
import com.oxyggen.c4k.target.CrawlTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import org.apache.logging.log4j.kotlin.Logging
import kotlin.math.max

class CrawlerEngine(val config: Config = Config()) : Logging {

    data class Config(
        val politenessDelay: Int = 200,
        val maxDepth: Int = -1
    )

    private var startTimeMillis: Long = 0L

    private val jobQueue: CrawlerJobQueue by lazy {
        CrawlerJobQueue(this)
    }

    private fun startTimer() {
        startTimeMillis = System.currentTimeMillis()
    }

    private fun getElapsedTimeMillis() = System.currentTimeMillis() - startTimeMillis
    private fun getEarliestStartTimeMillis() = (jobQueue.getExecutedJobCount() + 1L) * config.politenessDelay

    fun registerTargetAnalyzer(analyzer: CrawlTargetAnalyzer) =
        jobQueue.registerTargetAnalyzer(analyzer)

    fun addTarget(target: CrawlTarget) = jobQueue.pushTargets(setOf(target))

    fun addTargets(targets: Set<CrawlTarget>) = jobQueue.pushTargets(targets)

    suspend fun execute(scope: CoroutineScope) {
        // Register at least analyzer for HttpTarget
        jobQueue.registerTargetAnalyzer(HttpTargetAnalyzer(), replace = false)

        // Remember start time
        startTimer()

        // Write info
        logger.info { "Crawler started in coroutine context $scope..." }

        // While job is active or targets are waiting
        while (jobQueue.isJobActive() || jobQueue.isTargetWaiting()) {

            val waitTimeMillis = getEarliestStartTimeMillis() - getElapsedTimeMillis()

            //logger.info { "EPST: ${getEarliestStartTimeMillis()}, ET: ${getElapsedTimeMillis()}, TargetWaiting: ${jobHandler.isTargetWaiting()}, JobActive ${jobHandler.isJobActive()}" }

            // Start new jobs if possible
            if (jobQueue.isTargetWaiting()) {
                if (waitTimeMillis <= 0) {
                    // Earliest possible start time reached, so start next job
                    jobQueue.startJobForNextWaitingTarget(scope)
                    // Write log
                    logger.debug { ">> New job scheduled. Total executed jobs: ${jobQueue.getExecutedJobCount()}, active jobs: ${jobQueue.getActiveJobCount()}" }
                }
            }

            // Get result from jobs
            if (jobQueue.isJobActive()) {
                val newTargets = jobQueue.receiveNewTargets(maxDepth = config.maxDepth)
                if (newTargets != null) {
                    logger.debug { "<< Job finished. ${newTargets.size} new targets received, active jobs: ${jobQueue.getActiveJobCount()}" }
                    addTargets(newTargets)
                } else if (waitTimeMillis > 0) {
                    logger.debug { "Job was not finished, waiting $waitTimeMillis milliseconds" }
                    delay(waitTimeMillis)
                } else if (!jobQueue.isTargetWaiting()) {
                    delay(max(20, waitTimeMillis / 2))
                }
            }
        }

        val elapsedTimeMillis = getElapsedTimeMillis()
        val jobTimeMillis =
            if (jobQueue.getExecutedJobCount() > 0) elapsedTimeMillis / jobQueue.getExecutedJobCount() else 0

        logger.info { "Crawler finished. ${jobQueue.getExecutedJobCount()} finished jobs, elapsed time $elapsedTimeMillis ms, $jobTimeMillis ms / job" }
    }

}