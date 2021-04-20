package com.oxyggen.c4k.store

import com.oxyggen.c4k.persistency.Target
import kotlinx.coroutines.Deferred

open class TargetCoroutineJob(target: Target, val deferred: Deferred<Set<Target>>) :
    TargetJob(target) {
    override fun isCompleted(): Boolean = deferred.isCompleted
    override fun isCancelled(): Boolean = deferred.isCancelled
}