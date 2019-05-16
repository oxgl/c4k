package com.oxyggen.c4k.engine

import com.oxyggen.c4k.target.HttpTarget
import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class CrawlerEngineTest {

    @Test
    fun execute() = runBlocking {
        println("Test coroutine scope ${this}")

        val ce = CrawlerEngine(CrawlerConfig(politenessDelay = 200))

        ce.addTarget(HttpTarget("https://www.google.com"))
        ce.addTarget(HttpTarget("https://www.google.com?q=x"))
        ce.addTarget(HttpTarget("https://www.google.com?q=y"))
        ce.addTarget(HttpTarget("https://www.google.com?q=z"))

        ce.execute(this)

        println("out!")
    }
}