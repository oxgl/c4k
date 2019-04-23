package com.oxyggen.c4k.target

class HttpUrlCrawlTarget(url: String) : UrlCrawlTarget(url) {

    init {

    }

    fun getNormalizedUrl(withScheme: Boolean = false): String {
        var result = ""

        if (withScheme) {
            if (scheme == "http")
                result += "https"
            else
                result += scheme

            result += "://"
        }

        if (user.isNotBlank()) result += "$user@"

        result += host

        if (port > 0) result += ":$port"

        if (path.isNotBlank()) result += path

        if (query.isNotBlank()) result += "?$query"

        if (ref.isNotBlank()) result += "#$ref"

        return result;
    }

    override fun equals(other: Any?): Boolean {
        if (other is HttpUrlCrawlTarget) {
            return this.getNormalizedUrl() == other.getNormalizedUrl()
        } else {
            return super.equals(other);
        }
    }

    override fun hashCode(): Int {
        return getNormalizedUrl().hashCode();
    }

}