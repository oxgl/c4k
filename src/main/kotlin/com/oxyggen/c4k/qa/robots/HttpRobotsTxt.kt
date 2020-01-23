package com.oxyggen.c4k.qa.robots

import com.oxyggen.matcher.GlobMatcher
import com.oxyggen.matcher.Matcher

class HttpRobotsTxt(val content: String, val userAgent: String) {

    companion object {
        const val PARAM_USER_AGENT = "user-agent"
        const val PARAM_RULE_ALLOW = "allow"
        const val PARAM_RULE_DISALLOW = "disallow"
        const val PARAM_SITEMAP = "sitemap"
        const val PARAM_CRAWL_DELAY = "crawl-delay"
    }

    enum class RuleType { ALLOW, DISALLOW }

    data class Rule(val type: RuleType, val pathPattern: String, val pathMatcher: Matcher)

    data class Group(val userAgents: List<String>, val rules: List<Rule>, val params: Map<String, String>)

    private class GroupBuilder() {
        val userAgents: MutableList<String> = mutableListOf()
        val rules: MutableList<Rule> = mutableListOf()
        val params: MutableMap<String, String> = mutableMapOf()

        val isValid: Boolean
            get() = userAgents.isNotEmpty() && rules.isNotEmpty()

        val isUserAgentSectionOpened: Boolean
            get() = rules.isEmpty() && params.isEmpty()

        val isUserAgentSectionFilled
            get() = userAgents.isNotEmpty()

        fun clear() {
            userAgents.clear()
            rules.clear()
            params.clear()
        }

        fun isValidForAgent(userAgent: String): Boolean {
            if (isValid) {
                userAgents.forEach {
                    val gm = GlobMatcher(it, ignoreCase = true)
                    if (gm.matches(userAgent))
                        return true
                }
            }
            return false
        }

        fun getGroupOrClear(userAgent: String): Group? {
            userAgents.forEach {
                val gm = GlobMatcher(it, ignoreCase = true)
                if (gm.matches(userAgent)) return Group(userAgents.toList(), rules.toList(), params.toMap())
            }
            clear()
            return null
        }

    }

    val group: Group?

    init {
        group = parseContent(content, userAgent)
    }

    private fun pathPatternToGlobMatcher(pathPattern: String): GlobMatcher = GlobMatcher(
        if (pathPattern.endsWith('$')) {
            pathPattern.replace("?", "\\?").dropLast(1)
        } else {
            pathPattern.replace("?", "\\?") + "*"
        }, ignoreCase = false
    )

    private fun parseContent(content: String, userAgent: String): Group? {
        val groups: MutableList<Group> = mutableListOf()

        // FEFF is the Unicode char represented by the UTF-8 byte order mark (EF BB BF)
        // UTF-8	EF BB BF	239 187 191
        val lines = content.removePrefix("\uFEFF").replace("\r", "").split('\n')

        val gb = GroupBuilder()
        lines.forEach lit@{
            val line = it.trim()
            if (line.isNotBlank() && !line.startsWith('#')) {
                val param = line.substringBefore(':').trim().toLowerCase()
                val value = line.substringAfter(':', "").trim()
                when (param) {
                    PARAM_USER_AGENT -> {
                        if (!gb.isUserAgentSectionOpened) {
                            // User agent section is already closed,
                            // so this is the start of new group
                            val group = gb.getGroupOrClear(userAgent)
                            if (group != null)
                                return group
                        }
                        gb.userAgents.add(value)
                    }

                    PARAM_RULE_ALLOW -> if (gb.isUserAgentSectionFilled) gb.rules.add(
                        Rule(
                            RuleType.ALLOW,
                            value,
                            pathPatternToGlobMatcher(value)
                        )
                    )

                    PARAM_RULE_DISALLOW -> if (gb.isUserAgentSectionFilled) gb.rules.add(
                        Rule(
                            RuleType.DISALLOW,
                            value,
                            pathPatternToGlobMatcher(value)
                        )
                    )

                    else -> if (param.isNotBlank() && gb.isUserAgentSectionFilled) gb.params[param.trim().toLowerCase()] =
                        value.trim()
                }
            }
        }
        // If gb contains valid group => add to groups
        return gb.getGroupOrClear(userAgent)
    }

    fun isPathAllowed(path: String): Boolean {
        group?.rules?.forEach {
            if (it.pathMatcher.matches(path)) {
                // Rule found => return type
                return it.type == RuleType.ALLOW
            }
        }
        // Disable rule not found => exi
        return true
    }

    override fun toString(): String {
        var result = super.toString()
        if (group != null) {
            if (group.rules.isEmpty()) {
                "[0 rules = ALLOW *]"
            } else {
                result += "[ ${group.rules.size} rules ]"
            }
        } else {
            result += "[0 rules = ALLOW *]"
        }
        return result
    }
}