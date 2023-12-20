package dev.morj.adv23.day20

object Main2 {

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

        println(broadcast)
        gates.values.forEach { println(it) }

        // val flops = mutableMapOf<String, Flop>()
        val conjs = mutableMapOf<String, Conj>()
        gates.forEach { (k, v) ->
            if (!v.type) {
                val inputs = gates.values.filter { it.targets.contains(v.name) }.map { it.name }
                conjs[k] = Conj(v.name, if (broadcast.contains(v.name)) (inputs + broadcast) else inputs)
            } else {
                // flops[k] = Flop(v.name, gates.values.first { it.targets.contains(v.name) }.name)
            }
        }

        // flops.forEach { println(it) }
        conjs.forEach { println(it) }

        val currentFlops = mutableSetOf<String>()
        val currentConjInputs = mutableMapOf<String, MutableMap<String, Pulse>>()
        conjs.forEach { (k, c) ->
            currentConjInputs[k] = c.inputs.associateWith { Pulse.LOW }.toMutableMap()
        }
        var highs = 0L
        var lows = 0L
        repeat(1000) {
            lows++
            // println("button -low-> broadcaster")
            val queue = ArrayDeque<Signal>()
            broadcast.forEach {
                lows++
                queue.add(Signal("broadcaster", it, Pulse.LOW))
            }
            while (queue.isNotEmpty()) {
                val (from, name, pulse) = queue.removeFirst()
                // println("$from -${pulse.id}-> $name")
                if (name == "rx" && pulse == Pulse.LOW) {
                    break
                }
                gates[name]?.let { gate ->
                    if (gate.type) {
                        if (pulse == Pulse.LOW) {
                            if (!currentFlops.contains(name)) {
                                gate.targets.forEach { target ->
                                    highs++
                                    queue.add(Signal(name, target, Pulse.HIGH))
                                }
                                currentFlops.add(name)
                            } else {
                                currentFlops.remove(name)
                                gate.targets.forEach { target ->
                                    lows++
                                    queue.add(Signal(name, target, Pulse.LOW))
                                }
                            }
                        }
                    } else {
                        val inputs = currentConjInputs[name]!!
                        inputs[from] = pulse
                        if (inputs.values.all { it == Pulse.HIGH }) {
                            gate.targets.forEach {
                                lows++
                                queue.add(Signal(name, it, Pulse.LOW))
                            }
                        } else {
                            gate.targets.forEach {
                                highs++
                                queue.add(Signal(name, it, Pulse.HIGH))
                            }
                        }
                    }
                }
            }
            // println("=============")
        }
        println("lows $lows, highs: $highs")
        println("result ${lows * highs}") // 807739436 too low
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
}
