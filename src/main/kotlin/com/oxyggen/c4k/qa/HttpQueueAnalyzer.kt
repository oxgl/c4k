package com.oxyggen.c4k.qa

import com.oxyggen.c4k.config.Config
import com.oxyggen.c4k.config.HttpConfig
import com.oxyggen.c4k.event.EngineEvent
import com.oxyggen.c4k.qa.robots.HttpRobotsTxt
import com.oxyggen.c4k.target.HttpTarget
import com.oxyggen.c4k.target.Target
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.UserAgent
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel

open class HttpQueueAnalyzer(
    coroutineScope: CoroutineScope,
    queueId: String,
    config: Config,
    engineChannel: SendChannel<EngineEvent>
) : QueueAnalyzer(coroutineScope, queueId, config, engineChannel) {

    val httpConfig = HttpConfig(config)
    lateinit var robotsTxt: HttpRobotsTxt

    var politenessDelay = if (robotsTxt.politenessDelay > 0) robotsTxt.politenessDelay else httpConfig.politenessDelay

    open fun createHttpClient(): HttpClient = HttpClient(Apache) {
        engine {
            socketTimeout = httpConfig.connectionTimeout
            connectTimeout = httpConfig.connectionTimeout
            connectionRequestTimeout = httpConfig.connectionTimeout
        }
        install(UserAgent) {
            agent = httpConfig.userAgent
        }
    }

    override fun shouldVisit(target: Target): Boolean =
        if (target is HttpTarget) {
            robotsTxt.isPathAllowed(target.url.path.complete)
        } else {
            false
        }

    open suspend fun processRobotsTxt() {
        val client = createHttpClient()
        val response = try {
            client.get<HttpResponse>("$queueId/robots.txt")
        } catch (e: Throwable) {
            null
        }

        if (response != null && response.status.value in 200..299) {
            val content = response.readText()
            robotsTxt = HttpRobotsTxt(content, httpConfig.userAgent)
            logger.debug("QueueId ${queueId}, robot.txt found: ${robotsTxt.group?.rules?.size}")
        }
    }

    override suspend fun startup() {
        processRobotsTxt()
        super.startup()
    }

}