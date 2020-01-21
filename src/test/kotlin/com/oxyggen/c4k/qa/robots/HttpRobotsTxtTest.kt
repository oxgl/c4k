package com.oxyggen.c4k.qa.robots

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.net.URL

internal class HttpRobotsTxtTest {
    @Test
    fun `test robots txt parsing`() {
        val rs = URL("https://en.wikipedia.org/robots.txt").readText()
        val rtxt = HttpRobotsTxt(rs)
        println(rtxt)
    }
}