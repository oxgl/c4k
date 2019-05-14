package com.oxyggen.c4k.engine

import com.oxyggen.c4k.analyzer.HttpTargetAnalyzer
import com.oxyggen.c4k.target.CrawlTarget
import com.oxyggen.c4k.target.HttpTarget
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class CrawlerJobQueue(private val coroutineScope: CoroutineScope) {

    private val mutex = Mutex()
    private var targets: MutableSet<CrawlTarget> = mutableSetOf();
    private var activeJobs: MutableSet<CrawlerJobEntry> = mutableSetOf();

    suspend fun addTarets(newTargets: Set<CrawlTarget>) {
        mutex.withLock {
            newTargets.forEach {
                if (targets.contains(it)) {
                    logger.info { "Target already in jobs, skipping $it..." }
                } else {
                    targets.add(it)
                    val job = coroutineScope.async(Dispatchers.Default) {
                        if (it is HttpTarget) {
                            HttpTargetAnalyzer().analyze(it)
                        } else {
                            setOf()
                        }
                    }
                    activeJobs.add(CrawlerJobEntry(it, job));
                }
            }
        }
    }

    fun isJobActive() = activeJobs.isNotEmpty()

    fun getActiveJobCount() = activeJobs.size

    fun getTargets() = targets

    fun getTargetCount() = targets.size


    suspend fun receiveNewTargets(): Set<CrawlTarget> {
        val finishedJob = select<CrawlerJobEntry> {
            activeJobs.forEach {
                it.job.onAwait { _ ->
                    it
                }
            }
        }

        mutex.withLock {
            activeJobs.remove(finishedJob)
        }

        return finishedJob.job.await()
    }

}