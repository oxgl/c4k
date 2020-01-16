package com.oxyggen.c4k.store

import com.oxyggen.c4k.target.Target
import org.apache.logging.log4j.kotlin.Logging
import java.util.*

open class TargetMemoryStore() : TargetStore(), Logging {

    protected open val waitingTargets: Queue<Target> = ArrayDeque<Target>()

    override fun getWaitingTargetCount(): Int = waitingTargets.size

    override fun isTargetWaiting(target: Target?): Boolean =
        if (target == null) waitingTargets.isNotEmpty() else waitingTargets.contains(target)

    override fun enqueueWaitingTarget(target: Target): Boolean = waitingTargets.add(target)
    override fun dequeueWaitingTarget(): Target? = waitingTargets.poll()


    protected open val activeJobs: MutableList<TargetJob> = mutableListOf()

    override fun getActiveTargetCount(): Int = activeJobs.size

    override fun isTargetActive(target: Target?): Boolean =
        if (target == null) activeJobs.isNotEmpty() else activeJobs.find { it.target == target } != null

    override fun addJob(job: TargetJob): Boolean = activeJobs.add(job)

    override fun removeJob(job: TargetJob): Boolean = activeJobs.remove(job)

    protected fun pullWithFilter(predicate: (TargetJob) -> Boolean): Set<TargetJob> {
        val (found, rest) = activeJobs.partition(predicate)
        with(activeJobs)
        {
            clear()
            addAll(rest)
        }
        return found.toSet()
    }

    override fun pullCompletedJobs(): Set<TargetJob> = pullWithFilter { it.isCompleted() }

    override fun pullCancelledJobs(): Set<TargetJob> = pullWithFilter { it.isCancelled() }


    protected open val finishedTargets: MutableSet<Target> = mutableSetOf()

    override fun getFinishedTargetCount(): Int = finishedTargets.size

    override fun isTargetFinished(target: Target?): Boolean =
        if (target == null) finishedTargets.isNotEmpty() else finishedTargets.contains(target)

    override fun addFinishedTarget(target: Target) {
        finishedTargets.add(target)
    }
}