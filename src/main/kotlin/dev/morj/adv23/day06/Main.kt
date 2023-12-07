package dev.morj.adv23.day06

import kotlin.math.sqrt

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val times = arrayListOf<Long>()
        val distances = arrayListOf<Long>()
        /*consumeInput { index, line ->
            val elements = line.substring(10).tokenize().map { it.toLong() }
            if (index == 0) {
                times.addAll(elements)
            } else {
                distances.addAll(elements)
            }
        }*/
        consumeInput { index, line ->
            val element = line.substring(10).replace(" ", "").toLong()
            if (index == 0) {
                times.add(element)
            } else {
                distances.add(element)
            }
        }
        println(times.joinToString())
        println(distances.joinToString())
        solve(distances, times)
    }

    private fun solve(distances: ArrayList<Long>, times: ArrayList<Long>) {
        var result = 1L
        distances.forEachIndexed { index, b ->
            val t = times[index].toDouble()
            val q = (t * t) - (4 * b)
            val d = sqrt(q) / 2
            val x1 = t / 2 - d
            val x2 = t / 2 + d
            println("roots: $x1, $x2")
            if (x2 > b) {
                throw UnsupportedOperationException()
            }
            // val ways = ceil(sqrt(q)).toLong()
            val ways = x2.toLong() - x1.toLong()
            result *= ways
        }
        println("result: $result")
    }

    private fun String.tokenize(): List<String> {
        return split(' ').filter { it.isNotEmpty() }
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-06.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
