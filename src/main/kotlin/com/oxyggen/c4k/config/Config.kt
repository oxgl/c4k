package com.oxyggen.c4k.config

interface Config {
    val keys: Set<String>
    fun containsKey(key: String): Boolean
    operator fun get(key: String): Any?
    fun getOrDefault(key: String, defaultValue: Any): Any?
    fun mergeValuesFrom(config: Config): Config
}
