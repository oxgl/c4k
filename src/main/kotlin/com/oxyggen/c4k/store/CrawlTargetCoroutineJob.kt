package com.oxyggen.c4k.store

import com.oxyggen.c4k.target.CrawlTarget
import kotlinx.coroutines.Deferred

open class CrawlTargetCoroutineJob(target: CrawlTarget, val deferred: Deferred<Set<CrawlTarget>>) :
    CrawlTargetJob(target) {
    override fun isCompleted(): Boolean = deferred.isCompleted
    override fun isCancelled(): Boolean = deferred.isCancelled
}