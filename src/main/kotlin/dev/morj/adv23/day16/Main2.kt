package dev.morj.adv23.day16

object Main2 {
    @JvmStatic
    fun main(args: Array<String>) {
        val grid = mutableListOf<String>()
        var width = 0
        consumeInput { x, line ->
            grid.add(line)
            width = width.coerceAtLeast(line.length)
        }
        var count = 0
        var beam = Beam(0, -1, Dir.R)
        (0..<grid.size).forEach {
            val s1 = Beam(it, -1, Dir.R)
            val c1 = trace(s1, grid, width)
            if (c1 > count) {
                beam = s1
                count = c1
            }
            val s2 = Beam(it, width, Dir.L)
            val c2 = trace(s2, grid, width)
            if (c2 > count) {
                beam = s2
                count = c2
            }
            println("line $it")
        }
        val height = grid.size
        (0..<width).forEach {
            val s1 = Beam(-1, it, Dir.D)
            val c1 = trace(s1, grid, width)
            if (c1 > count) {
                beam = s1
                count = c1
            }
            val s2 = Beam(height, it, Dir.U)
            val c2 = trace(s2, grid, width)
            if (c2 > count) {
                beam = s2
                count = c2
            }
            println("column $it")
        }
        println("result: $count, starting at $beam")
    }

    private fun trace(startingBeam: Beam, grid: MutableList<String>, width: Int): Int {
        val beams = hashSetOf(startingBeam)
        val tiles = hashSetOf<Pair<Int, Int>>()
        var dirty: Boolean
        do {
            dirty = false
            beams.toList().forEach { beam ->
                val next = beam.next(grid, width)
                dirty = dirty || beams.addAll(next)
                tiles.addAll(next.map { it.x to it.y })
            }
        } while (dirty)
        return tiles.size
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-16.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }

    enum class Dir {
        U,
        D,
        L,
        R
    }

    data class Beam(val x: Int, val y: Int, val dir: Dir) {
        fun next(grid: MutableList<String>, width: Int): Collection<Beam> {
            val result = mutableListOf<Beam>()
            when (dir) {
                Dir.U -> if (x > 0) {
                    val c = grid[x - 1][y]
                    when (c) {
                        '|', '.' -> {
                            result.add(Beam(x - 1, y, Dir.U))
                        }

                        '-' -> {
                            result.add(Beam(x - 1, y, Dir.L))
                            result.add(Beam(x - 1, y, Dir.R))
                        }

                        '/' -> {
                            result.add(Beam(x - 1, y, Dir.R))
                        }

                        '\\' -> {
                            result.add(Beam(x - 1, y, Dir.L))
                        }
                    }
                }

                Dir.D -> if (x < grid.size - 1) {
                    val c = grid[x + 1][y]
                    when (c) {
                        '|', '.' -> {
                            result.add(Beam(x + 1, y, Dir.D))
                        }

                        '-' -> {
                            result.add(Beam(x + 1, y, Dir.L))
                            result.add(Beam(x + 1, y, Dir.R))
                        }

                        '/' -> {
                            result.add(Beam(x + 1, y, Dir.L))
                        }

                        '\\' -> {
                            result.add(Beam(x + 1, y, Dir.R))
                        }
                    }
                }

                Dir.R -> if (y < width - 1) {
                    val c = grid[x][y + 1]
                    when (c) {
                        '-', '.' -> {
                            result.add(Beam(x, y + 1, Dir.R))
                        }

                        '|' -> {
                            result.add(Beam(x, y + 1, Dir.U))
                            result.add(Beam(x, y + 1, Dir.D))
                        }

                        '/' -> {
                            result.add(Beam(x, y + 1, Dir.U))
                        }

                        '\\' -> {
                            result.add(Beam(x, y + 1, Dir.D))
                        }
                    }
                }

                Dir.L -> if (y > 0) {
                    val c = grid[x][y - 1]
                    when (c) {
                        '-', '.' -> {
                            result.add(Beam(x, y - 1, Dir.L))
                        }

                        '|' -> {
                            result.add(Beam(x, y - 1, Dir.U))
                            result.add(Beam(x, y - 1, Dir.D))
                        }

                        '/' -> {
                            result.add(Beam(x, y - 1, Dir.D))
                        }

                        '\\' -> {
                            result.add(Beam(x, y - 1, Dir.U))
                        }
                    }
                }
            }
            return result
        }
    }
}
