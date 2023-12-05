package dev.morj.adv23.day03

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val sym = mutableSetOf<Long>() // symbol offsets
        val starOffsets = mutableSetOf<Long>()
        consumeInput { x, text ->
            text.forEachIndexed { y, char ->
                when {
                    char == '.' || char.isDigit() -> Unit
                    else -> {
                        sym.add(combine(x, y))
                        if (char == '*') {
                            starOffsets.add(combine(x, y))
                        }
                    }
                }
            }
        }
        var sum = 0L
        val starToPart = mutableMapOf<Long, MutableSet<Pair<Long, Int>>>()
        val numberRegex = Regex("[0-9]+")
        consumeInput { x, text ->
            numberRegex.findAll(text).forEach {
                val coordinates = combine(x, it.range.first)
                var partNumber = -1
                if (it.range.any { y -> adjacentPoints(x, y).any { (x1, y1) -> sym.contains(combine(x1, y1)) } }) {
                    partNumber = it.value.toInt()
                    println("found part number: $partNumber")
                    sum += partNumber
                }
                if (partNumber >= 0) {
                    it.range.forEach { y ->
                        adjacentPoints(x, y).forEach { (x1, y1) ->
                            val star = combine(x1, y1)
                            if (starOffsets.contains(star)) {
                                val set = starToPart[star]
                                if (set == null) {
                                    starToPart[star] = mutableSetOf(coordinates to partNumber)
                                } else {
                                    set.add(coordinates to partNumber)
                                }
                            }
                        }
                    }
                }
            }
        }
        println("part 1 result $sum")
        sum = 0
        consumeInput { x, text ->
            text.forEachIndexed { y, char ->
                if (char == '*') {
                    starToPart[combine(x, y)]?.let {
                        if (it.size == 2) {
                            val partA = it.first().second
                            val partB = it.last().second
                            println("found gear at $x, $y, adjacent parts: $partA, $partB")
                            sum += partA * partB
                        }
                    }
                }
            }
        }
        println("part 2 result $sum")
    }

    private fun adjacentPoints(x: Int, y: Int) = arrayOf(
        x - 1 to y, x + 1 to y, x to y - 1, x to y + 1,
        x + 1 to y + 1, x - 1 to y - 1, x + 1 to y - 1, x - 1 to y + 1
    )

    private fun combine(x: Int, y: Int): Long {
        return 1024L * (x + 1) + y + 1
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-03.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
