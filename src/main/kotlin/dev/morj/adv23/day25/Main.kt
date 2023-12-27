package dev.morj.adv23.day25

import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val edges = mutableMapOf<String, MutableSet<String>>()
        val allEdgesSet = mutableSetOf<Pair<String, String>>()
        consumeInput { _, line ->
            val key = line.substringBefore(':')
            val tokens = line.substringAfter(": ").split(' ')
            val pairs = tokens.map { key to it } + tokens.map { it to key }
            allEdgesSet.addAll(pairs)
            pairs.forEach { (from, to) ->
                edges[from]?.add(to) ?: run {
                    edges[from] = mutableSetOf(to)
                }
            }
        }
        val totalPaths = mutableMapOf<Pair<String, String>, Int>()
        allEdgesSet.forEach { (from, to) ->
            totalPaths[from to to] = countUniquePaths(from, to, mutableSetOf(), edges)
            // println("===========")
        }
        println("total ${totalPaths.size}")
        val candidateEdges = allEdgesSet.filter { totalPaths[it]!! <= 3 && totalPaths[it.second to it.first]!! <= 3 }
        println("candidates ${candidateEdges.size}")
        // edges.forEach { (k, v) -> println(v.joinToString(prefix = "$k: ")) }
        candidateEdges.forEachIndexed { i1, e1 ->
            println("processing index $i1 of ${candidateEdges.size}")
            candidateEdges.subList(i1, candidateEdges.size).forEach { e2 ->
                val timer = AtomicInteger()
                val tin = mutableMapOf<String, Int>()
                val low = mutableMapOf<String, Int>()
                val visited = mutableSetOf<String>()
                val removed = mutableSetOf(e1, e2)
                edges.keys.forEach { v ->
                    if (!visited.contains(v)) {
                        findBridge(v, timer, removed, tin, low, visited, edges)
                    }
                }
            }
        }
    }

    // approximation
    private fun countUniquePaths(
        start: String,
        finish: String,
        visited: MutableSet<String>,
        edges: MutableMap<String, MutableSet<String>>
    ): Int {
        visited.add(start)
        var sum = 1
        val usedEdges = mutableSetOf<Pair<String, String>>()
        edges[start]!!.forEach { to ->
            if (!visited.contains(to)) {
                val path = findUniquePaths(to, finish, visited, listOf(start, to), usedEdges, edges)
                if (path != null) sum++
            }
        }
        return sum
    }

    private fun findUniquePaths(
        current: String,
        finish: String,
        visited: MutableSet<String>,
        p: List<String>,
        usedEdges: MutableSet<Pair<String, String>>,
        edges: MutableMap<String, MutableSet<String>>
    ): List<String>? {
        if (current == finish && p.size > 1) {
            p.zipWithNext { a, b ->
                usedEdges.add(a to b)
            }
            // println(p.joinToString())
            return p
        }
        visited.add(current)
        edges[current]!!.forEach { to ->
            if (!visited.contains(to) && !usedEdges.contains(current to to)) {
                findUniquePaths(to, finish, visited, p + to, usedEdges, edges)?.let {
                    return it
                }
            }
        }
        return null
    }

    private fun findBridge(
        v: String,
        timer: AtomicInteger,
        removed: MutableSet<Pair<String, String>>,
        timeIn: MutableMap<String, Int>,
        low: MutableMap<String, Int>,
        visited: MutableSet<String>,
        edges: MutableMap<String, MutableSet<String>>,
        p: String? = null
    ): Pair<String, String>? {
        visited.add(v)
        val time = timer.getAndIncrement()
        timeIn[v] = time
        low[v] = time
        for (to in edges[v]!!) {
            if (to == p || removed.contains(v to to) || removed.contains(to to v)) continue
            if (visited.contains(to)) {
                low[v] = min(low[v] ?: 0, timeIn[to] ?: 0)
            } else {
                findBridge(to, timer, removed, timeIn, low, visited, edges, v)?.let {
                    return it
                }
                low[v] = min(low[v] ?: 0, low[to] ?: 0)
                if ((low[to] ?: 0) > (timeIn[v] ?: 0)) {
                    println("if removed [${removed.joinToString { "${it.first}/${it.second}" }}] bridge: $v/$to")
                    return v to to
                }
            }
        }
        return null
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-25.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
