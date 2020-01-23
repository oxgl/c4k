package com.oxyggen.c4k.config

open class HttpConfig(config: Config? = null) : GenericConfig(config) {

    companion object {
        const val POLITENESS_DELAY = "HTTP.politenessDelay"
        const val MAX_DEPTH = "HTTP.maxDepth"
        const val USER_AGENT = "HTTP.userAgent"
        const val CONNECTION_TIMEOUT = "HTTP.connectionTimeout"
    }

    init {
        values.putIfAbsent(POLITENESS_DELAY, 200)
        values.putIfAbsent(MAX_DEPTH, -1)
        values.putIfAbsent(CONNECTION_TIMEOUT, 5000)
        values.putIfAbsent(USER_AGENT, "c4k (Oxyggen Kotlin crawler library)")
    }

    val politenessDelay
        get() = values[POLITENESS_DELAY] as Int

    val maxDepth
        get() = values[MAX_DEPTH] as Int

    val connectionTimeout
        get() = values[CONNECTION_TIMEOUT] as Int

    val userAgent
        get() = values[USER_AGENT] as String

    override fun getObjectCopy(): GenericConfig = HttpConfig(this)

}
