package dev.morj.adv23.day08

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val ids = mutableMapOf<String, Int>()
        var dir = ""
        consumeInput { index, line ->
            if (index == 0) {
                dir = line
            } else if (index > 1) {
                ids[line.substring(0, 3)] = index - 2
            }
        }
        val directions = BooleanArray(dir.length) { dir[it] == 'R' }
        val source = ids["AAA"]!!
        val target = ids["ZZZ"]!!
        val network = IntArray(ids.size * 2)
        consumeInput { index, line ->
            if (index > 1) {
                val l = line.substring(7, 10)
                val r = line.substring(12, 15)
                val id = index - 2
                network[id.l] = ids[l]!!
                network[id.r] = ids[r]!!
            }
        }
        var current = source
        var distance = 0L
        while (current != target) {
            val offset = directions[(distance % directions.size).toInt()]
            current = if (offset) network[current.r] else network[current.l]
            distance++
        }
        println("distance: $distance")
    }

    private val Int.l get() = 2 * this
    private val Int.r get() = 2 * this + 1

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-08.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
