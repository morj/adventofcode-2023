package dev.morj.adv23.day19

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        var pp = false
        val workflows = mutableMapOf<String, Workflow>()
        val allParts = mutableListOf<Part>()
        consumeInput { _, line ->
            if (line.isEmpty()) {
                pp = true
            } else if (pp) {
                allParts.add(parsePart(line))
            } else {
                val wf = parseWorkflow(line)
                workflows[wf.id] = wf
            }
        }
        part1(allParts, workflows)
    }

    private fun part1(allParts: MutableList<Part>, workflows: MutableMap<String, Workflow>) {
        // debug(workflows.values, allParts)
        val accepted = mutableSetOf<Part>()
        // val initial = mutableListOf(allParts.first())
        applyAll(mutableMapOf("in" to allParts), workflows, accepted)
        var result = 0L
        accepted.forEach {
            it.data.forEach { (_, value) -> result += value }
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
                parts.forEach { part ->
                    for (rule in wf.rules) {
                        val action = rule.test(part)
                        if (action != null) {
                            println(action)
                            when (action) {
                                Action.A -> accepted.add(part)
                                Action.R -> Unit // ignore part
                                is Action.Send -> newState[action.targetId]?.add(part) ?: run {
                                    newState[action.targetId] = mutableListOf(part)
                                }
                            }
                            break
                        }
                    }
                }
            }
            state = newState
        }
    }

    private fun parsePart(line: String): Part {
        val data = hashMapOf<Char, Int>()
        val tokens = line.substring(1, line.length - 1).split(',')
        tokens.forEach {
            data[it[0]] = it.substring(2).toInt()
        }
        return Part(data)
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

    data class Part(val data: Map<Char, Int>)

    class Workflow(val id: String, val rules: List<Rule>)

    sealed class Action {
        data object A : Action()
        data object R : Action()
        data class Send(val targetId: String) : Action()
    }

    private fun debug(workflows: Collection<Workflow>, parts: Collection<Part>) {
        workflows.forEach {
            print(it.id + ": ")
            println(it.rules.joinToString())
        }
        parts.forEach {
            println(it)
        }
    }

    data class Rule(val target: Char?, val sign: Boolean, val limit: Int, val action: Action) {
        fun test(part: Part): Action? {
            return if (target == null) {
                action
            } else {
                val value = part.data[target]!!
                if (sign) {
                    if (value > limit) {
                        action
                    } else {
                        null
                    }
                } else if (value < limit) {
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
