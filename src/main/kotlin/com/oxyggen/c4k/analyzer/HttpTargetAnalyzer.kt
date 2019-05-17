package com.oxyggen.c4k.analyzer

import com.oxyggen.c4k.target.CrawlTarget
import com.oxyggen.c4k.target.HttpTarget
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.request
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import io.ktor.http.HttpStatusCode
import io.ktor.http.takeFrom
import kotlinx.io.charsets.Charset
import org.apache.logging.log4j.kotlin.Logging
import org.jsoup.Jsoup
import java.net.URI
import java.net.URLEncoder
import kotlin.reflect.KClass

open class HttpTargetAnalyzer : CrawlTargetAnalyzer(), Logging {

    override fun getHandledTargets(): Set<KClass<out HttpTarget>> = setOf(HttpTarget::class)

    override suspend fun analyze(target: CrawlTarget): Set<CrawlTarget> = when (target) {
        is HttpTarget -> {
            logger.debug { ">>> HTTP Crawler started for target ${target.getUrlString()}" }

            val client = HttpClient(Apache) {
                engine {
                    followRedirects = true
                    socketTimeout = 20_000
                    connectTimeout = 20_000
                }
            }

            val response = try {
                client.request<HttpResponse>() {
                    url.takeFrom(target.getUrlString())
                    method = target.method
                }
            } catch (e: ClientRequestException) {
                logger.warn("Error when downloading target ${target.getUrlString()}: ${e.message}")
                null
            }

            if (response == null) {

                client.close()

                setOf()

            } else {

                logger.debug { "Target ${target.getUrlString()} returned status ${response.status.value}" }

                val targets = collectTargets(target, response)

                response.close()
                client.close()

                logger.debug { "<<< HTTP Crawler finished" }

                targets
            }
        }
        else -> {
            setOf();
        }
    }

    open suspend fun collectTargets(target: HttpTarget, response: HttpResponse): Set<CrawlTarget> {
        val result = mutableSetOf<CrawlTarget>()

        when (response.status) {
            HttpStatusCode.OK -> {
                val html = try {
                    response.readText(Charset.forName("UTF-8"))
                } catch (t: Throwable) {
                    ""
                }

                val doc = Jsoup.parse(html)

                val a = doc.select("a")
                if (a != null) {
                    a.forEach {
                        val href = it.attr("href").trim()
                        if (href.isNotBlank()) {
                            val newTarget = HttpTarget(href, parent = target)

                            if (newTarget.valid) {
                                when (newTarget.scheme) {
                                    "http", "https" -> result.add(newTarget)
                                }
                            } else {
                                logger.warn("Malformed URI: $href on page ${target.targetIdentifier}")
                            }
                        }
                    }
                }
            }
        }
        return result
    }


}