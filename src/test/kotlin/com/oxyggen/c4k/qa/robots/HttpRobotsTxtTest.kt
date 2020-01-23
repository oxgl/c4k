package com.oxyggen.c4k.qa.robots

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.net.URL

internal class HttpRobotsTxtTest {
    @Test
    fun `test robots txt parsing`() {
        val rs = URL("https://www.google.com/robots.txt").readText()
        val rtxt = HttpRobotsTxt(rs, "Oxyg")

        assertTrue(rtxt.isPathAllowed("/"))
        assertFalse(rtxt.isPathAllowed("/?"))
        assertFalse(rtxt.isPathAllowed("/?helpme"))
        assertTrue(rtxt.isPathAllowed("/maps?smallfile=xxx"))
        assertTrue(rtxt.isPathAllowed("/maps/d/"))
        assertTrue(rtxt.isPathAllowed("/maps/d/x.html"))
        assertTrue(rtxt.isPathAllowed("/maps/d.html"))

    }
}