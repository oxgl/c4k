package com.oxyggen.c4k.analyzer

import com.oxyggen.c4k.target.CrawlTarget
import com.oxyggen.c4k.target.HttpTarget
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlin.reflect.KClass


class HttpTargetAnalyzer : CrawlTargetAnalyzer<HttpTarget>() {

    override fun getHandledTargets(): Set<KClass<out HttpTarget>> = setOf(HttpTarget::class)

    override suspend fun analyze(target: HttpTarget): Set<CrawlTarget> {

        delay( 500L)

        println("---------------------------------------")
        println("Start of " + target.getUrl())

        val client = HttpClient(Apache)
        try {
            val result = client.get<String>(target.getUrl());
            println(result)
        } catch (e: ClientRequestException) {

        }

        client.close()

        println("Finished " + target.getUrl())

        if (target.parent != null)
            return setOf()
        else
            return setOf(
                HttpTarget(target.getUrl() + "&a=a", parent = target),
                HttpTarget(target.getUrl() + "&a=b", parent = target)
            )
    }


}