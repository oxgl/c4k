package com.oxyggen.c4k.target

import io.ktor.http.HttpMethod

open class HttpTarget(urlString: String, val method: HttpMethod = HttpMethod.Get, parent: CrawlTarget? = null) :
    UrlTarget(urlString, parent) {

    protected override val hashCode: Int by lazy {
        targetIdentifier.hashCode()
    }

    override val targetIdentifier: String
        get() = "${method.value} ${super.targetIdentifier}"

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

}