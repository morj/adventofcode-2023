package dev.morj.adv23.day01

object Main3 {
    private val words = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
    private val wordsIndex = words.mapIndexed { index, value -> value to index + 1 }.toMap()
    private val pattern = "${words.joinToString("|")}|1|2|3|4|5|6|7|8|9"
    private val regex = Regex("($pattern)")
    private val eagerRegex = Regex(".*($pattern)")

    @JvmStatic
    fun main(args: Array<String>) {
        val text = javaClass.classLoader.getResource("day-01.txt")?.readText()
        var sum = 0L
        text?.split('\n')?.forEach { line ->
            val firstDigit = regex.find(line)!!.value.toDigit()
            val lastDigit = eagerRegex.find(line)!!.groups.last()!!.value.toDigit()
            sum += (firstDigit * 10 + lastDigit).also { println(it) }
        }
        println(sum)
    }

    private fun String.toDigit() = wordsIndex[this] ?: this[0].digitToInt()
}
