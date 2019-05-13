package com.oxyggen.c4k.engine

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class CrawlerEngineTest {

    @Test
    fun execute() = runBlocking {
        println("Test coroutine scope ${this}")

        CrawlerEngine().execute(this)

        println("out!")
    }
}