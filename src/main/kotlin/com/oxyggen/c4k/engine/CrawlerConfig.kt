package com.oxyggen.c4k.engine

data class CrawlerConfig(
    val politenessDelay: Int = 200,
    val maxDepth: Int = -1
)