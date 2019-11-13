package com.oxyggen.c4k.target

import com.oxyggen.net.HttpURL
import io.ktor.http.HttpMethod

open class HttpTarget(urlString: String, val method: HttpMethod = HttpMethod.Get, parent: CrawlTarget? = null) :
    UriTarget(urlString, parent) {

    open val url get() = uri as HttpURL

    override fun isValid() = resolvedUri is HttpURL

    override val targetIdentifier: String by lazy { "${method.value} ${url.toNormalizedUriString()}" }

    override fun equals(other: Any?): Boolean =
        if (other is HttpTarget) {
            // Fast check (hashCode)
            if (this.hashCode() != other.hashCode()) {
                false
            } else {
                // Slow comparison
                targetIdentifier == other.targetIdentifier
            }
        } else {
            false
        }

    override fun toString(): String = url.toResolvedUriString()

}