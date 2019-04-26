package com.oxyggen.c4k.analyzer

import com.oxyggen.c4k.target.HttpTarget
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class HttpTargetAnalyzerTest {
    @Test
    fun `test handled class`() {
        var ta = HttpTargetAnalyzer()

        var x = HttpTargetAnalyzer().getHandledTargets()

        runBlocking { ta.analyze(HttpTarget("http://www.google.com")) }

        assert(x.contains(HttpTarget::class))
    }
}