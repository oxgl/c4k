package com.oxyggen.c4k.config

interface CrawlConfig {
    fun containsKey(key: String): Boolean
    operator fun get(key: String): Any?
    fun getOrDefault(key: String, defaultValue: @UnsafeVariance Any): Any?
}
