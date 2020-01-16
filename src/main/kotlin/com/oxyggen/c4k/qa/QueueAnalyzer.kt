package com.oxyggen.c4k.qa

import com.oxyggen.c4k.config.Config
import com.oxyggen.c4k.config.HttpConfig
import com.oxyggen.c4k.event.EngineEvent
import com.oxyggen.c4k.event.QueueEvent
import com.oxyggen.c4k.store.TargetMemoryStore
import com.oxyggen.c4k.target.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.Logging

open class QueueAnalyzer(
    protected val coroutineScope: CoroutineScope,
    protected val queueId: String,
    protected val config: Config,
    protected val engineChannel: SendChannel<EngineEvent>
) :
    Logging {

    protected val queueChannel = Channel<QueueEvent>(Channel.UNLIMITED)
    protected val schedulingChannel: Channel<Long> = Channel<Long>()
    protected val tq = TargetMemoryStore()

    open suspend fun rerouteTarget(target: Target) =
        engineChannel.send(
            EngineEvent(
                EngineEvent.Type.REROUTE,
                target
            )
        )

    open val inputChannel: SendChannel<QueueEvent>
        get() = queueChannel

    protected open suspend fun queueReceiver() {
        var isActive = true
        logger.debug { ">> Queue $queueId waiting for events" }
        while (isActive) {
            val event = queueChannel.receive()
            logger.debug { "Queue event received: $event" }

            when (event.type) {
                QueueEvent.Type.ADD_TARGET, QueueEvent.Type.REROUTE_TARGET -> {

                }
                QueueEvent.Type.ABORT -> isActive = false
            }
        }
        logger.debug { ">> Queue $queueId exiting..." }
    }

    open fun startup() {
        with (coroutineScope) {
            launch { queueReceiver() }
        }

    }

}