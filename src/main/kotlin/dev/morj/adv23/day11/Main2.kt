package dev.morj.adv23.day11

import kotlin.math.abs

object Main2 {
    val REP = 1000000

    @JvmStatic
    fun main(args: Array<String>) {
        val data = mutableListOf<MutableList<Char>>()
        val emptyCol = mutableListOf<Boolean>()
        val emptyRow = mutableListOf<Boolean>()
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
            emptyRow.add(line.all { it == '.' })
        }
        data.forEach {
            println(it.joinToString(""))
        }
        var stars = mutableListOf<Pair<Int, Int>>()
        data.forEachIndexed { x, chars ->
            chars.forEachIndexed { y, c ->
                if (c == '#') {
                    stars.add(x to y)
                }
            }
        }
        val (emptyColCount, emptyRowCount) = expandSpace(emptyCol, emptyRow)
        println(emptyColCount.joinToString())
        println(emptyRowCount.joinToString())
        stars = stars.map {
            (it.first + emptyRowCount[it.first] * (REP - 1)) to (it.second + emptyColCount[it.second] * (REP - 1))
        }.toMutableList()
        var sum = 0L
        var pairs = 0
        stars.forEachIndexed { na, a ->
            stars.forEachIndexed { nb, b ->
                if (na < nb) {
                    pairs++
                    val dist = dist(a, b)
                    sum += dist
                    // println("from $a to $b is $dist")
                }
            }
        }
        println("pairs $pairs, result $sum")
    }

    private fun dist(a: Pair<Int, Int>, b: Pair<Int, Int>): Int {
        return abs(a.first - b.first) + abs(a.second - b.second)
    }

    private fun expandSpace(emptyCol: MutableList<Boolean>, emptyRow: MutableList<Boolean>): Pair<List<Int>, List<Int>> {
        var tmp1 = 0 // no fold + map in Kotlin, or am I missing it?
        val emptyColCount = emptyCol.map { if (it) tmp1++ else tmp1 }
        tmp1 = 0
        return Pair(emptyColCount, emptyRow.map { if (it) tmp1++ else tmp1 })
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
