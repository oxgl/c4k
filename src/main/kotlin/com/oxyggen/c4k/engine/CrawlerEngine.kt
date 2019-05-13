package com.oxyggen.c4k.engine

import com.oxyggen.c4k.target.HttpTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job


class CrawlerEngine() {

    var job: Job? = null

    suspend fun execute(scope: CoroutineScope) {
        //job = scope.launch {
        val jobQueue = CrawlerJobQueue(scope);

        println("Crawler engine coroutine context ${scope}")

        println("Crawler started...")
        for (i in 0..100) {
            jobQueue.addTarets(setOf(HttpTarget("http://google.com/$i")))
        }

        while (jobQueue.isJobActive()) {
            println("${jobQueue.getActiveJobCount()} jobs active, receiving result...")

            val newTargets = jobQueue.receiveNewTargets()

            println("${newTargets.size} new targets, adding to queue")

            jobQueue.addTarets(newTargets)

            println("Total targets in queue: ${jobQueue.getTargetCount()}")
        }
        println("Crawler finished.")
        //}
    }

    suspend fun await() {
        job?.join()
        job = null
    }


}