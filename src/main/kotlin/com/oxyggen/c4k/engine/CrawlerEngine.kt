package com.oxyggen.c4k.engine

import com.oxyggen.c4k.analyzer.HttpTargetAnalyzer
import com.oxyggen.c4k.target.CrawlTarget
import com.oxyggen.c4k.target.HttpTarget
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select

class CrawlerEngine() {

    fun execute() = runBlocking {

        val jobQueue = CrawlerJobQueue(this);

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


        println("Crawler finished")

    }


}