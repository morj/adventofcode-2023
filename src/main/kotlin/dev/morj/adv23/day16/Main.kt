package dev.morj.adv23.day16

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val grid = mutableListOf<String>()
        var width = 0
        consumeInput { x, line ->
            grid.add(line)
            width = width.coerceAtLeast(line.length)
        }
        val beams = mutableSetOf<Beam>()
        // beams.add(Beam(0, -1, Dir.R))
        beams.add(Beam(-1, 3, Dir.D))
        extracted(beams, grid, width)
        val energized = beams.map { it.x to it.y }
        var result = 0
        grid.forEachIndexed { x, line ->
            line.forEachIndexed { y, c ->
                print(
                    if (energized.contains(x to y)) {
                        result++
                        '#'
                    } else {
                        // c
                        '.'
                    }
                )
            }
            println()
        }
        println("result: $result")
    }

    private fun extracted(
        beams: MutableSet<Beam>,
        grid: MutableList<String>,
        width: Int
    ) {
        var dirty: Boolean
        do {
            dirty = false
            beams.toList().forEach {
                val next = it.next(grid, width)
                dirty = dirty || beams.addAll(next)
            }
        } while (dirty)
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
