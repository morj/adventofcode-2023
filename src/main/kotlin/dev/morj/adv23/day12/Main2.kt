package dev.morj.adv23.day12

object Main2 {

    @JvmStatic
    fun main(args: Array<String>) {
        var result = 0L
        consumeInput { index, line ->
            val tokens = line.split(' ')
            val f = tokens.first().toCharArray().toList()
            val springs = f + '?' + f + '?' + f + '?' + f + '?' + f
            // val springs = f
            val g = tokens.last().split(',').map { it.toInt() }
            val groups = g + g + g + g + g
            // val groups = g
            val sum = gen(mutableMapOf(), ArrayList(groups.size), groups, springs, 0, springs.size)
            println("line ${index + 1}, sum: $sum")
            result += sum
        }
        println("result: $result")
    }

    private fun gen(
        cache: MutableMap<Pair<Int, Int>, Long>,
        acc: MutableList<Int>, groups: List<Int>, springs: List<Char>, start: Int, size: Int
    ): Long {
        var result = 0L
        var finish = size
        for (index in (start..<size)) {
            if (springs[index] == '#') {
                finish = index
                break
            }
        }
        for (offset in start..finish) {
            val cacheKey = offset to groups.size
            val cached = cache[cacheKey]
            if (cached != null) {
                result += cached
                continue
            }
            val group = groups.first()
            val next = offset + group
            val count = if (next <= size) {
                if ((offset..<next).any { springs[it] == '.' }) {
                    cache[cacheKey] = 0
                    continue
                }
                if (next < size && springs[next] == '#') {
                    cache[cacheKey] = 0
                    continue
                }
                val remaining = groups.subList(1, groups.size)
                if (remaining.isEmpty()) {
                    if (!(next + 1..<size).any { springs[it] == '#' }) {
                        1
                    } else {
                        0
                    }
                } else {
                    gen(cache, acc, remaining, springs, next + 1, size)
                }
            } else {
                0
            }
            cache[cacheKey] = count
            result += count
        }
        return result
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
