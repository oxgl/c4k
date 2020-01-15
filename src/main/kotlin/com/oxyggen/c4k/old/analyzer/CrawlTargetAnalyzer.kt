package com.oxyggen.c4k.old.analyzer

import com.oxyggen.c4k.target.CrawlTarget
import kotlin.reflect.KClass

abstract class CrawlTargetAnalyzer {

    /**
     * Should return the set of target classes handled by current target analyzer
     */
    abstract fun getHandledTargets(): Set<KClass<out CrawlTarget>>

    /**
     * Analyze given [target] and get the new targets
     */
    abstract suspend fun analyze(target: CrawlTarget): Set<CrawlTarget>

    /*
    * Return whether we should visit (and analyze) given [target]
     */
    open fun shouldVisit(target: CrawlTarget) = true

}