package com.oxyggen.c4k.event

import com.oxyggen.c4k.persistency.Target

data class EngineEvent(val type: Type, val target: Target? = null) {
    enum class Type { REROUTE, ABORT }
}