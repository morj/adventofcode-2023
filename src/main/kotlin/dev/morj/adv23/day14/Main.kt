package dev.morj.adv23.day14

object Main {
    private const val REP_MAX = 1000000000
    private const val REP = 1000

    @JvmStatic
    fun main(args: Array<String>) {
        val data = mutableListOf<MutableList<Char>>()
        consumeInput { x, line ->
            data.add(line.toCharArray().toMutableList())
        }
        // summarize(data, part1(data))
        part2(data)
    }

    private fun part1(data: MutableList<MutableList<Char>>): MutableList<Rock> {
        val rocks = findRocks(data)
        rocks.forEach { it.moveNorth(data) }
        data.forEach {
            println(it.joinToString(""))
        }
        return rocks
    }

    private fun part2(data: MutableList<MutableList<Char>>): Int {
        val rocks = findRocks(data)
        val steps = mutableMapOf<Int, List<Rock>>()
        val fingerprints = mutableMapOf<String, Pair<Int, List<Rock>>>()
        (1..REP).forEach { step ->
            rocks.sortBy { it.x }
            rocks.forEach { it.moveNorth(data) }
            rocks.sortBy { it.y }
            rocks.forEach { it.moveWest(data) }
            rocks.sortByDescending { it.x }
            rocks.forEach { it.moveSouth(data) }
            rocks.sortByDescending { it.y }
            rocks.forEach { it.moveEast(data) }
            val fingerprint = fingerprint(rocks)
            fingerprints[fingerprint]?.let { prevStep ->
                if (rocks == prevStep.second) {
                    println("step $step matches step ${prevStep.first}")
                    val base = prevStep.first
                    val len = step - base
                    val offset = (REP_MAX - base) % len
                    summarize(data, steps[base + offset]!!)
                    return step
                }
            } ?: run {
                // println(fingerprint)
                val copy = rocks.map { Rock(it.x, it.y) }
                steps[step] = copy
                fingerprints[fingerprint] = step to copy
            }
            // debug(data)
            summarize(data, rocks)
        }
        throw NoSuchElementException()
    }

    private fun fingerprint(rocks: List<Rock>): String {
        return rocks.joinToString("|") { (it.x * 111 + it.y).toString() }
    }

    private fun debug(data: MutableList<MutableList<Char>>) {
        data.forEach {
            println(it.joinToString(""))
        }
        println("===========")
    }

    private fun summarize(data: MutableList<MutableList<Char>>, rocks: List<Rock>) {
        val height = data.size
        val result = rocks.sumOf { height - it.x }
        println("result: $result")
    }

    data class Rock(var x: Int, var y: Int) {
        fun moveNorth(data: MutableList<MutableList<Char>>) {
            while (x > 0 && data[x - 1][y] == '.') {
                data[x - 1][y] = 'O'
                data[x][y] = '.'
                x--
            }
        }

        fun moveSouth(data: MutableList<MutableList<Char>>) {
            val height = data.size
            while (x < height - 1 && data[x + 1][y] == '.') {
                data[x + 1][y] = 'O'
                data[x][y] = '.'
                x++
            }
        }

        fun moveWest(data: MutableList<MutableList<Char>>) {
            while (y > 0 && data[x][y - 1] == '.') {
                data[x][y - 1] = 'O'
                data[x][y] = '.'
                y--
            }
        }

        fun moveEast(data: MutableList<MutableList<Char>>) {
            val width = data[x].size
            while (y < width - 1 && data[x][y + 1] == '.') {
                data[x][y + 1] = 'O'
                data[x][y] = '.'
                y++
            }
        }
    }

    private fun findRocks(data: MutableList<MutableList<Char>>): MutableList<Rock> {
        val rocks = mutableListOf<Rock>()
        data.forEachIndexed { x, row ->
            row.forEachIndexed { y, char ->
                if (char == 'O') {
                    rocks.add(Rock(x, y))
                }
            }
        }
        return rocks
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-14.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
