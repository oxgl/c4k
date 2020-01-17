package com.oxyggen.c4k.qa.robots

class HttpRobotsTxt(val content: String) {

    enum class RuleType { ALLOW, DISALLOW }

    data class Rule(val type: RuleType, val pathPattern: String)

    data class Group(val userAgent: String, val rules: List<Rule>)

    val groups: List<Group>

    init {
        groups = listOf()
    }





}