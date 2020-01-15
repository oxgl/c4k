package com.oxyggen.c4k.store

import com.oxyggen.c4k.target.CrawlTarget
import org.apache.logging.log4j.kotlin.Logging

abstract class CrawlTargetStore() : Logging {

    abstract fun getWaitingTargetCount(): Int

    /**
     * Returns whether target is already scheduled (and not active or finished)
     * @param target the target
     * @return is scheduled?
     */
    abstract fun isTargetWaiting(target: CrawlTarget? = null): Boolean

    /**
     * Adds a target into scheduled target queue
     * @param target the target
     * @return true if target added to queue
     */
    abstract fun enqueueWaitingTarget(target: CrawlTarget): Boolean

    /**
     * Get and remove next target from queue
     * @return the target (or null if queue is empty)
     */
    abstract fun dequeueWaitingTarget(): CrawlTarget?

    /**
     * Returns the count of active targets
     * @return count of targets
     */
    abstract fun getActiveTargetCount(): Int

    /**
     * Returns whether target is already active (but not finished)
     * @param target the target
     * @return is active?
     */
    abstract fun isTargetActive(target: CrawlTarget? = null): Boolean

    /**
     * Add (active) job to store
     * @param job the job
     * @return true if it was added
     */
    abstract fun addJob(job: CrawlTargetJob): Boolean

    /**
     * Remove job from store
     * @param job the job
     * @return true if it was removed
     */
    abstract fun removeJob(job: CrawlTargetJob): Boolean

    /**
     * Remove and return the set of all completed jobs
     * @return set of jobs
     */
    abstract fun pullCompletedJobs(): Set<CrawlTargetJob>

    /**
     * Remove and return the set of all cancelled jobs
     * @return set of jobs
     */
    abstract fun pullCancelledJobs(): Set<CrawlTargetJob>

    /**
     * Get count of already finished targets
     * @return count of targets
     */
    abstract fun getFinishedTargetCount(): Int

    /**
     * Check whether target (or at least one target) is finished
     * @param target the target or null if at least one target was finished
     * @return true if it was finished
     */
    abstract fun isTargetFinished(target: CrawlTarget? = null): Boolean

    abstract fun addFinishedTarget(target: CrawlTarget)

    open fun isTargetStored(target: CrawlTarget): Boolean =
        isTargetWaiting(target) || isTargetActive(target) || isTargetFinished(target)
}