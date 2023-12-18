package dev.morj.adv23.day18

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val instructions = computeInstructions()
        val (height, width, startX, startY) = computeStart(instructions)
        val data = Array(height) { CharArray(width) { '.' } }
        process(data, instructions, startX, startY)
        // print(data)
        // println()
        // fillTheVoid(data)
        dfs(data, 180, 180)
        print(data)
        println("total: ${count(data)}")
        println("perimeter: ${count(data, '#')}")
    }

    private fun computeInstructions(): MutableList<Inst> {
        val instructions = mutableListOf<Inst>()
        consumeInput { _, line ->
            val tokens = line.split(' ')
            val dir = Dir.valueOf(tokens[0])
            val length = tokens[1].toInt()
            instructions.add(Inst(dir, length))
        }
        return instructions
    }

    private fun process(data: Array<CharArray>, instructions: MutableList<Inst>, startX: Int, startY: Int) {
        var x = startX
        var y = startY
        instructions.forEach {
            val l = it.length
            when (it.dir) {
                Dir.U -> {
                    ((x - l)..x).forEach { x1 -> data[x1][y] = '#' }
                    x -= l
                }

                Dir.D -> {
                    (x..(x + l)).forEach { x1 -> data[x1][y] = '#' }
                    x += l
                }

                Dir.L -> {
                    ((y - l)..y).forEach { y1 -> data[x][y1] = '#' }
                    y -= l
                }

                Dir.R -> {
                    (y..(y + l)).forEach { y1 -> data[x][y1] = '#' }
                    y += l
                }
            }
        }
    }

    private fun count(data: Array<CharArray>, target: Char = '*'): Int {
        var result = 0
        data.forEach { line ->
            line.forEach { c ->
                if (c == '#' || c == target) {
                    result++
                }
            }
        }
        return result
    }

    private fun dfs(data: Array<CharArray>, startX: Int, startY: Int) {
        val queue = mutableListOf<Pair<Int, Int>>()
        queue.add(startX to startY)
        val w = data[0].size
        val visited = mutableSetOf<Pair<Int, Int>>()
        while (queue.isNotEmpty()) {
            val (x, y) = queue.removeLast()
            data[x][y] = '*'
            visited.add(x to y)
            val neighbors = mutableListOf<Pair<Int, Int>>()
            if (x > 0) {
                neighbors.add(x - 1 to y)
            }
            if (x + 1 < data.size) {
                neighbors.add(x + 1 to y)
            }
            if (y > 0) {
                neighbors.add(x to y - 1)
            }
            if (y + 1 < w) {
                neighbors.add(x to y + 1)
            }
            neighbors.forEach {
                if (!visited.contains(it) && data[it.first][it.second] != '#') {
                    queue.add(it)
                }
            }
        }
    }

    private fun fillTheVoid(data: Array<CharArray>) {
        data.forEachIndexed { x, line ->
            var inside = false
            var prev = '.'
            line.forEachIndexed { y, c ->
                if (inside) {
                    if (c == '#' && prev == '.') {
                        inside = false
                    }
                } else {
                    if (c == '.' && prev == '#') {
                        inside = true
                    }
                }
                if (inside) {
                    data[x][y] = '#'
                }
                prev = c
            }
        }
    }

    private fun computeStart(instructions: MutableList<Inst>): Dim {
        var minX = 0
        var minY = 0
        var x = 0
        var y = 0
        var maxX = 0
        var maxY = 0
        instructions.forEach {
            val l = it.length
            when (it.dir) {
                Dir.U -> x -= l
                Dir.D -> x += l
                Dir.L -> y -= l
                Dir.R -> y += l
            }
            minX = minOf(minX, x)
            minY = minOf(minY, y)
            maxX = maxX.coerceAtLeast(x)
            maxY = maxY.coerceAtLeast(y)
        }
        val startX = -minX
        val startY = -minY
        return Dim(maxX + startX + 1, maxY + startY + 1, startX, startY)
    }

    private fun print(data: Array<CharArray>) {
        data.forEach {
            println(it.joinToString(""))
        }
    }

    data class Dim(
        val height: Int,
        val length: Int,
        val startX: Int,
        val startY: Int
    )

    data class Inst(
        val dir: Dir,
        val length: Int
    )

    enum class Dir {
        U,
        D,
        L,
        R
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-18.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
