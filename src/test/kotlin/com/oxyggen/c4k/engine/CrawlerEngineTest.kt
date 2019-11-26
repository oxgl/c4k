package com.oxyggen.c4k.engine

import com.oxyggen.c4k.analyzer.HttpTargetAnalyzer
import com.oxyggen.c4k.target.HttpTarget
import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

internal class CrawlerEngineTest {

    @Test
    fun execute() = runBlocking {
        println("Test coroutine scope ${this}")

        val ce = CrawlerEngine(CrawlerEngine.Config(politenessDelay = 200, maxDepth = 2))

        //ce.addTarget(HttpTarget("https://www.wikipedia.org/"))
        ce.addTarget(HttpTarget("https://distrowatch.com"))

        ce.execute(this)

        println("out!")
        val x = HttpTargetAnalyzer.Config()
    }
}