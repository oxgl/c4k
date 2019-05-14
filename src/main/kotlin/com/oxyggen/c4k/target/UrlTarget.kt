package com.oxyggen.c4k.target

import java.net.MalformedURLException
import java.net.URL

open class UrlTarget(url: String, parent: CrawlTarget? = null) : CrawlTarget(parent) {
    // http://user@test.com:8080/public/find.html?word=abc#last
    open val scheme: String         // http
    open val user: String           // user
    open val host: String           // test.com
    open val port: Int              // 8080
    open val path: String           // /public/find.html
    open val query: String          // word=abc
    open val fragment: String       // last

    protected open val hashCode: Int by lazy {
        getComparableUrl(true).hashCode()
    }

    /*
     * Get URL string with or without scheme
     */
    open fun getUrl(withScheme: Boolean = true): String {
        var result = ""

        if (withScheme)
            result += "$scheme://"

        if (user.isNotBlank()) result += "$user@"
        if (host.isNotBlank()) result += host
        if (port > 0) result += ":$port"
        if (path.isNotBlank()) result += path
        if (query.isNotBlank()) result += "?$query"
        if (fragment.isNotBlank()) result += "#$fragment"

        return result;
    }

    /*
     * Get comparable URL -> same comparable URL means same target!
     */
    open fun getComparableUrl(withScheme: Boolean = false): String {
        return getUrl(withScheme)
    }

    init {
        var u: URL
        var foundScheme: String
        try {
            // Try to parse the URL
            // Protocol handlers for the following protocols are
            // guaranteed to exist: http, https, file, and jar
            u = URL(url)
            foundScheme = u.protocol.toLowerCase()
        } catch (ex: MalformedURLException) {
            // Throws an exception if it does not recognize the scheme
            val index = url.indexOf(':')
            foundScheme = url.take(index).toLowerCase()

            // So we will simulate "http" scheme
            u = URL("http" + url.drop(index))
        }

        scheme = foundScheme
        user = if (u.userInfo != null) u.userInfo else ""
        host = if (u.host != null) u.host.toLowerCase() else ""
        port = u.port
        path = if (u.path != null) u.path else ""
        query = if (u.query != null) u.query else ""
        fragment = if (u.ref != null) u.ref else ""
    }

    override fun hashCode(): Int {
        return hashCode
    }

    override fun toString(): String {
        return getComparableUrl(true);
    }


    override fun equals(other: Any?): Boolean {
        if (other is UrlTarget) {
            // Fast check (hashCode)
            if (this.hashCode() != other.hashCode())
                return false;

            // Slow comparison
            return getComparableUrl(true) == other.getComparableUrl(true);
        } else {
            return false
        }
    }
}