package dev.morj.adv23.day20

object Main3 {

    @JvmStatic
    fun main(args: Array<String>) {
        val broadcast = mutableListOf<String>()
        val gates = mutableMapOf<String, Gate>()
        val names = mutableListOf<String>()
        consumeInput { _, line ->
            val tokens = line.split(" -> ")
            val g = tokens[0]
            if (g == "broadcaster") {
                broadcast.addAll(tokens[1].split(", "))
            } else {
                val type = g[0] == '%'
                val name = g.substring(1, g.length)
                names.add(name)
                gates[name] = Gate(name, type, tokens[1].split(", "))
            }
        }

        val totals = mutableSetOf<Long>()
        broadcast.forEach { bc ->
            val subset = dfs(bc, gates).sorted() + "rx"
            val gatesSubset = mutableMapOf<String, Gate>()
            gates.forEach { (name, gate) ->
                if (subset.contains(name)) {
                    gatesSubset[name] = gate.copy(targets = gate.targets.filter { subset.contains(it) })
                }
            }
            val total = find(gatesSubset, listOf(bc))
            totals.add(total)
            println("finish for $bc")
        }
        println(totals.joinToString())
        println(lcm(totals.toLongArray()))
    }

    private fun find(gates: MutableMap<String, Gate>, broadcast: Collection<String>): Long {
        val conjs = mutableMapOf<String, Conj>()
        gates.forEach { (k, v) ->
            if (!v.type) {
                val inputs = gates.values.filter { it.targets.contains(v.name) }.map { it.name }
                conjs[k] = Conj(v.name, if (broadcast.contains(v.name)) (inputs + broadcast) else inputs)
            }
        }
        gates.forEach { println(it) }
        conjs.forEach { println(it) }
        val currentFlops = mutableSetOf<String>()
        val currentConjInputs = mutableMapOf<String, MutableMap<String, Pulse>>()
        conjs.forEach { (k, c) ->
            currentConjInputs[k] = c.inputs.associateWith { Pulse.LOW }.toMutableMap()
        }
        var totalSteps = 0L
        var desired = Pulse.LOW
        var test = 0
        while (totalSteps < Long.MAX_VALUE) {
            totalSteps++
            val queue = ArrayDeque<Signal>()
            broadcast.forEach {
                queue.add(Signal("broadcaster", it, Pulse.LOW))
            }
            while (queue.isNotEmpty()) {
                val (from, name, pulse) = queue.removeFirst()
                if (name == "rx" && pulse == desired) {
                    desired = if (desired == Pulse.HIGH) Pulse.LOW else Pulse.HIGH
                    println("total $totalSteps")
                    if (test++ >= 1) {
                        return totalSteps
                    }
                }
                gates[name]?.let { gate ->
                    if (gate.type) {
                        if (pulse == Pulse.LOW) {
                            if (!currentFlops.contains(name)) {
                                currentFlops.add(name)
                                Pulse.HIGH
                            } else {
                                currentFlops.remove(name)
                                Pulse.LOW
                            }
                        } else {
                            null
                        }
                    } else {
                        val inputs = currentConjInputs[name]!!
                        inputs[from] = pulse
                        if (inputs.values.all { it == Pulse.HIGH }) Pulse.LOW else Pulse.HIGH
                    }?.let {
                        gate.targets.forEach { target ->
                            queue.add(Signal(name, target, it))
                        }
                    }
                }
            }
        }
        return -1
    }

    private fun dfs(start: String, gates: MutableMap<String, Gate>): MutableSet<String> {
        val all = mutableSetOf<String>()
        dfs(emptyList(), all, mutableSetOf(), start, gates)
        return all
    }

    private fun dfs(
        path: List<Gate>,
        all: MutableSet<String>,
        visited: MutableSet<Pair<Int, Gate>>,
        target: String,
        gates: MutableMap<String, Gate>
    ) {
        val gate = gates[target]
        if (gate == null) {
            // println(path.joinToString { it.name })
        } else {
            val key = path.size to gate
            if (path.size < 60 && !visited.contains(key)) {
                all.add(gate.name)
                visited.add(key)
                gate.targets.forEach {
                    dfs(path + gate, all, visited, it, gates)
                }
            }
        }
    }

    data class Signal(val from: String, val to: String, val pulse: Pulse)

    enum class Pulse(val id: String) { LOW("low"), HIGH("high") }

    data class Gate(val name: String, val type: Boolean, val targets: List<String>)

    data class Conj(val name: String, val inputs: List<String>)

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-20.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }

    private tailrec fun gcd(x: Long, y: Long): Long {
        return if (y == 0L) x else gcd(y, x % y)
    }

    private fun lcm(numbers: LongArray): Long {
        return numbers.asSequence().fold(1) { x, y -> x * (y / gcd(x, y)) }
    }
}
