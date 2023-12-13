package dev.morj.adv23.day13

object Main {
    private const val EXPECT_ERRORS = 1

    @JvmStatic
    fun main(args: Array<String>) {
        val patterns = mutableListOf<MutableList<String>>()
        var offset = 0
        consumeInput { index, line ->
            if (index == 0) {
                patterns.add(mutableListOf())
            }
            if (line.isEmpty()) {
                patterns.add(mutableListOf())
                offset++
            } else {
                patterns[offset].add(line)
            }
        }
        var a = 0L
        var b = 0L
        patterns.forEach {
            val (vert, count) = analyze(it)
            if (vert) {
                a += count
            } else {
                b += count
            }
        }
        println(a * 100 + b)
    }

    private fun analyze(lines: List<String>): Pair<Boolean, Int> {
        val (size, count) = sym(transpose(lines))
        val (size2, count2) = sym(lines)
        return if (size2 > size) {
            true to count2
        } else {
            false to count
        }
    }

    private fun sym(lines: List<String>): Pair<Int, Int> {
        var result = (0 to -1)
        for (start in lines.indices) {
            val size = minOf(start, lines.size - start)
            if (size > 0) {
                val errors = (1..size).sumOf { offset ->
                    diff(lines[start - offset], lines[start + offset - 1])
                }
                if (errors == EXPECT_ERRORS && size > result.first) {
                    result = size to start
                }
            }
        }
        return result
    }

    private fun diff(l1: String, l2: String): Int {
        return if (l1 == l2) {
            0
        } else {
            var result = 0
            l1.forEachIndexed { index, c ->
                if (l2[index] != c) {
                    result++
                }
            }
            result
        }
    }

    private fun transpose(lines: List<String>): List<String> {
        val count = lines.maxOf { it.length }
        val chars = (0..<count).map { mutableListOf<Char>() }
        lines.forEach { line ->
            line.forEachIndexed { y, c -> chars[y].add(c) }
        }
        return chars.map { it.joinToString("") }.onEach {
            // println(it)
        }
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-13.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
