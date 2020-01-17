package com.oxyggen.c4k.config

open class HttpConfig(config: Config? = null) : GenericConfig(config) {

    companion object {
        const val POLITENESS_DELAY = "HTTP.politenessDelay"
        const val MAX_DEPTH = "HTTP.maxDepth"
    }

    init {
        if (!values.containsKey(POLITENESS_DELAY)) values.set(POLITENESS_DELAY, 200)
        if (!values.containsKey(MAX_DEPTH)) values.set(POLITENESS_DELAY, -1)
    }

    override fun getObjectCopy(): GenericConfig = HttpConfig(this)

}
