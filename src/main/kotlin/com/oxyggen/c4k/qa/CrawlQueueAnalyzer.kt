package com.oxyggen.c4k.qa

import com.oxyggen.c4k.config.CrawlConfig
import com.oxyggen.c4k.config.HttpConfig
import com.oxyggen.c4k.engine.CrawlEvent
import com.oxyggen.c4k.store.CrawlTargetMemoryStore
import com.oxyggen.c4k.target.CrawlTarget
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import org.apache.logging.log4j.kotlin.Logging

open class CrawlQueueAnalyzer(
    protected val queueId: String,
    protected val config: CrawlConfig,
    protected val rerouteChannel: SendChannel<CrawlEvent>
) :
    Logging {

    val receiveChannel = Channel<CrawlEvent>()

    private val schedulingChannel: Channel<Long> = Channel<Long>()

    val tq = CrawlTargetMemoryStore()

    open suspend fun rerouteTarget(target: CrawlTarget) =
        rerouteChannel.send(CrawlEvent(CrawlEvent.Type.REROUTE, target))

    open fun addTarget(target: CrawlTarget): Boolean = tq.enqueueWaitingTarget(target)

    open fun startup() {
        println("QueueId ${queueId}")
        println("Max depth: " + config[HttpConfig.MAX_DEPTH])
        println("Delay: " + config[HttpConfig.POLITENESS_DELAY])

    }

}