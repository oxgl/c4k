package com.oxyggen.c4k.engine

import com.oxyggen.c4k.config.Config
import com.oxyggen.c4k.event.EngineEvent
import com.oxyggen.c4k.event.QueueEvent
import com.oxyggen.c4k.qa.QueueAnalyzer
import com.oxyggen.c4k.persistency.Target
import com.oxyggen.matcher.GlobMatcher
import com.oxyggen.matcher.Matcher
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.apache.logging.log4j.kotlin.Logging
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

open class CrawlEngine(val config: Config, val coroutineScope: CoroutineScope = GlobalScope) : Logging {

    /**
     *
     */
    protected data class QAEntry(
        val matcher: Matcher,
        val queueAnalyzerClass: KClass<out QueueAnalyzer>,
        val priority: Int
    )

    protected val registeredQAs: MutableList<QAEntry> = mutableListOf()

    protected val engineChannel = Channel<EngineEvent>(Channel.UNLIMITED)

    protected val queues: MutableMap<String, QueueAnalyzer> = mutableMapOf()
    protected val queuesMutex = Mutex()

    protected suspend fun getQueueAnalyzer(queueId: String): QueueAnalyzer? = queuesMutex.withLock {
        var queueAnalyzer = queues[queueId]

        if (queueAnalyzer == null) {
            val qaClass = registeredQAs.find { it.matcher.matches(queueId) }?.queueAnalyzerClass
            if (qaClass != null) {
                // Try to create QA Instance
                val qaConstructorParams: MutableMap<KParameter, Any> = mutableMapOf()

                val qaConstructor = qaClass.primaryConstructor

                qaConstructor?.parameters?.forEach {
                    when (it.name) {
                        "coroutineScope" -> qaConstructorParams[it] = coroutineScope
                        "queueId" -> qaConstructorParams[it] = queueId
                        "config" -> qaConstructorParams[it] = config
                        "engineChannel" -> qaConstructorParams[it] = engineChannel
                    }
                }

                // Create query analyzer object
                queueAnalyzer = qaConstructor?.callBy(qaConstructorParams) ?: qaClass.createInstance()

                // Put into queues set
                queues[queueId] = queueAnalyzer

                // Write log
                logger.debug { "Engine: New QueueAnalyzer with class $qaClass was created for queue $queueId" }

                // Startup with coroutine (do not wait)
                coroutineScope.async { queueAnalyzer.startup() }
            }
        }
        queueAnalyzer
    }


    open fun registerQueueAnalyzer(
        matcher: Matcher,
        queueAnalyzerClass: KClass<out QueueAnalyzer>,
        priority: Int = 0
    ) {
        registeredQAs.add(QAEntry(matcher, queueAnalyzerClass, priority))
        registeredQAs.sortBy { -it.priority }
    }

    open fun registerQueueAnalyzer(
        globPattern: String,
        queueAnalyzerClass: KClass<out QueueAnalyzer>,
        priority: Int = 0
    ) = registerQueueAnalyzer(GlobMatcher(globPattern), queueAnalyzerClass, priority)

    open fun addTarget(target: Target): Boolean = engineChannel.offer(
        EngineEvent(
            EngineEvent.Type.REROUTE,
            target
        )
    )

    open suspend fun router() {
        logger.debug { "Engine: Router started" }
        var isActive = true
        while (isActive) {
            val event = engineChannel.receive()
            logger.debug { "Engine: Event received: $event" }
            when (event.type) {
                EngineEvent.Type.REROUTE -> {
                    // Get QueueAnalyzer for given QueueId
                    val qa = getQueueAnalyzer(event.target!!.getQueueId())

                    // Send event into analyzer if
                    qa?.inputChannel?.send(
                        QueueEvent(
                            QueueEvent.Type.REROUTE_TARGET,
                            event.target
                        )
                    )
                }

                EngineEvent.Type.ABORT -> {
                    queues.forEach {
                        it.value.inputChannel.send(QueueEvent(QueueEvent.Type.ABORT))
                    }
                    isActive = false
                }
            }
        }
        logger.debug { "Engine: Router exited" }
    }

    open suspend fun execute(): Unit {
        router()
    }

    open suspend fun stop(): Unit {
        engineChannel.send(EngineEvent(EngineEvent.Type.ABORT))
    }

    open fun executeBlocking() = runBlocking(coroutineScope.coroutineContext) { execute() }
}