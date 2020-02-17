package com.oxyggen.c4k.qa

import com.oxyggen.c4k.config.Config
import com.oxyggen.c4k.event.EngineEvent
import com.oxyggen.c4k.event.QueueEvent
import com.oxyggen.c4k.store.TargetMemoryStore
import com.oxyggen.c4k.target.Target
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.apache.logging.log4j.kotlin.Logging

open class QueueAnalyzer(
    protected val coroutineScope: CoroutineScope,
    protected val queueId: String,
    protected val config: Config,
    protected val engineChannel: SendChannel<EngineEvent>
) : Logging {

    protected val eventChannel = Channel<QueueEvent>(Channel.UNLIMITED)
    protected val targetChannel: Channel<Target> = Channel<Target>(Channel.UNLIMITED)
    protected val tqMutex = Mutex()
    protected val tq = TargetMemoryStore()

    protected lateinit var eventReceiverJob: Job
    protected lateinit var targetSchedulerJob: Job

    /**
     * Send target to engine which will try to reroute the event
     * to the corresponding QueueAnalyzer
     * @param target the target to reroute
     */
    open suspend fun rerouteTarget(target: Target) =
        engineChannel.send(
            EngineEvent(
                EngineEvent.Type.REROUTE,
                target
            )
        )

    /**
     * The input channel which receives the events from engine
     */
    open val inputChannel: SendChannel<QueueEvent>
        get() = eventChannel

    /**
     * Event was received from queueChannel
     * @param event the received event
     * @return true if queueReceiver should continue receiving events,false to exit queueReceiver
     */
    protected open suspend fun eventReceived(event: QueueEvent): Boolean {
        when (event.type) {
            QueueEvent.Type.ADD_TARGET, QueueEvent.Type.REROUTE_TARGET -> {
                if (event.target != null) {
                    tqMutex.withLock {
                        if (tq.isTargetStored(event.target)) {
                            logger.debug { "QA $queueId target ${event.target} alredy known, skipping..." }
                        } else {
                            logger.debug { "QA $queueId new target ${event.target} received, adding to store..." }
                            tq.enqueueWaitingTarget(event.target)
                            targetChannel.offer(event.target)
                        }
                    }
                }
                return true
            }
            QueueEvent.Type.ABORT -> {
                return false    // Exit queueReciever
            }
            else -> {
                return true     // Continue
            }
        }
    }

    /**
     * Loop which waits for events from engine
     * the received event is sent to fun eventReceived
     */
    protected open suspend fun eventReceiver() {
        eventReceiverJob = coroutineScope.launch {
            var isActive = true
            logger.debug { "QA $queueId waiting for events..." }
            while (isActive) {
                val event = eventChannel.receive()
                logger.debug { "QA $queueId event received: $event" }
                isActive = eventReceived(event)
            }

            if (::targetSchedulerJob.isInitialized) {
                targetSchedulerJob.cancelAndJoin()
                logger.debug { "QA $queueId targetScheduler cancelled" }
            }

            logger.debug { "QA $queueId exiting" }
        }
    }

    protected open fun getTargetSchedulerTimeout(): Long = 0L

    protected open suspend fun processTarget(target: Target) {
        logger.debug { "QA $queueId processing target $target" }
    }

    protected open suspend fun targetScheduler() {
        targetSchedulerJob = coroutineScope.launch {
            val timeout = getTargetSchedulerTimeout()
            logger.debug { "QA $queueId targetScheduler executed, scheduler timeout $timeout..." }
            while (isActive) {
                val nextTarget = targetChannel.receive()
                tqMutex.withLock {
                    
                }
                delay(timeout)

                processTarget(nextTarget)
            }
        }
    }

    /**
     * Returns whether the target should be visited
     */
    protected open fun shouldVisit(target: Target): Boolean = true

    /**
     * Starts up the QueueAnalyzer.
     * Called by engine immediately after creation.
     */
    open suspend fun startup() {
        targetScheduler()
        eventReceiver()
    }

}