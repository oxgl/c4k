package com.oxyggen.c4k.old.engine

import com.oxyggen.c4k.old.analyzer.CrawlTargetAnalyzer
import com.oxyggen.c4k.target.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.apache.logging.log4j.kotlin.Logging
import java.util.*
import kotlin.reflect.KClass


class CrawlerJobQueue(val engine: CrawlerEngine) : Logging {

    var executedJobs = 0

    private val mutex = Mutex()
    private var targetsWaiting: Queue<Target> = ArrayDeque<Target>()
    private var targets: MutableSet<Target> = mutableSetOf()
    private var targetAnalyzers: MutableMap<KClass<out Target>, CrawlTargetAnalyzer> = mutableMapOf()
    private var activeJobs: MutableSet<CrawlerJobEntry> = mutableSetOf()

    private fun getTargetAnalyzer(target: Target): CrawlTargetAnalyzer? = targetAnalyzers[target::class]

    fun registerTargetAnalyzer(analyzer: CrawlTargetAnalyzer, replace: Boolean = true) {

        val target = analyzer.getHandledTargets()
        target.forEach {
            if (replace)
                targetAnalyzers.replace(it, analyzer)
            else if (!targetAnalyzers.containsKey(it))
                targetAnalyzers.put(it, analyzer)
        }
    }

    fun pushTargets(newTarget: Set<Target>) {
        newTarget.forEach {
            if (targets.contains(it)) {
                logger.debug { "Target already in jobs, skipping $it..." }
            } else {
                targets.add(it)
                targetsWaiting.add(it)
            }
        }
    }

    suspend fun startJobForNextWaitingTarget(coroutineScope: CoroutineScope) {
        val target = targetsWaiting.poll()

        if (target != null) {
            val analyzer = getTargetAnalyzer(target)
            val job = coroutineScope.async(Dispatchers.Default) {
                analyzer!!.analyze(target)
            }
            mutex.withLock {
                executedJobs++
                activeJobs.add(CrawlerJobEntry(target, job))
            }
        }
    }

    fun isTargetWaiting() = targetsWaiting.isNotEmpty()

    fun getWaitingTargetCount() = targetsWaiting.size

    fun isJobActive() = activeJobs.isNotEmpty()

    fun getActiveJobCount() = activeJobs.size

    fun getExecutedJobCount() = executedJobs

    fun getTargets() = targets

    fun getTargetCount() = targets.size

    suspend fun receiveNewTargets(maxDepth: Int = -1): Set<Target>? {

        var result: MutableSet<Target>? = null

        val finishedJobs = mutableSetOf<CrawlerJobEntry>()

        activeJobs.forEach {
            if (!it.job.isActive) {
                finishedJobs.add(it)
                if (it.job.isCompleted) {
                    if (result == null) result = mutableSetOf<Target>()
                    val newTargets = it.job.await()
                    newTargets.forEach {
                        if (maxDepth <= 0 || it.depth <= maxDepth) {
                            val analyzer = getTargetAnalyzer(it)
                            if (analyzer != null && analyzer.shouldVisit(it))
                                result!!.add(it)
                        }
                    }
                }
            }
        }

        mutex.withLock {
            activeJobs.removeAll(finishedJobs)
        }

        return result
    }

}