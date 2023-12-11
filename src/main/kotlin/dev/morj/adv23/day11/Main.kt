package dev.morj.adv23.day11

import kotlin.math.abs

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val data = mutableListOf<MutableList<Char>>()
        val emptyCol = mutableListOf<Boolean>()
        consumeInput { index, line ->
            val chars = line.toCharArray()
            if (index == 0) {
                emptyCol.addAll(chars.map { c -> c == '.' })
            } else {
                chars.forEachIndexed { i, c ->
                    emptyCol[i] = c == '.' && emptyCol[i]
                }
            }
            data.add(chars.toMutableList())
            if (line.all { it == '.' }) {
                data.add(chars.toMutableList())
            }
        }
        var offset = 0
        emptyCol.forEachIndexed { index, b ->
            if (b) {
                data.forEach { it.add(index + offset, '.') }
                offset++
            }
        }
        data.forEach {
            println(it.joinToString(""))
        }
        val stars = mutableListOf<Pair<Int, Int>>()
        data.forEachIndexed { x, chars -> 
            chars.forEachIndexed { y, c -> 
                if (c == '#') {
                    stars.add(x to y)
                }
            }
        }
        var sum = 0L
        var pairs = 0
        stars.forEachIndexed { na, a ->
            stars.forEachIndexed { nb, b ->
                if (na < nb) {
                    pairs++
                    val dist = dist(a, b)
                    sum += dist
                    println("from $a to $b is $dist")
                }
            }
        }
        println("pairs $pairs, result $sum")
    }

    private fun dist(a: Pair<Int, Int>, b: Pair<Int, Int>): Int {
        return abs(a.first - b.first) + abs(a.second - b.second)
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-11.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
