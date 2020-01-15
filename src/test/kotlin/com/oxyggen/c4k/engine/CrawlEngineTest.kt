package com.oxyggen.c4k.engine

import com.oxyggen.c4k.config.HttpConfig
import com.oxyggen.c4k.qa.CrawlQueueAnalyzer
import com.oxyggen.c4k.target.HttpTarget
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class CrawlEngineTest {

    @Test
    fun `Creating new engine`() = runBlocking{
        val e = CrawlEngine(HttpConfig())
        e.registerQueueAnalyzer("https://*", CrawlQueueAnalyzer::class)
        e.registerQueueAnalyzer("http://*", CrawlQueueAnalyzer::class)
        e.addTarget(HttpTarget("https://google.com"))
        e.execute(this)
    }

    @Test
    fun addTarget() {
    }
}