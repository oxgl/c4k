package com.oxyggen.c4k.target

abstract class CrawlTarget(val parent: CrawlTarget? = null) {

    val depth: Int

    init {
        if (parent != null) {
            depth = parent.depth + 1
        } else {
            depth = 0
        }
    }

    /********************************************************************************
     * @return true if target is valid
     ********************************************************************************/
    abstract fun isValid(): Boolean

}
