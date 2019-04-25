package com.oxyggen.c4k.analyzer

import com.oxyggen.c4k.target.HttpTarget
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class HttpTargetAnalyzerTest {
    @Test
    fun `test handled class`() {
        var x = HttpTargetAnalyzer().getHandledClasses()
        assert(x.contains(HttpTarget::class))
    }
}