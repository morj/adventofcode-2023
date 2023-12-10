package dev.morj.adv23.day09

object Main2 {
    @JvmStatic
    fun main(args: Array<String>) {
        var sum = 0
        consumeInput { index, line ->
            var values = line.split(' ').mapNotNull { it.takeIf { it.isNotEmpty() }?.toInt() }
            val table = mutableListOf(values)
            do {
                values = next(values)
                table.add(values)
            } while (values.any { it != 0 })
            val reversed = table.reversed()
            reversed.forEachIndexed { i, list ->
                if (i == 0) {
                    list.addFirst(0)
                } else {
                    list.addFirst(list.first() - reversed[i - 1].first())
                }
            }
            table.forEach {
                println(it.joinToString())
            }
            val extrapolated = table.first().first()
            sum += extrapolated
            println("=== $extrapolated")
        }
        println("result: $sum")
    }

    private fun next(values: List<Int>): List<Int> {
        return values.zipWithNext { a, b -> b - a }
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-09.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
