package dev.morj.adv23.day07

@Suppress("MemberVisibilityCanBePrivate")
object Main {
    val deck = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
    val powers = deck.reversed().mapIndexed { index, c -> c to index }.toMap()

    class Hand(val rank: Int, val bid: Long, val cards: CharArray) : Comparable<Hand> {
        override fun toString(): String {
            return "Hand {${cards.sortedArray().joinToString("")}, $rank, $bid}"
        }

        override fun compareTo(other: Hand): Int {
            if (rank != other.rank) return rank.compareTo(other.rank)
            (0..<5).forEach {
                val our = powers[cards[it]]!!
                val their = powers[other.cards[it]]!!
                if (our != their) {
                    return our.compareTo(their)
                }
            }
            return 0
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val hands = mutableListOf<Hand>()
        consumeInput { _, line ->
            val sourceHand = line.substring(0, 5).toCharArray()
            val bid = line.substring(6).toLong()
            // println("hand ${hand.joinToString()}, bid: $bid")
            val hand = sourceHand.sortedArray()
            hands.add(
                Hand(
                    when {
                        hand.fiveOfAKind -> 6
                        hand.fourOfAKind -> 5
                        hand.fullHouse -> 4
                        hand.triple -> 3
                        hand.twoPairs -> 2
                        hand.onePair -> 1
                        else -> 0
                    }, bid, sourceHand
                )
            )
        }
        hands.sort()
        var result = 0L
        hands.forEachIndexed { index, hand ->
            println(hand)
            result += (index + 1) * hand.bid
        }
        println("result: $result")
    }

    // all applicable only for sorted arrays
    val CharArray.fiveOfAKind: Boolean get() = this[0] == this[4]
    val CharArray.fourOfAKind: Boolean get() = this[0] == this[3] || this[1] == this[4]
    val CharArray.triple: Boolean get() = this[0] == this[2] || this[1] == this[3] || this[2] == this[4]
    val CharArray.pairs: Int
        get() = toList().zipWithNext().fold(0) { acc, p -> if (p.first == p.second) acc + 1 else acc }
    val CharArray.fullHouse: Boolean get() = pairs == 3
    val CharArray.twoPairs: Boolean get() = pairs == 2
    val CharArray.onePair: Boolean get() = pairs == 1

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-07.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
