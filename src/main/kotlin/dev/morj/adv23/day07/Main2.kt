package dev.morj.adv23.day07

@Suppress("MemberVisibilityCanBePrivate")
object Main2 {
    val normies = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
    val deck = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')
    val powers = deck.reversed().mapIndexed { index, c -> c to index }.toMap()

    class Hand(val rank: Int, val bid: Long, val cards: CharArray, val best: CharArray) : Comparable<Hand> {
        override fun toString(): String {
            return "Hand {${best.sortedArray().joinToString("")}, $rank, $bid, source: ${cards.sortedArray().joinToString("")}}"
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
            var maxRank = -1
            var bestHand = sourceHand
            normies.forEach { replacement ->
                val hand = sourceHand.map {
                    if (it == 'J') {
                        replacement
                    } else {
                        it
                    }
                }.toCharArray().sortedArray()
                val newRank = rankOf(hand)
                if (newRank > maxRank) {
                    maxRank = newRank
                    bestHand = hand
                }
            }
            hands.add(Hand(maxRank, bid, sourceHand, bestHand))
        }
        hands.sort()
        var result = 0L
        hands.forEachIndexed { index, hand ->
            println(hand)
            result += (index + 1) * hand.bid
        }
        println("result: $result")
    }

    private fun rankOf(hand: CharArray) = when {
        hand.fiveOfAKind -> 6
        hand.fourOfAKind -> 5
        hand.fullHouse -> 4
        hand.triple -> 3
        hand.twoPairs -> 2
        hand.onePair -> 1
        else -> 0
    }

    // all applicable only for sorted arrays
    val CharArray.fiveOfAKind: Boolean get() = this[0] == this[4]
    val CharArray.fourOfAKind: Boolean get() = this[0] == this[3] || this[1] == this[4]
    val CharArray.triple: Boolean get() = this[0] == this[2] || this[1] == this[3] || this[2] == this[4]
    val CharArray.pairs: Int
        get() {
            var result = 0
            this.toList().zipWithNext { a, b -> if (a == b) result++ }
            return result
        }
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
