package dev.morj.adv23.day17

import java.util.PriorityQueue

object Main {
    private const val LIM = 3

    @JvmStatic
    fun main(args: Array<String>) {
        val data = mutableListOf<List<Int>>()
        consumeInput { _, line ->
            data.add(line.map {
                it.digitToInt()
            })
        }
        data.forEach {
            println(it.joinToString(""))
        }
        findShortestPath(data, 0, 0).forEach {
            println("${it.key} to ${it.value}")
        }
    }

    private fun findShortestPath(data: List<List<Int>>, startX: Int, startY: Int): Map<Node, Int> {
        val start = Node(startX, startY, Dir.R, 1)
        val best = mutableMapOf<Node, Int>()
        val height = data.size
        val width = data[0].size
        best[start] = 0
        val queue = PriorityQueue<Node> { a, b ->
            best[a]!!.compareTo(best[b]!!)
        }
        queue.add(start)
        var found = Int.MAX_VALUE
        while (queue.isNotEmpty()) {
            val node = queue.poll()
            val distance = best[node]!!
            node.next(width, height).forEach { next ->
                val x = next.x
                val y = next.y
                val d = distance + data[x][y]
                if (d < found) {
                    if (x == height - 1 && y == width - 1) {
                        found = d
                    }
                    val t = best[next]
                    if (t == null || t > d) {
                        best[next] = d
                        queue.add(next)
                    }
                }
            }
        }
        return best.filter { it.key.x == height - 1 && it.key.y == width - 1 }
    }

    data class Node(val x: Int, val y: Int, val dir: Dir, val lim: Int) {

        fun next(width: Int, height: Int): List<Node> {
            val result = mutableListOf<Node>()
            when (dir) {
                Dir.U -> {
                    if (lim < LIM && x > 0) {
                        result.add(Node(x - 1, y, dir, lim + 1))
                    }
                    if (y > 0) {
                        result.add(Node(x, y - 1, Dir.L, 1))
                    }
                    if (y + 1 < width) {
                        result.add(Node(x, y + 1, Dir.R, 1))
                    }
                }

                Dir.D -> {
                    if (lim < LIM && x + 1 < height) {
                        result.add(Node(x + 1, y, dir, lim + 1))
                    }
                    if (y > 0) {
                        result.add(Node(x, y - 1, Dir.L, 1))
                    }
                    if (y + 1 < width) {
                        result.add(Node(x, y + 1, Dir.R, 1))
                    }
                }

                Dir.L -> {
                    if (lim < LIM && y > 0) {
                        result.add(Node(x, y - 1, dir, lim + 1))
                    }
                    if (x > 0) {
                        result.add(Node(x - 1, y, Dir.U, 1))
                    }
                    if (x + 1 < height) {
                        result.add(Node(x + 1, y, Dir.D, 1))
                    }
                }

                Dir.R -> {
                    if (lim < LIM && y + 1 < width) {
                        result.add(Node(x, y + 1, dir, lim + 1))
                    }
                    if (x > 0) {
                        result.add(Node(x - 1, y, Dir.U, 1))
                    }
                    if (x + 1 < height) {
                        result.add(Node(x + 1, y, Dir.D, 1))
                    }
                }
            }
            return result
        }
    }

    enum class Dir {
        U,
        D,
        L,
        R
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-17.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
