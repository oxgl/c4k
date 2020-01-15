package com.oxyggen.c4k.old.engine

import com.oxyggen.c4k.target.HttpTarget
import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

internal class CrawlerEngineTest {

    @Test
    fun execute() = runBlocking {
        println("Test coroutine scope ${this}")

        val ce = CrawlerEngine(
            CrawlerEngine.Config(
                politenessDelay = 2000,
                maxDepth = 2
            )
        )

        //ce.addTarget(HttpTarget("https://www.wikipedia.org/"))
        ce.addTarget(HttpTarget("https://google.com/"))

        ce.execute(this)

        println("out!")
    }
}