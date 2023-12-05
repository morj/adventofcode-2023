package dev.morj.adv23.day01

object Main2 {
    private val words = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
    private val wordsIndex = words.mapIndexed { index, value -> value to index + 1 }.toMap()
    private val wordsIndexReversed = words.mapIndexed { index, value -> value.reversed() to index + 1 }.toMap()
    private val pattern = "${words.joinToString("|")}|1|2|3|4|5|6|7|8|9"
    private val regex = Regex("($pattern)")
    private val regexReversed = Regex("(${pattern.reversed()})")

    @JvmStatic
    fun main(args: Array<String>) {
        val text = javaClass.classLoader.getResource("day-01.txt")?.readText()
        var sum = 0L
        text?.split('\n')?.forEach { line ->
            val firstDigit = regex.find(line)!!.toDigit(wordsIndex)
            val lastDigit = regexReversed.find(line.reversed())!!.toDigit(wordsIndexReversed)
            sum += (firstDigit * 10 + lastDigit).also { println(it) }
        }
        println(sum)
    }

    private fun MatchResult.toDigit(index: Map<String, Int>) = index[value] ?: value[0].digitToInt()
}
