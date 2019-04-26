package com.oxyggen.c4k.target

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class HttpTargetTest {

    @Test
    fun `returns correct normalized Url`() {
        val url = HttpTarget("https://user@TEST.com/index.htm?query=abc#last")

        Assertions.assertEquals(url.getComparableUrl(true), "GET https://user@test.com/index.htm?query=abc#last")
        Assertions.assertEquals(url.getComparableUrl(false), "GET user@test.com/index.htm?query=abc#last")
    }


    @Test
    fun `http and https schema difference ignored`() {
        val url1 = HttpTarget("https://test.com/index.htm")
        val url2 = HttpTarget("http://test.com/index.htm")

        Assertions.assertEquals(url1, url2)
    }

    @Test
    fun `host name case ignored`() {
        val url1 = HttpTarget("https://TEST.com/index.htm")
        val url2 = HttpTarget("http://test.com/index.htm")

        Assertions.assertEquals(url1, url2)
    }

    @Test
    fun `different path not ignored`() {
        val url1 = HttpTarget("http://test.com/index.htm")
        val url2 = HttpTarget("http://test.com/INDEX.htm")

        Assertions.assertNotEquals(url1, url2)
    }

    @Test
    fun `different query not ignored`() {
        val url1 = HttpTarget("http://test.com/index.htm?query=abc")
        val url2 = HttpTarget("http://test.com/index.htm?query=ABC")

        Assertions.assertNotEquals(url1, url2)
    }

    @Test
    fun `different reference not ignored`() {
        val url1 = HttpTarget("http://test.com/index.htm#fragment")
        val url2 = HttpTarget("http://test.com/index.htm#REF")

        Assertions.assertNotEquals(url1, url2)
    }


}