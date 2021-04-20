package com.oxyggen.c4k.event

import com.oxyggen.c4k.persistency.Target

data class QueueEvent(val type: Type, val target: Target? = null) {
    enum class Type { ADD_TARGET, REROUTE_TARGET, ABORT }
}