package com.oxyggen.c4k.qa.robots

class HttpRobotsTxt(val content: String) {

    companion object {
        const val PARAM_USER_AGENT = "user-agent"
        const val PARAM_RULE_ALLOW = "allow"
        const val PARAM_RULE_DISALLOW = "disallow"
        const val PARAM_SITEMAP = "sitemap"
        const val PARAM_CRAWL_DELAY = "crawl-delay"
    }

    enum class RuleType { ALLOW, DISALLOW }

    data class Rule(val type: RuleType, val pathPattern: String)

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

        fun toGroup(): Group = Group(userAgents.toList(), rules.toList(), params.toMap())

    }

    val groups: List<Group>

    init {
        groups = parseContent(content)
    }

    private fun parseContent(content: String): List<Group> {
        val groups: MutableList<Group> = mutableListOf()
        val lines = content.replace("\r", "").split('\n')

        val gb = GroupBuilder()
        lines.forEach {
            if (!it.trim().startsWith("#")) {
                val param = it.substringBefore(':').trim().toLowerCase()
                val value = it.substringAfter(':', "").trim()
                when (param) {
                    PARAM_USER_AGENT -> {
                        if (!gb.isUserAgentSectionOpened) {
                            if (gb.isValid) groups.add(gb.toGroup())
                            gb.clear()
                        }
                        gb.userAgents.add(value)
                    }

                    PARAM_RULE_ALLOW -> if (gb.isUserAgentSectionFilled) gb.rules.add(Rule(RuleType.ALLOW, value))

                    PARAM_RULE_DISALLOW -> if (gb.isUserAgentSectionFilled) gb.rules.add(Rule(RuleType.DISALLOW, value))

                    else -> if (param.isNotBlank() && gb.isUserAgentSectionFilled) gb.params[param.trim().toLowerCase()] =
                        value.trim()
                }
            }
        }
        if (gb.isValid) groups.add(gb.toGroup())
        return groups
    }


}