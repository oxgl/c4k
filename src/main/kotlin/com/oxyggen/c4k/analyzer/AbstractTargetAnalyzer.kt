package com.oxyggen.c4k.analyzer

import com.oxyggen.c4k.target.CrawlTarget

abstract class AbstractTargetAnalyzer<T> where T : CrawlTarget {
    abstract suspend fun analyze(target: T): Set<CrawlTarget>
}