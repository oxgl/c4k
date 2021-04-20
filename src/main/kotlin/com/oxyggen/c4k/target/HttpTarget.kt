package com.oxyggen.c4k.target

import com.oxyggen.c4k.persistency.Target
import com.oxyggen.net.HttpURL
import io.ktor.http.HttpMethod

open class HttpTarget(urlString: String, val method: HttpMethod = HttpMethod.Get, parent: Target? = null) :
    UriTarget(urlString, parent) {

    init {
        if (uri !is HttpURL) {
            throw IllegalArgumentException("HttpTarget: can't resolve ${urlString} into HttpURL")
        }
    }

    open val url get() = uri as HttpURL

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

    override fun getQueueId() = "${url.scheme}://${url.host}"

}