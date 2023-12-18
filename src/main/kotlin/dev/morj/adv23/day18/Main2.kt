package dev.morj.adv23.day18

object Main2 {

    @JvmStatic
    fun main(args: Array<String>) {
        val instructions = computeInstructions2()
        val tmp = computeStart(instructions)
        println(tmp)
        val (_, _, startX, startY) = tmp
        val all = process(instructions, startX, startY)
        val ups = all.filterIsInstance(Line.V::class.java).filter { it.dir == Dir.U }
        val downs = all.filterIsInstance(Line.V::class.java).filter { it.dir == Dir.D }
        var area = 0L
        downs.sortedBy { it.x1 }.forEach { line ->
            val (i, y, x, x2) = line
            val opp = ups.filter {
                it.y < y && ((it.x1 <= x && it.x2 >= x) || (it.x1 <= x2 && it.x2 >= x2) || (it.x1 >= x && it.x2 <= x2))
            }.sortedByDescending { it.y }
            if (opp.isEmpty()) {
                throw IllegalStateException()
            }
            val steps = (opp.map { it.x1 } + opp.map { it.x2 }).distinct().sorted()
            var sum = 0L
            var x1 = if (all[i - 1].dir == Dir.R) x + 1 else x
            val end = if (all[i + 1].dir == Dir.L) x2 else x2 + 1
            while (x1 < end) {
                val prev = x1
                val next = steps.firstOrNull { it >= x1 }?.coerceAtMost(end) ?: end
                x1 = if (next > prev) {
                    next
                } else {
                    prev + 1
                }
                val l = opp.first { it.x1 <= prev && it.x2 >= prev }
                val delta = (x1 - prev).toLong() * (y - l.y - 1)
                sum += delta
                println(delta)
            }
            // print(data)
            area += sum
            println("--- $line")
        }
        // print(data)
        println()
        println("area: $area")
        val perimeter = all.sumOf { it.length }
        println("perimeter: $perimeter")
        println("result: ${area + perimeter}")
    }

    private fun computeInstructions2(): MutableList<Inst> {
        val instructions = mutableListOf<Inst>()
        consumeInput { _, line ->
            val tokens = line.split(' ')
            val c = tokens[2]
            val l = c.substring(2, c.length - 2)
            val d = c.substring(c.length - 2, c.length - 1)
            instructions.add(Inst(Dir.entries[d.toInt(16)], l.toInt(16)))
        }
        return instructions
    }

    private fun process(instructions: MutableList<Inst>, startX: Int, startY: Int): List<Line> {
        val data = arrayListOf<Line>()
        var x = startX
        var y = startY
        // var prev = Line.H(0, 0, 0, 0, Dir.R)
        instructions.forEachIndexed { i, it ->
            val l = it.length
            when (it.dir) {
                Dir.U -> {
                    data.add(Line.V(i, y, x - l, x, it.dir))
                    x -= l
                }

                Dir.D -> {
                    data.add(Line.V(i, y, x, x + l, it.dir))
                    x += l
                }

                Dir.L -> {
                    val line = Line.H(i, x, y - l, y, it.dir)
                    // prev = line
                    data.add(line)
                    y -= l
                }

                Dir.R -> {
                    val line = Line.H(i, x, y, y + l, it.dir)
                    // prev = line
                    data.add(line)
                    y += l
                }
            }
        }
        return data
    }

    sealed class Line {
        abstract val dir: Dir

        abstract val length: Int

        data class V(val i: Int, val y: Int, val x1: Int, val x2: Int, override val dir: Dir) : Line() {
            override val length: Int
                get() = x2 - x1
        }
        data class H(val i: Int, val x: Int, val y1: Int, val y2: Int, override val dir: Dir) : Line() {
            override val length: Int
                get() = y2 - y1
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
        R,
        D,
        L,
        U
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
