package dev.morj.adv23.day08

import java.util.*


object Main2 {
    @JvmStatic
    fun main(args: Array<String>) {
        val ids = mutableMapOf<String, Int>()
        val rev = mutableMapOf<Int, String>()
        var header = ""
        consumeInput { index, line ->
            if (index == 0) {
                header = line
            } else if (index > 1) {
                val text = line.substring(0, 3)
                ids[text] = index - 2
                rev[index - 2] = text
            }
        }
        val directions = BooleanArray(header.length) { header[it] == 'R' }
        val sources = ids.mapNotNull { (k, v) -> v.takeIf { k.endsWith('A') } }.toIntArray()
        val targets = ids.mapNotNull { (k, v) -> v.takeIf { k.endsWith('Z') } }.toIntArray()
        val network = IntArray(ids.size * 2)
        consumeInput { index, line ->
            if (index > 1) {
                val l = line.substring(7, 10)
                val r = line.substring(12, 15)
                val id = index - 2
                network[id.l] = ids[l]!!
                network[id.r] = ids[r]!!
            }
        }
        val periods = arrayListOf<Long>()
        val set = targets.toMutableSet()
        sources.forEach { source ->
            var current = source
            var distance = 0L
            val mod = directions.size
            while (true) {
                val offset = (distance % mod).toInt()
                val dir = directions[offset]
                current = if (dir) network[current.r] else network[current.l]
                distance++
                if (set.contains(current)) {
                    periods.add(distance)
                    println("from ${rev[source]} to ${rev[current]} in $distance")
                    break
                }
            }
            println("cycle: $distance")
        }
        println(lcm(periods.toLongArray()))
    }

    private fun gcd(x: Long, y: Long): Long {
        return if (y == 0L) x else gcd(y, x % y)
    }

    private fun gcd(numbers: LongArray): Long {
        return numbers.asSequence().fold(0) { x, y -> gcd(x, y) }
    }

    private fun lcm(numbers: LongArray): Long {
        return numbers.asSequence().fold(1) { x, y -> x * (y / gcd(x, y)) }
    }

    private fun tooSlow(
        sources: IntArray,
        targets: IntArray,
        directions: BooleanArray,
        network: IntArray
    ): Long {
        var distance = 0L
        val fullMatch = sources.size
        val t0 = targets[0]
        val t1 = if (fullMatch > 1) targets[1] else 0
        val t2 = if (fullMatch > 2) targets[2] else 0
        val t3 = if (fullMatch > 3) targets[3] else 0
        val t4 = if (fullMatch > 4) targets[4] else 0
        val t5 = if (fullMatch > 5) targets[5] else 0
        val mod = directions.size
        while (true) {
            val offset = directions[(distance % mod).toInt()]
            var matches = 0
            sources.forEachIndexed { index, current ->
                val updated = if (offset) network[current.r] else network[current.l]
                sources[index] = updated
                if (updated == t0 || updated == t1 || updated == t2 || updated == t3 || updated == t4 || updated == t5) {
                    matches++
                }
            }
            distance++
            if (distance % 1000000000 == 0L) {
                println("at ${Date()} we have $distance")
            }
            if (matches == fullMatch) {
                break
            }
        }
        return distance
    }

    private val Int.l get() = 2 * this
    private val Int.r get() = 2 * this + 1

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-08.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
