package com.oxyggen.c4k.config

open class HttpConfig : CrawlConfig {

    companion object {
        const val POLITENESS_DELAY = "HTTP.politenessDelay"
        const val MAX_DEPTH = "HTTP.maxDepth"
    }

    private val values: MutableMap<String, Any> = mutableMapOf()

    var politenessDelay: Int = 200
    var maxDepth: Int = -1

    override fun get(key: String): Any? = when (key) {
        POLITENESS_DELAY -> politenessDelay
        MAX_DEPTH -> maxDepth
        else ->
            values[key]
    }

    override fun containsKey(key: String): Boolean = get(key) != null

    override fun getOrDefault(key: String, defaultValue: Any): Any? = values[key] ?: defaultValue
}
