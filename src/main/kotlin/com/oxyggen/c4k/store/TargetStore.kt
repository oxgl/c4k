package com.oxyggen.c4k.store

import com.oxyggen.c4k.target.Target
import org.apache.logging.log4j.kotlin.Logging

abstract class TargetStore() : Logging {

    abstract fun getWaitingTargetCount(): Int

    /**
     * Returns whether target is already scheduled (and not active or finished)
     * @param target the target
     * @return is scheduled?
     */
    abstract fun isTargetWaiting(target: Target? = null): Boolean

    /**
     * Adds a target into scheduled target queue
     * @param target the target
     * @return true if target added to queue
     */
    abstract fun enqueueWaitingTarget(target: Target): Boolean

    /**
     * Get and remove next target from queue
     * @return the target (or null if queue is empty)
     */
    abstract fun dequeueWaitingTarget(): Target?

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
    abstract fun isTargetActive(target: Target? = null): Boolean

    /**
     * Add (active) job to store
     * @param job the job
     * @return true if it was added
     */
    abstract fun addJob(job: TargetJob): Boolean

    /**
     * Remove job from store
     * @param job the job
     * @return true if it was removed
     */
    abstract fun removeJob(job: TargetJob): Boolean

    /**
     * Remove and return the set of all completed jobs
     * @return set of jobs
     */
    abstract fun pullCompletedJobs(): Set<TargetJob>

    /**
     * Remove and return the set of all cancelled jobs
     * @return set of jobs
     */
    abstract fun pullCancelledJobs(): Set<TargetJob>

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
    abstract fun isTargetFinished(target: Target? = null): Boolean

    abstract fun addFinishedTarget(target: Target)

    open fun isTargetStored(target: Target): Boolean =
        isTargetWaiting(target) || isTargetActive(target) || isTargetFinished(target)
}