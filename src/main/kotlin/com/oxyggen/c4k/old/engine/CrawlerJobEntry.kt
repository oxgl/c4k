package com.oxyggen.c4k.old.engine

import com.oxyggen.c4k.target.Target
import kotlinx.coroutines.Deferred

class CrawlerJobEntry(
    val target: Target,
    val job: Deferred<Set<Target>>
)