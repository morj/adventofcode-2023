package dev.morj.adv23.day21

import java.math.BigInteger

object Part2Notebook {
    @JvmStatic
    fun main(args: Array<String>) {
        // foo()
        bar()
        // 26501365 = 202300 * 131 + 65
        println(202300 * 131 + 65)
        println("===")
        // 0 -> 3730    1
        // 1 -> 33366   4
        // 2 -> 92548   9
        // 3 -> 181276
        val data = arrayListOf(3730, 33366, 92548, 181276)
        val q = mutableListOf<Int>()
        for (n in (0..4)) {
            println((14773 * n * n) + (14863 * n) + 3730)
        }
        println()
        var prev = 0
        for (n in (1..3)) {
            val t = 0
            // println(14783 * ((2 * n - 1) * (2 * n - 1) - 4 * (2 * n - 2)))
            val diff = data[n - 1] - (14773 + t) * (n - 1) * (n - 1) - (14863 * (n - 1))
            // println(diff)
            println(diff - prev)
            q.add(diff - prev)
            prev = diff
        }
        /*for (t in (0..100)) {
            println("q: ${q[2] - q[1]}, t: $t")
        }*/
        /*for (t in (0..9)) {
            val a = t * t * 3730
            println(a)
            val b = (t + 1) * (t + 1) * (3730 + 202)
            println(b)
            println("sum: ${a + b}")
        }*/
    }

    private fun foo() {
        val n = BigInteger.valueOf(202300 - 1)
        val a = BigInteger.valueOf(14773)
        val b = BigInteger.valueOf(14863)
        val c = BigInteger.valueOf(3730)
        println(a * n * n + (b * n) + c)
    }

    private fun bar() {
        val n = 202300L
        println((14773 * n * n) + (14863 * n) + 3730)
    }
}
