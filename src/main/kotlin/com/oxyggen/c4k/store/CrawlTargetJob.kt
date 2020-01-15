package com.oxyggen.c4k.store

import com.oxyggen.c4k.target.CrawlTarget

abstract class CrawlTargetJob(val target: CrawlTarget) {
    abstract fun isCompleted(): Boolean
    abstract fun isCancelled(): Boolean
}