package dev.morj.adv23.day19

object Main2 {

    @JvmStatic
    fun main(args: Array<String>) {
        var pp = false
        val workflows = mutableMapOf<String, Workflow>()
        consumeInput { _, line ->
            if (line.isEmpty()) {
                pp = true
            } else if (!pp) {
                val wf = parseWorkflow(line)
                workflows[wf.id] = wf
            }
        }
        val accepted = mutableSetOf<Part>()
        val initialPart = Part(
            mutableMapOf(
                'x' to (1 to 4000),
                'm' to (1 to 4000),
                'a' to (1 to 4000),
                's' to (1 to 4000),
            )
        )
        applyAll(mutableMapOf("in" to mutableListOf(initialPart)), workflows, accepted)
        var result = 0L
        accepted.forEach {
            var mul = 1L
            it.data.forEach { (_, value) -> mul *= (value.second - value.first + 1) }
            result += mul
            println(it)
        }
        println("result $result")
    }

    private fun applyAll(
        initial: MutableMap<String, MutableList<Part>>,
        workflows: MutableMap<String, Workflow>,
        accepted: MutableSet<Part>
    ) {
        var state = initial
        while (state.isNotEmpty()) {
            val newState = mutableMapOf<String, MutableList<Part>>()
            state.forEach { (id, parts) ->
                val wf = workflows[id]!!
                parts.forEach { maybePart ->
                    var part = maybePart
                    for (rule in wf.rules) {
                        var split: Part? = null
                        if (rule.target != null) {
                            val (from, to) = part.data[rule.target]!!
                            if (rule.sign) {
                                if (rule.limit in (from..<to)) {
                                    val d1 = part.data.toMutableMap()
                                    d1[rule.target] = (rule.limit + 1 to to)
                                    split = Part(d1)
                                    val d2 = part.data.toMutableMap()
                                    d2[rule.target] = (from to rule.limit)
                                    part = Part(d2)
                                }
                            } else {
                                if (rule.limit in (from + 1)..to) {
                                    val d = part.data.toMutableMap()
                                    d[rule.target] = (from to rule.limit - 1)
                                    split = Part(d)
                                    val d2 = part.data.toMutableMap()
                                    d2[rule.target] = (rule.limit to to)
                                    part = Part(d2)
                                }
                            }
                        }
                        val action1 = rule.test(part)
                        val action2 = split?.let {
                            requireNotNull(rule.test(it)) // must match by definition
                        }
                        if (action2 != null) {
                            when (action2) {
                                Action.A -> accepted.add(split!!)
                                Action.R -> Unit // ignore part
                                is Action.Send -> send(newState, action2.targetId, split!!)
                            }
                        }
                        if (action1 != null) {
                            when (action1) {
                                Action.A -> accepted.add(part)
                                Action.R -> Unit // ignore part
                                is Action.Send -> send(newState, action1.targetId, part)
                            }
                            break // nothing remains
                        }
                    }
                }
            }
            state = newState
        }
    }

    private fun send(newState: MutableMap<String, MutableList<Part>>, target: String, part: Part) {
        newState[target]?.add(part) ?: run {
            newState[target] = mutableListOf(part)
        }
    }

    private fun parseWorkflow(line: String): Workflow {
        val opening = line.indexOf('{')
        val id = line.substring(0, opening)
        val tokens = line.substring(opening + 1, line.length - 1).split(',')
        return Workflow(id, tokens.map { parseRule(it) })
    }

    private fun parseRule(text: String): Rule {
        return if (text.contains(':')) {
            val tokens = text.split(':')
            val filter = tokens[0]
            val sign = filter[1] == '>'
            Rule(filter[0], sign, filter.substring(2).toInt(), parseAction(tokens[1]))
        } else {
            Rule(null, false, 0, parseAction(text))
        }
    }

    private fun parseAction(text: String): Action {
        return when (text) {
            "A" -> Action.A
            "R" -> Action.R
            else -> Action.Send(text)
        }
    }

    data class Part(val data: Map<Char, Pair<Int, Int>>)

    class Workflow(val id: String, val rules: List<Rule>)

    sealed class Action {
        data object A : Action()
        data object R : Action()
        data class Send(val targetId: String) : Action()
    }

    data class Rule(val target: Char?, val sign: Boolean, val limit: Int, val action: Action) {
        fun test(part: Part): Action? {
            return if (target == null) {
                action
            } else {
                val (from, to) = part.data[target]!!
                if (sign) {
                    if (from > limit) {
                        action
                    } else {
                        null
                    }
                } else if (to < limit) {
                    action
                } else {
                    null
                }
            }
        }
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-19.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
