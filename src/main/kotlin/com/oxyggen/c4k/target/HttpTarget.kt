package com.oxyggen.c4k.target

class HttpTarget(url: String) : UrlTarget(url) {

    protected override val hashCode: Int by lazy {
        getComparableUrl(false).hashCode()
    }

    override fun getComparableUrl(withScheme: Boolean): String {
        var result = super.getComparableUrl(false);
        if (withScheme) {
            if (scheme == "http")
                result = "https://$result"
            else
                result = "$scheme://$result"
        }
        return result;
    }

    override fun equals(other: Any?): Boolean {
        if (other is UrlTarget) {
            // Fast check (hashCode)
            if (this.hashCode() != other.hashCode())
                return false;

            // Slow comparison
            return getComparableUrl(false) == other.getComparableUrl(false);
        } else {
            return false
        }
    }

}