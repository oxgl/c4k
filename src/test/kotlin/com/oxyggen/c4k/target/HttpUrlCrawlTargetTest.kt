package com.oxyggen.c4k.target

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class HttpUrlCrawlTargetTest {

    @Test
    fun `returns correct normalized Url`() {
        val url= HttpUrlCrawlTarget("https://user@TEST.com/index.htm?query=abc#last")

        Assertions.assertEquals(url.getNormalizedUrl(true), "https://user@test.com/index.htm?query=abc#last" )
        Assertions.assertEquals(url.getNormalizedUrl(false), "user@test.com/index.htm?query=abc#last" )
    }


    @Test
    fun `http and https schema difference ignored`() {
        val url1 = HttpUrlCrawlTarget("https://test.com/index.htm")
        val url2 = HttpUrlCrawlTarget("http://test.com/index.htm")

        Assertions.assertEquals(url1, url2)
    }

    @Test
    fun `host name case ignored`() {
        val url1 = HttpUrlCrawlTarget("https://TEST.com/index.htm")
        val url2 = HttpUrlCrawlTarget("http://test.com/index.htm")

        Assertions.assertEquals(url1, url2)
    }

    @Test
    fun `different path not ignored`() {
        val url1 = HttpUrlCrawlTarget("http://test.com/index.htm")
        val url2 = HttpUrlCrawlTarget("http://test.com/INDEX.htm")

        Assertions.assertNotEquals(url1, url2)
    }

    @Test
    fun `different query not ignored`() {
        val url1 = HttpUrlCrawlTarget("http://test.com/index.htm?query=abc")
        val url2 = HttpUrlCrawlTarget("http://test.com/index.htm?query=ABC")

        Assertions.assertNotEquals(url1, url2)
    }

    @Test
    fun `different reference not ignored`() {
        val url1 = HttpUrlCrawlTarget("http://test.com/index.htm#ref")
        val url2 = HttpUrlCrawlTarget("http://test.com/index.htm#REF")

        Assertions.assertNotEquals(url1, url2)
    }


}