package dev.morj.adv23.day12

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        var result = 0L
        consumeInput { index, line ->
            val tokens = line.split(' ')
            val springs = tokens.first().toCharArray().toList()
            val groups = tokens.last().split(',').map { it.toInt() }
            var sum = 0L
            sequence {
                gen(mutableListOf(), groups, 0, springs.size)
            }.forEach {
                val candidate = springs.map { '.' }.toMutableList()
                // println(it.joinToString())
                it.forEachIndexed { i, offset ->
                    for (x in offset..<offset + groups[i]) {
                        candidate[x] = '#'
                    }
                }
                if (matches(springs, candidate)) {
                    sum++
                    //println(candidate.joinToString(""))
                }
            }
            println("line ${index + 1}, sum: $sum")
            result += sum
        }
        println("result: $result")
    }

    private fun test(
        matches: Sequence<List<Int>>,
        springs: List<Char>,
        groups: List<Int>
    ) {
        matches.forEach {
            val candidate = springs.map { '.' }.toMutableList()
            // println(it.joinToString())
            it.forEachIndexed { i, offset ->
                for (x in offset..<offset + groups[i]) {
                    candidate[x] = '#'
                }
            }
            if (matches(springs, candidate)) {
                println(candidate.joinToString(""))
            } else {
                println("!!!!")
                println(springs.joinToString(""))
                println(candidate.joinToString(""))
                error("")
            }
        }
    }

    private fun matches(springs: List<Char>, it: List<Char>): Boolean {
        springs.forEachIndexed { index, char ->
            if (char == '#' && it[index] != '#') return false
            if (char == '.' && it[index] != '.') return false
        }
        return true
    }

    private suspend fun SequenceScope<List<Int>>.gen(acc: MutableList<Int>, groups: List<Int>, start: Int, size: Int) {
        for (offset in start..size) {
            val group = groups.first()
            acc.addLast(offset)
            val next = offset + group
            if (next <= size) {
                val remaining = groups.subList(1, groups.size)
                if (remaining.isEmpty()) {
                    yield(acc)
                } else {
                    gen(acc, remaining, next + 1, size)
                }
            }
            acc.removeLast()
        }
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-12.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
