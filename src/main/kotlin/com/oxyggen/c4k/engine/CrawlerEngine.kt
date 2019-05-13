package com.oxyggen.c4k.engine

import com.oxyggen.c4k.target.HttpTarget
import kotlinx.coroutines.CoroutineScope
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class CrawlerEngine() {

    suspend fun execute(scope: CoroutineScope) {
        //job = scope.launch {
        val jobQueue = CrawlerJobQueue(scope);

        logger.info { "Crawler engine coroutine context ${scope}" }

        logger.info { "Crawler started..." }
        for (i in 0..100) {
            jobQueue.addTarets(setOf(HttpTarget("http://google.com/$i")))
        }

        while (jobQueue.isJobActive()) {
            logger.info { "${jobQueue.getActiveJobCount()} jobs active, receiving result..." }

            val newTargets = jobQueue.receiveNewTargets()

            logger.info { "${newTargets.size} new targets, adding to queue" }

            jobQueue.addTarets(newTargets)

            logger.info { "Total targets in queue: ${jobQueue.getTargetCount()}" }
        }
        logger.info { "Crawler finished." }
        //}
    }

}