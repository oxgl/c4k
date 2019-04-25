package com.oxyggen.c4k.analyzer

import com.oxyggen.c4k.target.CrawlTarget
import kotlin.reflect.KClass

abstract class AbstractTargetAnalyzer<T> where T : CrawlTarget {

    /**
     * Should return the set of target classes handled by current target analyzer
     */
    abstract fun getHandledClasses(): Set<KClass<out CrawlTarget>>

    /**
     * Analyze given [target]
     */
    abstract suspend fun analyze(target: T): Set<out CrawlTarget>

    /*
    * Return whether we should visit (and analyze) given [target]
     */
    open fun shouldVisit(target: T) = true

}