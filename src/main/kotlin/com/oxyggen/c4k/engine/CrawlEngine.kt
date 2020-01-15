package com.oxyggen.c4k.engine

import com.oxyggen.c4k.config.CrawlConfig
import com.oxyggen.c4k.qa.CrawlQueueAnalyzer
import com.oxyggen.c4k.target.CrawlTarget
import com.oxyggen.matcher.GlobMatcher
import com.oxyggen.matcher.Matcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.Logging
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

open class CrawlEngine(val config: CrawlConfig) : Logging {

    /**
     *
     */
    protected data class QAEntry(
        val matcher: Matcher,
        val queueAnalyzerClass: KClass<out CrawlQueueAnalyzer>,
        val priority: Int
    )

    protected val registeredQAs: MutableList<QAEntry> = mutableListOf()

    protected val rerouteChannel = Channel<CrawlEvent>(Channel.UNLIMITED)


    protected val queues: MutableMap<String, CrawlQueueAnalyzer> = mutableMapOf()

    protected fun getQueueAnalyzer(queueId: String): CrawlQueueAnalyzer? {

        var result = queues[queueId]

        if (result == null) {
            val qaClass = registeredQAs.find { it.matcher.matches(queueId) }?.queueAnalyzerClass
            if (qaClass != null) {
                // Try to create QA Instance
                val qaConstructorParams: MutableMap<KParameter, Any> = mutableMapOf()

                val qaConstructor = qaClass.primaryConstructor

                qaConstructor?.parameters?.forEach {
                    when (it.name) {
                        "queueId" -> qaConstructorParams[it] = queueId
                        "config" -> config
                        "rerouteChannel" -> qaConstructorParams[it] = rerouteChannel
                    }
                }

                result = qaConstructor?.callBy(qaConstructorParams) ?: qaClass.createInstance()

                if (result != null) {
                    logger.debug { "New QueueAnalyzer with class $qaClass was created for queue $queueId" }
                    result.startup()
                    queues[queueId] = result
                }
            }
        }
        return result
    }


    open fun registerQueueAnalyzer(
        matcher: Matcher,
        queueAnalyzerClass: KClass<out CrawlQueueAnalyzer>,
        priority: Int = 0
    ) {
        registeredQAs.add(QAEntry(matcher, queueAnalyzerClass, priority))
        registeredQAs.sortBy { -it.priority }
    }

    open fun registerQueueAnalyzer(
        globPattern: String,
        queueAnalyzerClass: KClass<out CrawlQueueAnalyzer>,
        priority: Int = 0
    ) = registerQueueAnalyzer(GlobMatcher(globPattern), queueAnalyzerClass, priority)

    open fun addTarget(target: CrawlTarget): Boolean = rerouteChannel.offer(CrawlEvent(CrawlEvent.Type.REROUTE, target))

    open suspend fun router() {
        logger.debug { ">> Router started" }
        var isActive = true
        while (isActive) {
            val event = rerouteChannel.receive()
            logger.debug { "Router event received: $event" }
            when (event.type) {
                CrawlEvent.Type.REROUTE -> getQueueAnalyzer(event.target!!.getQueueId())?.receiveChannel?.send(event)
                CrawlEvent.Type.ABORT -> isActive = false
            }
        }
        logger.debug { ">> Logger exited" }
    }

    open suspend fun execute(scope: CoroutineScope) {
        scope.launch { router() }
        delay(1000)

    }

}