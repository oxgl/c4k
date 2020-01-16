package com.oxyggen.c4k.target

abstract class Target(val parent: Target? = null) {

    val depth: Int

    init {
        if (parent != null) {
            depth = parent.depth + 1
        } else {
            depth = 0
        }
    }

    abstract fun getQueueId(): String

}
