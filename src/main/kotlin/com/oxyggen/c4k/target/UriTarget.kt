package com.oxyggen.c4k.target

import com.oxyggen.net.ContextURI
import com.oxyggen.net.ResolvedURI
import com.oxyggen.net.URI

open class UriTarget(urlString: String, parent: Target? = null) : Target(parent) {

    protected val resolvedUri: ResolvedURI

    /**
     * Initialization
     **/
    init {
        val parentUri = if (parent is UriTarget) parent.uri else null
        val contextUri = if (parentUri is ContextURI) parentUri else null

        val parsedUri = URI.parse(urlString, contextUri)

        resolvedUri = if (parsedUri is ResolvedURI) {
            parsedUri
        } else {
            throw IllegalArgumentException("UriTarget: can't resolve ${urlString} into ResolvedURI")
        }
    }

    /**
     * The URI (no backing property)
     **/
    open val uri get():ResolvedURI = resolvedUri

    /**
     * Target identifier should be the same for 2 targets pointing to the same
     * resource (so http://www.test.com/abc = http://www.test.com/def/../abc
     **/
    open val targetIdentifier: String by lazy { uri.toString() }

    /**
     * Hash code
     **/
    protected open val hashCode: Int by lazy { targetIdentifier.hashCode() }

    override fun getQueueId(): String = ""

    override fun hashCode(): Int = hashCode

    override fun toString() = uri.toString()

    override fun equals(other: Any?): Boolean =
        if (other is UriTarget) {
            // Fast check (hashCode)
            if (this.hashCode() != other.hashCode()) {
                false
            } else {
                // Slow comparison
                uri == other.uri
            }
        } else {
            false
        }
}