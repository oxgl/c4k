package com.oxyggen.c4k.analyzer

import com.oxyggen.c4k.target.CrawlTarget
import com.oxyggen.c4k.target.HttpTarget
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import org.apache.logging.log4j.kotlin.Logging
import kotlin.reflect.KClass

open class HttpTargetAnalyzer : CrawlTargetAnalyzer(), Logging {

    override fun getHandledTargets(): Set<KClass<out HttpTarget>> = setOf(HttpTarget::class)

    override suspend fun analyze(target: CrawlTarget): Set<CrawlTarget> = when (target) {
        is HttpTarget -> {
            logger.info { "HTTP Crawler started for target ${target.getUrl()}" }

            val response = download(target)

            if (response == null) {

                setOf()

            } else {

                logger.info { "Target ${target.getUrl()} returned status ${response.status.value}" }

                val targets = collectTargets(target, response)

                response.close()

                logger.info { "HTTP Crawler finished" }

                targets
            }
        }
        else -> {
            setOf();
        }
    }

    open suspend fun download(target: HttpTarget): HttpResponse? {
        val client = HttpClient(Apache)
        val response: HttpResponse? =
            try {
                client.get<HttpResponse>(target.getUrl())
            } catch (e: ClientRequestException) {
                logger.warn("Error when downloading target ${target.getUrl()}: ${e.message}")
                null
            }

        return response
    }

    open suspend fun collectTargets(target: HttpTarget, response: HttpResponse): Set<CrawlTarget> {
        val result = mutableSetOf<CrawlTarget>()

        if (target == HttpTarget("https://www.google.com"))
            for (i in 0..20)
            result.add(
                HttpTarget(
                    parent = target,
                    url = "https://www.google.com/search?q=google$i"
                )
            )
        return result

/*        try {
            val x = response.readText()


            val doc = org.jsoup.Jsoup.parse(x)
        } catch (e: Exception) {
            logger.warn("Error when reading response ${e.message}")
            println("Exception ${e.message}")
        }

        return setOf()*/
    }


}