package com.oxyggen.c4k.engine

import com.oxyggen.c4k.analyzer.HttpTargetAnalyzer
import com.oxyggen.c4k.target.CrawlTarget
import com.oxyggen.c4k.target.HttpTarget
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select

class CrawlerEngine() {

    private var targetQueue: MutableSet<CrawlTarget> = mutableSetOf()
    private var jobs: MutableMap<CrawlTarget, CrawlerJob> = mutableMapOf()

    private fun addTargets(foundTargets: Set<CrawlTarget>) {
        foundTargets.forEach {
            synchronized(jobs) {
                if (jobs.contains(it)) {
                    println("Target already in jobs, skipping $it...")
                } else if (targetQueue.contains(it)) {
                    println("Target already in queue, skipping $it...")
                } else {
                    targetQueue.add(it);
                    println("Target added $it")
                }
            }
        }
    }

    private fun getNewTargets(): Set<CrawlTarget> {
        var result: Set<CrawlTarget> = setOf()
        synchronized(jobs) {
            result = targetQueue
            targetQueue = mutableSetOf()
        }
        return result
    }

    private fun addJob(target: CrawlTarget, crawlerJob: CrawlerJob) {
        synchronized(jobs) {
            jobs.put(target, crawlerJob)
        }
    }


    fun execute() = runBlocking {

        println("Main start")
        for (i in 0..100) {
            addTargets(setOf(HttpTarget("http://google.com/$i")))
        }

        var isDone = false
//        do {
            val newTargets = getNewTargets()

            newTargets.forEach {
                val asyncJob = async(Dispatchers.Default) {
                    if (it is HttpTarget) {
                        HttpTargetAnalyzer().analyze(it)
                    } else {
                        setOf()
                    }
                }
                addJob(it, CrawlerJob(asyncJob))
            }

            val finishedJob = select<Map.Entry<CrawlTarget, CrawlerJob>> {
                jobs.forEach {
                    it.value.job.onAwait { _ ->
                        it
                    }
                }
            }

            println("XXXXXXXXXXXX -> $finishedJob")

//        } while (finishedJob != null)

        println("Main finished")

    }


}