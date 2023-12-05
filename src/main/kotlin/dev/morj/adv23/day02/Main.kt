package dev.morj.adv23.day02

object Main {
    private val limits = mapOf("red" to 12, "green" to 13, "blue" to 14)
    private val headerRegex = Regex("Game (.*):")
    private val gameRegex = Regex("(?:([0-9]+\\s[a-z]+),\\s)?(?:([0-9]+\\s[a-z]+),\\s)?([0-9]+\\s[a-z]+)[;\\n\$]")

    @JvmStatic
    fun main(args: Array<String>) {
        var sum = 0L
        val text = javaClass.classLoader.getResource("day-02.txt")?.readText()
        text?.split('\n')?.forEach { line ->
            sum += processGame(line)
        }
        println("total: $sum")
    }

    private fun processGame(line: String): Int {
        val gameId = headerRegex.find(line)?.groups?.takeIf { it.size > 1 }?.get(1)?.value?.toInt()!!
        println("Found game #$gameId")
        var gamePossible = true
        val minimums = mutableMapOf<String, Int>()
        gameRegex.findAll(line + "\n").forEach { matchResult ->
            matchResult.groups.forEachIndexed { index, matchGroup ->
                if (index > 0) { // skip entire match
                    matchGroup?.value?.let {
                        val tokens = it.split(" ")
                        val count = tokens[0].toInt()
                        val color = tokens[1]
                        val limit = limits[color]!!
                        println("limit: $limit, actual $count")
                        minimums[color] = (minimums[color] ?: 0).coerceAtLeast(count)
                        if (count > limit) {
                            gamePossible = false
                        }
                    }
                }
            }
            // println("========")
        }
        minimums.forEach { (color, limit) -> println("for $color: at least $limit") }
        println("verdict: $gamePossible")
        return minimums.values.fold(1) { acc, i -> acc * i }
    }
}
