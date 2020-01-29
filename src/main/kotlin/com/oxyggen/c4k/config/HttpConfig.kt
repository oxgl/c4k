package com.oxyggen.c4k.config

open class HttpConfig(config: Config? = null) : GenericConfig(config) {

    companion object {
        const val POLITENESS_DELAY = "HTTP.politenessDelay"
        const val MAX_DEPTH = "HTTP.maxDepth"
        const val USER_AGENT = "HTTP.userAgent"
        const val CONNECTION_TIMEOUT = "HTTP.connectionTimeout"
    }

    init {
        values.putIfAbsent(POLITENESS_DELAY, 200L)
        values.putIfAbsent(MAX_DEPTH, -1)
        values.putIfAbsent(CONNECTION_TIMEOUT, 5000)
        values.putIfAbsent(USER_AGENT, "c4k (Oxyggen Kotlin crawler library)")
    }

    var politenessDelay: Long
        get() =
            when (val value = values[POLITENESS_DELAY]) {
                is Long -> value
                is Int -> value.toLong()
                is String -> value.toLong()
                else -> 0L
            }
        set(value) {
            values[POLITENESS_DELAY] = value
        }


    var maxDepth: Int
        get() = values[MAX_DEPTH] as Int
        set(value) {
            values[MAX_DEPTH] = value
        }

    var connectionTimeout: Int
        get() = values[CONNECTION_TIMEOUT] as Int
        set(value) {
            values[CONNECTION_TIMEOUT] = value
        }

    var userAgent: String
        get() = values[USER_AGENT] as String
        set(value) {
            values[USER_AGENT] = value
        }

    override fun getObjectCopy(): GenericConfig = HttpConfig(this)
}
