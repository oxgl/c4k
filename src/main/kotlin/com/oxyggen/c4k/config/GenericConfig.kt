package com.oxyggen.c4k.config

open class GenericConfig(config: Config? = null) : Config {

    protected val values: MutableMap<String, Any>

    init {
        values = if (config != null) {
            val result: MutableMap<String, Any> = mutableMapOf()
            config.keys.forEach {
                val v = config[it]
                if (v != null) result[it] = v
            }
            result
        } else {
            mutableMapOf()
        }
    }

    protected open fun getObjectCopy(): GenericConfig = GenericConfig(this)

    override fun get(key: String): Any? = values[key]

    override val keys: Set<String>
        get() = values.keys

    override fun containsKey(key: String): Boolean = get(key) != null

    override fun getOrDefault(key: String, defaultValue: Any): Any? = values[key] ?: defaultValue

    override fun mergeValuesFrom(config: Config): Config {
        val newConfig = getObjectCopy()
        config.keys.forEach {
            val value = config[it]
            if (value != null) {
                newConfig.values[it] = value
            }
        }
        return newConfig
    }
}
