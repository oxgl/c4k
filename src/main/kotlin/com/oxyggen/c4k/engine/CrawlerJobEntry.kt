package com.oxyggen.c4k.engine

import com.oxyggen.c4k.target.CrawlTarget
import kotlinx.coroutines.Deferred

class CrawlerJobEntry(
    val target: CrawlTarget,
    val job: Deferred<Set<CrawlTarget>>
)