package com.oxyggen.c4k.engine

import com.oxyggen.c4k.target.CrawlTarget

data class CrawlEvent(val type: Type, val target: CrawlTarget? = null) {
    enum class Type { REROUTE, ABORT}
}