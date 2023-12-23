package dev.morj.adv23.day23

import java.util.concurrent.atomic.AtomicInteger

object Main2 {
    @JvmStatic
    fun main(args: Array<String>) {
        val grid = mutableListOf<String>()
        var width = 0
        val junctions = mutableSetOf<Position>()
        consumeInput { _, line ->
            grid.add(line)
            width = width.coerceAtLeast(line.length)
        }
        val ultimateStart = Position(0, grid[0].indexOf('.'))
        junctions.add(ultimateStart)
        val ultimateEnd = Position(grid.size - 1, grid[grid.size - 1].indexOf('.'))
        junctions.add(ultimateEnd)
        grid.forEachIndexed { x, line ->
            line.forEachIndexed { y, c ->
                if (c != '#') {
                    val junction = neighbors(grid, x, y, width).count { n ->
                        grid[n.x][n.y] != '#'
                    } > 2
                    if (junction) {
                        junctions.add(Position(x, y))
                    }
                }
            }
        }
        // junctions.forEach { println(it) }
        println("total junctions: ${junctions.size}")
        val edges = mutableMapOf<Position, MutableList<Pair<Position, Int>>>()
        junctions.forEach { start ->
            val dirs = neighbors(grid, start.x, start.y, width)
            dirs.forEach { dir ->
                var current: Position? = dir
                var length = 1
                var prev = start
                while (current != null && !junctions.contains(current)) {
                    length++
                    val next = neighbors(grid, current.x, current.y, width).filter { it != prev }
                    require(next.size <= 1)
                    prev = current
                    current = next.firstOrNull()
                }
                if (current != null) {
                    require(current != start) { "loop" }
                    val entry = current to length
                    edges[start]?.add(entry) ?: run { edges[start] = mutableListOf(entry) }
                    // println("from $start to $current is $length")
                }
            }
        }
        var totalEdges = 0
        edges.forEach { e ->
            totalEdges += e.value.size
            println(e.value.joinToString(prefix = "${e.key}: ") { "${it.first}@${it.second}" })
        }
        println("total edges: $totalEdges")
        val result = AtomicInteger()
        dfs(ultimateStart, ultimateEnd, 0, 0, result, listOf(), edges)
        println("result: ${result.get()}")
    }

    private fun dfs(
        start: Position,
        end: Position,
        depth: Int,
        currentWeight: Int,
        out: AtomicInteger,
        path: List<Position>,
        edges: MutableMap<Position, MutableList<Pair<Position, Int>>>
    ) {
        if (depth > 555) return
        if (start == end) {
            if (currentWeight > out.get()) {
                out.set(currentWeight)
            }
        } else {
            edges[start]?.forEach { (p, weight) ->
                if (!path.contains(p)) {
                    dfs(p, end, depth + 1, currentWeight + weight, out, path + p, edges)
                }
            }
        }
    }

    data class Position(val x: Int, val y: Int)

    private fun neighbors(grid: MutableList<String>, x: Int, y: Int, width: Int): List<Position> {
        val height = grid.size
        val result = mutableListOf<Position>()
        if (x > 0) {
            result.add(Position(x - 1, y))
        }
        if (y > 0) {
            result.add(Position(x, y - 1))
        }
        if (x + 1 < height) {
            result.add(Position(x + 1, y))
        }
        if (y + 1 < width) {
            result.add(Position(x, y + 1))
        }

        return result.filter { grid[it.x][it.y] != '#' }
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-23.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
