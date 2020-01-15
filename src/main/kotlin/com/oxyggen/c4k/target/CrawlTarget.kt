package com.oxyggen.c4k.target

import com.oxyggen.c4k.old.group.CrawlGroup

abstract class CrawlTarget(val parent: CrawlTarget? = null) {

    val depth: Int

    init {
        if (parent != null) {
            depth = parent.depth + 1
        } else {
            depth = 0
        }
    }

    abstract fun getQueueId(): String

}
