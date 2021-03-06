package com.oxyggen.c4k.old.analyzer

import com.oxyggen.c4k.target.HttpTarget
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class HttpTargetAnalyzerTest {
    @Test
    fun `test handled class`() {
        var ta = HttpTargetAnalyzer()

        var x = HttpTargetAnalyzer().getHandledTargets()

        runBlocking { ta.analyze(HttpTarget("https://www.distrowatch.com")) }

        assert(x.contains(HttpTarget::class))
    }
}