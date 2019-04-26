package com.oxyggen.c4k.analyzer

import com.oxyggen.c4k.target.CrawlTarget
import kotlin.reflect.KClass

abstract class CrawlTargetAnalyzer<T> where T : CrawlTarget {

    /**
     * Should return the set of target classes handled by current target analyzer
     */
    abstract fun getHandledTargets(): Set<KClass<out CrawlTarget>>

    /**
     * Analyze given [target]
     */
    abstract suspend fun analyze(target: T): Set<out CrawlTarget>

    /*
    * Return whether we should visit (and analyze) given [target]
     */
    open fun shouldVisit(target: T) = true

}