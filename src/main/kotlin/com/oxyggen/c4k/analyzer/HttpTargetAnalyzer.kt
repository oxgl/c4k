package com.oxyggen.c4k.analyzer

import com.oxyggen.c4k.target.CrawlTarget
import com.oxyggen.c4k.target.HttpTarget
import kotlin.reflect.KClass

class HttpTargetAnalyzer : AbstractTargetAnalyzer<HttpTarget>() {

    override fun getHandledClasses(): Set<KClass<out HttpTarget>> = setOf(HttpTarget::class)

    override suspend fun analyze(target: HttpTarget): Set<out CrawlTarget> {
        return setOf(HttpTarget("http://xxx"))
    }


}