package dev.morj.adv23.day01

object Main1 {
    @JvmStatic
    fun main(args: Array<String>) {
        val text = javaClass.classLoader.getResource("day-01.txt")?.readText()
        var sum = 0L
        text?.split('\n')?.forEach { line ->
            var firstDigit: Char? = null
            var lastDigit: Char? = null
            for (char in line) {
                if (char.isDigit()) {
                    if (firstDigit == null) {
                        firstDigit = char
                    }
                    lastDigit = char
                }
            }
            if (firstDigit != null && lastDigit != null) {
                sum += (firstDigit.digitToInt() * 10 + lastDigit.digitToInt()).also { println(it) }
            }
        }
        println(sum)
    }
}
