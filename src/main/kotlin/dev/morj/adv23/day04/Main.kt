package dev.morj.adv23.day04

import kotlin.math.pow

object Main {
    private const val DELIMITER = 39
    private const val START = 10
    /* private const val START = 8
    private const val DELIMITER = 22 */

    @JvmStatic
    fun main(args: Array<String>) {
        var sum = 0
        val power = mutableMapOf<Int, Int>()
        consumeInput { index, s ->
            val winningsText = s.substring(START, DELIMITER)
            val winnings = tokenize(winningsText).mapTo(mutableSetOf()) { it.toInt() }
            //println("Card #${index + 1} has ${winnings.joinToString()}")
            val havesText = s.substring(DELIMITER + 3)
            val haves = tokenize(havesText).map { it.toInt() }
            //println("Card #${index + 1} has ours ${haves.joinToString()}")
            val ourWins = haves.filter { winnings.contains(it) }
            val score = if (ourWins.isEmpty()) {
                0
            } else {
                2.toDouble().pow(ourWins.size - 1).toInt()
            }
            println("Card #${index + 1} has our wins: ${ourWins.joinToString()}, score is $score")
            sum += score
            power[index] = ourWins.size
        }
        println("result: $sum")
        val cardCount = power.size
        val cards = (1..cardCount).mapTo(arrayListOf()) { 1L }
        for (index in (0..<cardCount)) {
            val weight = cards[index]
            val pow = power[index] ?: 0
            var offset = index + 1
            repeat(pow) {
                if (offset < cardCount) {
                    cards[offset] = cards[offset] + weight
                }
                offset++
            }
        }
        for (index in (0..<cardCount)) {
            println("card weight: ${cards[index]}")
        }
        println("part 2 result ${cards.sum()}")
    }

    private fun tokenize(winningsText: String): List<String> {
        return winningsText.split(' ').filter { it.isNotEmpty() }
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-04.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
