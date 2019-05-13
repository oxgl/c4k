package com.oxyggen.c4k.analyzer

import com.oxyggen.c4k.target.CrawlTarget
import com.oxyggen.c4k.target.HttpTarget
import kotlinx.coroutines.delay
import java.net.URL
import kotlin.reflect.KClass

class HttpTargetAnalyzer : CrawlTargetAnalyzer<HttpTarget>() {

    override fun getHandledTargets(): Set<KClass<out HttpTarget>> = setOf(HttpTarget::class)

    override suspend fun analyze(target: HttpTarget): Set<out CrawlTarget> {
        println("---------------------------------------")
        println("Start of " + target.getUrl())

//        val result = URL(target.getUrl()).readText()

        //      println(result)

        delay(target.getUrl().length * 100L)

        println("Finished " + target.getUrl())

        if (target.getUrl().length > 20)
            return setOf()
        else
            return setOf(
                HttpTarget(target.getUrl() + "&a=a", parent = target),
                HttpTarget(target.getUrl() + "&a=b", parent = target)
            )
    }


}