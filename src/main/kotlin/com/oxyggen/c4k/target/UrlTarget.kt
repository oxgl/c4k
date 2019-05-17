package com.oxyggen.c4k.target

import java.net.URI

open class UrlTarget(urlString: String, parent: CrawlTarget? = null) : CrawlTarget(parent) {

    protected val uri: URI?

    open val scheme get() = uri?.scheme ?: ""
    open val user get() = uri?.userInfo ?: ""
    open val host get() = uri?.host ?: ""
    open val port get() = uri?.port ?: -1
    open val path get() = uri?.path ?: ""
    open val query get() = uri?.query ?: ""
    open val fragment get() = uri?.fragment ?: ""
    open val valid get() = uri != null
    open val targetIdentifier get() = getUrlString(false)

    init {
        uri = try {
            //TODO: fix the url encoding/decoding nightmare


            val thisUri = URI.create(urlString)
            if (thisUri.isAbsolute) {
                thisUri
            } else {
                if (parent is UrlTarget) {
                    URI.create(parent.getUrlString()).resolve(thisUri)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    protected open val hashCode: Int by lazy {
        targetIdentifier.hashCode()
    }


    /*
     * Get URL string with or without scheme
     */
    open fun getUrlString(withScheme: Boolean = true): String {
        var result = ""

        if (withScheme)
            result += "${scheme}://"

        if (user.isNotBlank()) result += "${user}@"
        if (host.isNotBlank()) result += host
        if (port > 0) result += ":$port"
        if (path.isNotBlank()) result += path
        if (query.isNotBlank()) result += "?$query"
        if (fragment.isNotBlank()) result += "#$fragment"


        return result
    }

    /*
     * Get target identifier -> same ID means same target!
     */
    //open fun getTargetIdentifier(): String = getUrlString(false)


    override fun hashCode() = hashCode

    override fun toString() = targetIdentifier


    override fun equals(other: Any?): Boolean =
        if (other is UrlTarget) {
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