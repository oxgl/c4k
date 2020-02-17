package com.oxyggen.c4k.target

abstract class Target(val parent: Target? = null) {

    enum class Status { CREATED, ANALYZING, FINISHED, CANCELLED }

    val depth: Int

    var status: Status = Status.CREATED

    init {
        depth = if (parent != null) {
            parent.depth + 1
        } else {
            0
        }
    }

    abstract fun getQueueId(): String

}
