package com.oxyggen.c4k.store

import com.oxyggen.c4k.target.CrawlTarget
import org.apache.logging.log4j.kotlin.Logging
import java.util.*

open class CrawlTargetMemoryStore() : CrawlTargetStore(), Logging {

    protected open val waitingTargets: Queue<CrawlTarget> = ArrayDeque<CrawlTarget>()

    override fun getWaitingTargetCount(): Int = waitingTargets.size

    override fun isTargetWaiting(target: CrawlTarget?): Boolean =
        if (target == null) waitingTargets.isNotEmpty() else waitingTargets.contains(target)

    override fun enqueueWaitingTarget(target: CrawlTarget): Boolean = waitingTargets.add(target)
    override fun dequeueWaitingTarget(): CrawlTarget? = waitingTargets.poll()


    protected open val activeJobs: MutableList<CrawlTargetJob> = mutableListOf()

    override fun getActiveTargetCount(): Int = activeJobs.size

    override fun isTargetActive(target: CrawlTarget?): Boolean =
        if (target == null) activeJobs.isNotEmpty() else activeJobs.find { it.target == target } != null

    override fun addJob(job: CrawlTargetJob): Boolean = activeJobs.add(job)

    override fun removeJob(job: CrawlTargetJob): Boolean = activeJobs.remove(job)

    protected fun pullWithFilter(predicate: (CrawlTargetJob) -> Boolean): Set<CrawlTargetJob> {
        val (found, rest) = activeJobs.partition(predicate)
        with(activeJobs)
        {
            clear()
            addAll(rest)
        }
        return found.toSet()
    }

    override fun pullCompletedJobs(): Set<CrawlTargetJob> = pullWithFilter { it.isCompleted() }

    override fun pullCancelledJobs(): Set<CrawlTargetJob> = pullWithFilter { it.isCancelled() }


    protected open val finishedTargets: MutableSet<CrawlTarget> = mutableSetOf()

    override fun getFinishedTargetCount(): Int = finishedTargets.size

    override fun isTargetFinished(target: CrawlTarget?): Boolean =
        if (target == null) finishedTargets.isNotEmpty() else finishedTargets.contains(target)

    override fun addFinishedTarget(target: CrawlTarget) {
        finishedTargets.add(target)
    }
}