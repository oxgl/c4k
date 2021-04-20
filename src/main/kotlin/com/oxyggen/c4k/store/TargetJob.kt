package com.oxyggen.c4k.store

import com.oxyggen.c4k.persistency.Target

abstract class TargetJob(val target: Target) {
    abstract fun isCompleted(): Boolean
    abstract fun isCancelled(): Boolean
}