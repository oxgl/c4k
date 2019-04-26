package com.oxyggen.c4k.engine

import com.oxyggen.c4k.target.CrawlTarget
import kotlinx.coroutines.Deferred

class CrawlerJob(
    val job: Deferred<Set<out CrawlTarget>>
)