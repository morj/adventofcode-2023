package dev.morj.adv23.day10

object Main {
    private enum class Dir(val offset: Int) {
        U(-1),
        D(+1),
        L(-1),
        R(+1)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        var start = 0 to 0
        consumeInput { x, line ->
            line.forEachIndexed { y, c ->
                if (c == 'S') {
                    start = x to y
                }
            }
        }
        val data = arrayListOf<String>()
        consumeInput { _, line ->
            data.add(line)
        }
        var pos = Triple(Dir.R, start.first, start.second)
        var length = 0
        val grid = arrayListOf<MutableList<Int>>()
        data.forEach { line -> grid.add(line.map { 0 }.toMutableList()) }
        do {
            // println(pos)
            val (dir, x, y) = pos
            grid[x][y] = 2
            updateAdj(data[x][y], dir, grid, x, y)
            pos = next(pos, data)
            length++
        } while (pos.second != start.first || pos.third != start.second)
        // bfs(grid)
        grid.forEachIndexed { x, line ->
            line.mapIndexed { y, char ->
                if (grid[x][y] == 3) {
                    bfs(x, y, grid)
                }
            }
        }
        grid.forEachIndexed { x, line ->
            println(line.mapIndexed { y, char ->
                when (char) {
                    1 -> "*"
                    //2 -> data[x][y]
                    2 -> '█'
                    3 -> '☹'
                    4 -> '☺'
                    else -> " "
                }
            }.joinToString(""))
        }
        var area = 0L
        grid.forEach { line ->
            line.forEach {
                if (it == 4) {
                    area++
                }
            }
        }
        println("length: $length")
        println("result: ${length / 2}")
        println("area: $area")
    }

    private fun updateAdj(char: Char, dir: Dir, grid: ArrayList<MutableList<Int>>, x: Int, y: Int) {
        when (char) {
            '|' -> when (dir) {
                Dir.U -> grid.set(x, y + 1)
                Dir.D -> grid.set(x, y - 1)
                else -> throw IllegalArgumentException()
            }

            '-' -> when (dir) {
                Dir.R -> grid.set(x + 1, y)
                Dir.L -> grid.set(x - 1, y)
                else -> throw IllegalArgumentException()
            }

            'L' -> when (dir) {
                Dir.D -> {
                    grid.set(x + 1, y)
                    grid.set(x + 1, y - 1)
                    grid.set(x, y - 1)
                }

                Dir.L -> grid.set(x - 1, y + 1)
                else -> throw IllegalArgumentException()
            }

            'J' -> when (dir) {
                Dir.D -> grid.set(x - 1, y - 1)
                Dir.R -> {
                    grid.set(x + 1, y)
                    grid.set(x + 1, y + 1)
                    grid.set(x, y + 1)
                }

                else -> throw IllegalArgumentException()
            }

            '7' -> when (dir) {
                Dir.U -> {
                    grid.set(x, y + 1)
                    grid.set(x - 1, y + 1)
                    grid.set(x - 1, y)
                }

                Dir.R -> grid.set(x + 1, y - 1)
                else -> throw IllegalArgumentException()
            }

            'F' -> when (dir) {
                Dir.L -> {
                    grid.set(x - 1, y)
                    grid.set(x - 1, y - 1)
                    grid.set(x, y - 1)
                }

                Dir.U -> grid.set(x + 1, y + 1)
                else -> throw IllegalArgumentException()
            }
        }
    }

    private fun ArrayList<MutableList<Int>>.set(x: Int, y: Int) {
        if (x in 0..<size) {
            val line = this[x]
            if (y in 0..<line.size && line[y] == 0) {
                line[y] = 3
            }
        }
    }

    private fun bfs(startX: Int, startY: Int, grid: ArrayList<MutableList<Int>>) {
        val queue = arrayListOf(startX to startY)
        while (queue.isNotEmpty()) {
            val (x, y) = queue.removeLast()
            val line = grid[x]
            if (line[y] == 0 || line[y] == 3) {
                line[y] = 4
                if (x < grid.size - 1) {
                    queue.add(x + 1 to y)
                }
                if (x > 0) {
                    queue.add(x - 1 to y)
                }
                if (y < line.size - 1) {
                    queue.add(x to y + 1)
                }
                if (y > 0) {
                    queue.add(x to y - 1)
                }
            }
        }
    }

    private fun next(pos: Triple<Dir, Int, Int>, data: ArrayList<String>): Triple<Dir, Int, Int> {
        val (dir, x, y) = pos
        val line = data[x]
        // val symbol = if (line[y] == 'S') 'F' else line[y]
        val symbol = if (line[y] == 'S') 'J' else line[y]
        return when (symbol) {
            '|' -> Triple(dir, x + dir.offset, y)
            '-' -> Triple(dir, x, y + dir.offset)
            'L' -> when (dir) {
                Dir.D -> Triple(Dir.R, x, y + 1)
                Dir.L -> Triple(Dir.U, x - 1, y)
                else -> throw IllegalArgumentException()
            }

            'J' -> when (dir) {
                Dir.D -> Triple(Dir.L, x, y - 1)
                Dir.R -> Triple(Dir.U, x - 1, y)
                else -> throw IllegalArgumentException()
            }

            '7' -> when (dir) {
                Dir.U -> Triple(Dir.L, x, y - 1)
                Dir.R -> Triple(Dir.D, x + 1, y)
                else -> throw IllegalArgumentException()
            }

            'F' -> when (dir) {
                Dir.L -> Triple(Dir.D, x + 1, y)
                Dir.U -> Triple(Dir.R, x, y + 1)
                else -> throw IllegalArgumentException()
            }

            else -> throw IllegalArgumentException()
        }
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-10.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
