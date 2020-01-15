package com.oxyggen.c4k.old.group

import com.oxyggen.c4k.target.CrawlTarget
import kotlin.reflect.KClass

open class CrawlGroup(val targetClass: KClass<out CrawlTarget>, val id: String) {
    override fun toString(): String {
        return "$targetClass:  $id"
    }
}