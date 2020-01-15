package com.oxyggen.c4k.old.analyzer

import com.oxyggen.c4k.target.CrawlTarget
import com.oxyggen.c4k.target.HttpTarget
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.UserAgent
import io.ktor.client.request.request
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import io.ktor.http.HttpStatusCode
import io.ktor.http.takeFrom
import kotlinx.io.charsets.Charset
import org.apache.logging.log4j.kotlin.Logging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.reflect.KClass

open class HttpTargetAnalyzer(val config: Config = Config()) : CrawlTargetAnalyzer(), Logging {

    data class Config(
        val followRedirects: Boolean = true,
        val socketTimeout: Int = 20_000,
        val connectTimeout: Int = 20_000,
        val userAgent: String = "Mozilla/5.0 c4k/1.0"
    )

    override fun getHandledTargets(): Set<KClass<out HttpTarget>> = setOf(HttpTarget::class)

    override suspend fun analyze(target: CrawlTarget): Set<CrawlTarget> = when (target) {
        is HttpTarget -> {
            logger.debug { ">>> HTTP Crawler started for target ${target.uri.toString()}" }

            val client = HttpClient(Apache) {
                engine {
                    followRedirects = config.followRedirects
                    socketTimeout = config.socketTimeout
                    connectTimeout = config.connectTimeout
                }
                install(UserAgent) {
                    agent = config.userAgent
                }
            }

            val targetUri = target.uri.toResolvedUriString()

            val response = try {
                client.request<HttpResponse>() {
                    url.takeFrom(targetUri)
                    method = target.method
                }
            } catch (e: Exception) {
                logger.warn("Error when downloading target ${targetUri}: ${e.message}")
                null
            }

            if (response == null) {

                client.close()

                setOf()

            } else {

                logger.debug { "Target ${target.uri.toResolvedUriString()} returned status ${response.status.value}" }

                val targets = analyzeResponse(target, response)

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

    /**
     * Collect data from document
     */
    protected open suspend fun collectData(target: HttpTarget, doc: Document) {

    }

    /**
     * Collect new targets from document
     * @return the set of new targets
     */
    protected open suspend fun collectTargets(target: HttpTarget, doc: Document): Set<CrawlTarget> {
        val result = mutableSetOf<CrawlTarget>()
        doc.select("a")?.forEach {
            val href = it.attr("href").trim()
            if (href.isNotBlank()) {
                try {
                    val newTarget = HttpTarget(href, parent = target)
                    when (newTarget.uri.scheme) {
                        "http", "https" -> result.add(newTarget)
                    }
                } catch (e: IllegalArgumentException) {
                    logger.warn("Malformed URI: $href on page ${target.targetIdentifier}")
                }
            }
        }
        return result
    }

    /**
     * Analyze the HTTP response:
     *  - parse document and collect the needed data
     *  - parse document and collect all new targets
     * @return the new targets
     */
    protected open suspend fun analyzeResponse(target: HttpTarget, response: HttpResponse): Set<CrawlTarget> =
        when (response.status) {
            HttpStatusCode.OK -> {
                val html = try {
                    response.readText(Charset.forName("UTF-8"))
                } catch (t: Throwable) {
                    ""
                }

                val doc = Jsoup.parse(html)

                collectData(target, doc)

                collectTargets(target, doc)
            }
            else -> setOf<CrawlTarget>()
        }

}