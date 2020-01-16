package com.oxyggen.c4k.old.analyzer

import com.oxyggen.c4k.target.Target
import kotlin.reflect.KClass

abstract class CrawlTargetAnalyzer {

    /**
     * Should return the set of target classes handled by current target analyzer
     */
    abstract fun getHandledTargets(): Set<KClass<out Target>>

    /**
     * Analyze given [target] and get the new targets
     */
    abstract suspend fun analyze(target: Target): Set<Target>

    /*
    * Return whether we should visit (and analyze) given [target]
     */
    open fun shouldVisit(target: Target) = true

}