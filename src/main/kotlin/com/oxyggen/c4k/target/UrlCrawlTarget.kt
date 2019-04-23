package com.oxyggen.c4k.target

import java.net.MalformedURLException
import java.net.URL

open class UrlCrawlTarget(val url: String) : CrawlTarget() {
    // http://user@test.com:8080/public/find.html?word=abc#last
    var scheme: String private set        // http
    var user: String private set          // user
    var host: String private set          // test.com
    var port: Int private set             // 8080
    var path: String private set          // /public/find.html
    var query: String private set         // word=abc
    var ref: String private set           // last


    init {
        var u: URL
        try {
            // Try to parse the URL
            // Protocol handlers for the following protocols are
            // guaranteed to exist: http, https, file, and jar
            u = URL(url)
            scheme = u.protocol.toLowerCase()
        } catch (ex: MalformedURLException) {
            // Throws an exception if it does not recognize the scheme
            val index = url.indexOf(':')
            scheme = url.take(index).toLowerCase()

            // So we will simulate "http" scheme
            u = URL("http" + url.drop(index))
        }

        user = if (u.userInfo != null) u.userInfo else ""
        host = if (u.host != null) u.host.toLowerCase() else ""
        port = u.port
        path = if (u.path != null) u.path else ""
        query = if (u.query != null) u.query else ""
        ref = if (u.ref != null) u.ref else ""
    }


    override fun equals(other: Any?): Boolean {
        if (other is UrlCrawlTarget) {
            return this.url == other.url
        } else {
            return false
        }
    }
}