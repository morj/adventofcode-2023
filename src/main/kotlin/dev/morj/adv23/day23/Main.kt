package dev.morj.adv23.day23

object Main {
    private val slopeChars = setOf('^', '>', 'v', '<')
    private val walkable = setOf('.', '^', '>', 'v', '<')

    @JvmStatic
    fun main(args: Array<String>) {
        val grid = mutableListOf<String>()
        var width = 0
        val slopes = mutableSetOf<Position>()
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
                if (walkable.contains(c)) {
                    if (slopeChars.contains(c)) {
                        slopes.add(Position(x, y))
                    }
                    val junction = neighbors(grid, x, y, width).count { n ->
                        walkable.contains(grid[n.x][n.y])
                    } > 2
                    if (junction) {
                        junctions.add(Position(x, y))
                    }
                }
            }
        }
        // slopes.forEach { println(it) }
        println("total slopes: ${slopes.size}")
        // junctions.forEach { println(it) }
        println("total junctions: ${junctions.size}")
        require(junctions.none { slopes.contains(it) })
        val edges = mutableMapOf<Position, MutableList<Pair<Position, Int>>>()
        junctions.forEach { start ->
            val dirs = next(grid, start.x, start.y, width)
            dirs.forEach { dir ->
                var current: Position? = dir
                var length = 1
                var prev = start
                while (current != null && !junctions.contains(current)) {
                    length++
                    val next = next(grid, current.x, current.y, width).filter { it != prev }
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
        edges.forEach { e ->
            println(e.value.joinToString(prefix = "${e.key}: ") { "${it.first}@${it.second}" })
        }
        val topTmp = mutableSetOf<Position>()
        dfs(ultimateStart, topTmp, listOf(), edges)
        val top = topTmp.reversed()
        println(top.joinToString(prefix = "topological sort: "))
        require(junctions.size == top.size)
        val lengthTo = mutableMapOf<Position, Int>()
        top.forEach { from ->
            edges[from]?.forEach { (to, weight) ->
                val upd = (lengthTo[from] ?: 0) + weight
                if ((lengthTo[to] ?: 0) <= upd) {
                    lengthTo[to] = upd
                }
            }
        }
        lengthTo.forEach { (k, v) ->
            println("$k: $v")
        }
    }

    private fun dfs(
        start: Position,
        result: MutableSet<Position>,
        path: List<Position>,
        edges: MutableMap<Position, MutableList<Pair<Position, Int>>>
    ) {
        edges[start]?.forEach {
            val p = it.first
            require(!path.contains(p)) { "not a dag" }
            if (!result.contains(p)) {
                dfs(p, result, path + p, edges)
            }
        }
        result.add(start)
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

    private fun next(grid: MutableList<String>, x: Int, y: Int, width: Int): List<Position> {
        val height = grid.size
        val result = mutableListOf<Position>()
        if (x > 0 && grid[x - 1][y] != 'v') {
            result.add(Position(x - 1, y))
        }
        if (y > 0 && grid[x][y - 1] != '>') {
            result.add(Position(x, y - 1))
        }
        if (x + 1 < height && grid[x + 1][y] != '^') {
            result.add(Position(x + 1, y))
        }
        if (y + 1 < width && grid[x][y + 1] != '<') {
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
