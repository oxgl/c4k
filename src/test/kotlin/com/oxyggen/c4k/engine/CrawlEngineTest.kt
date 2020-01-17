package com.oxyggen.c4k.engine

import com.oxyggen.c4k.config.HttpConfig
import com.oxyggen.c4k.qa.HttpQueueAnalyzer
import com.oxyggen.c4k.target.HttpTarget
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class CrawlEngineTest {

    @Test
    fun `Executing and stopping engine after 2s`() {
        runBlocking {
            val e = CrawlEngine(HttpConfig(), this)
            e.registerQueueAnalyzer("https://*", HttpQueueAnalyzer::class)
            e.registerQueueAnalyzer("http://*", HttpQueueAnalyzer::class)
            e.addTarget(HttpTarget("https://google.com"))
            launch { e.execute() }
            delay(2000)
            launch { e.stop() }
        }
    }

    @Test
    fun `Executing engine in blocking mode`() {
        val e = CrawlEngine(HttpConfig())
        e.registerQueueAnalyzer("https://*", HttpQueueAnalyzer::class)
        e.registerQueueAnalyzer("http://*", HttpQueueAnalyzer::class)
        e.addTarget(HttpTarget("https://google.com"))
        //e.executeBlocking()
    }


    @Test
    fun addTarget() {
    }
}