package com.oxyggen.c4k.engine

import com.oxyggen.c4k.analyzer.HttpTargetAnalyzer
import com.oxyggen.c4k.target.CrawlTarget
import com.oxyggen.c4k.target.HttpTarget
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select

class CrawlerJobQueue(val coroutineScope: CoroutineScope) {

    private var targets: MutableSet<CrawlTarget> = mutableSetOf();
    private var activeJobs: MutableSet<CrawlerJobEntry> = mutableSetOf();

    suspend fun addTarets(newTargets: Set<CrawlTarget>) {
        synchronized(targets) {
            newTargets.forEach {
                if (targets.contains(it)) {
                    println("Target already in jobs, skipping $it...")
                }
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

    fun isJobActive() = activeJobs.isNotEmpty()

    fun getActiveJobCount() = activeJobs.size

    fun getTargets() = targets

    fun getTargetCount() = targets.size


    suspend fun receiveNewTargets(): Set<out CrawlTarget> {
        val finishedJob = select<CrawlerJobEntry> {
            activeJobs.forEach {
                it.job.onAwait { _ ->
                    it
                }
            }
        }

        synchronized(targets) {
            activeJobs.remove(finishedJob)
        }

        return finishedJob.job.getCompleted()
    }

}