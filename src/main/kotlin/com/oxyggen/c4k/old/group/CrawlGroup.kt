package com.oxyggen.c4k.old.group

import com.oxyggen.c4k.persistency.Target
import kotlin.reflect.KClass

open class CrawlGroup(val targetClass: KClass<out Target>, val id: String) {
    override fun toString(): String {
        return "$targetClass:  $id"
    }
}