package dev.morj.adv23.day21

object Main {
    private const val MAX_STEPS = 64

    @JvmStatic
    fun main(args: Array<String>) {
        val grid = mutableListOf<String>()
        var width = 0
        var startX = 0
        var startY = 0
        consumeInput { x, line ->
            grid.add(line)
            width = width.coerceAtLeast(line.length)
            line.forEachIndexed { y, c ->
                if (c == 'S') {
                    startX = x
                    startY = y
                }
            }
        }
        val queue = ArrayDeque<FullPosition>()
        val visited = mutableSetOf<FullPosition>()
        queue.add(FullPosition(startX, startY, 0))
        while (queue.isNotEmpty()) {
            val (x, y, depth) = queue.removeFirst()
            neighbors(grid, x, y, width).forEach {
                val position = FullPosition(it.x, it.y, depth + 1)
                if (!visited.contains(position) && position.depth <= MAX_STEPS) {
                    visited.add(position)
                    queue.add(position)
                }
            }
        }
        val pairs = visited.filter { it.depth == MAX_STEPS }.map { it.x to it.y }.toSet()
        grid.forEachIndexed { x, line ->
            line.forEachIndexed { y, c ->
                if (pairs.contains(x to y)) {
                    print('O')
                } else {
                    print(c)
                }
            }
            println()
        }
        println("result: ${pairs.size}")
    }

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

        return result.filter {grid[it.x][it.y] != '#'}
    }

    data class Position(val x: Int, val y: Int)
    data class FullPosition(val x: Int, val y: Int, val depth: Int)

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-21.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
