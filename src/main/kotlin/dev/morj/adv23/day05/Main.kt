package dev.morj.adv23.day05

object Main {
    data class Range(val pos: Long, val length: Long)
    class Seed(val id: Long, val chain: MutableList<Range>)
    data class MappingEntry(val to: Long, val from: Long, val length: Long)
    class MappingTable(val mappings: List<MappingEntry>)

    @JvmStatic
    fun main(args: Array<String>) {
        val (seedIds, mappingTables) = parseInput()
        // var seeds = seedIds.map { Seed(it, arrayListOf(Range(it, 1))) }
        var seeds = seedIds.windowed(2, 2).map { Seed(it.first(), arrayListOf(Range(it.first(), it.last()))) }
        for (mappingTable in mappingTables) {
            seeds = splitByMappings(seeds, mappingTable.mappings)
            seeds = applyMappings(seeds, mappingTable.mappings)
        }
        seeds.forEach {
            println("seed ${it.id}: ${it.chain.last()}")
        }
        println("result: ${seeds.minBy { it.chain.last().pos }.chain.last().pos}")
    }

    private fun splitByMappings(seeds: List<Seed>, mappings: List<MappingEntry>): List<Seed> {
        return seeds.map { seed ->
            var s = listOf(seed)
            mappings.forEach { entry ->
                s = s.map { splitL(it, entry.from) }.flatten()
                s = s.map { splitR(it, entry.from + entry.length - 1) }.flatten()
            }
            s
        }.flatten()
    }

    private fun splitR(seed: Seed, point: Long): List<Seed> {
        val seedRange = seed.chain.last()
        val end = seedRange.pos + seedRange.length - 1
        return if (seedRange.pos <= point && point < end) {
            listOf(
                Seed(seed.id, (seed.chain + Range(seedRange.pos, point - seedRange.pos + 1)).toMutableList()),
                Seed(seed.id, (seed.chain + Range(point + 1, end - point)).toMutableList()),
            )
        } else {
            listOf(seed)
        }
    }

    private fun splitL(seed: Seed, point: Long): List<Seed> {
        val seedRange = seed.chain.last()
        val end = seedRange.pos + seedRange.length - 1
        return if (seedRange.pos < point && point <= end) {
            listOf(
                Seed(seed.id, (seed.chain + Range(seedRange.pos, point - seedRange.pos)).toMutableList()),
                Seed(seed.id, (seed.chain + Range(point, end - point + 1)).toMutableList()),
            )
        } else {
            listOf(seed)
        }
    }

    private fun applyMappings(seeds: List<Seed>, mappings: List<MappingEntry>): List<Seed> {
        val sorted = seeds.sortedBy { it.chain.last().pos }
        val seedIterator = sorted.iterator()
        if (!seedIterator.hasNext()) return seeds
        var seed = seedIterator.next()
        for (entry in mappings) {
            val start = entry.from
            val end = entry.from + entry.length - 1
            while (true) {
                val range = seed.chain.last()
                val position = range.pos
                seed = when {
                    position < start -> {
                        seed.chain.add(Range(position, range.length)) // add the same position
                        if (!seedIterator.hasNext()) return seeds
                        seedIterator.next()
                    }

                    position <= end -> {
                        seed.chain.add(Range(position - start + entry.to, range.length)) // add new position
                        if (!seedIterator.hasNext()) return seeds
                        seedIterator.next()
                    }

                    else -> break // skip mapping
                }
            }
        }
        seed.chain.add(seed.chain.last())
        while (seedIterator.hasNext()) {
            seed = seedIterator.next()
            seed.chain.add(seed.chain.last())
        }
        return seeds
    }

    private fun parseInput(): Pair<List<Long>, List<MappingTable>> {
        val seeds = mutableListOf<Long>()
        val mappingTables = mutableListOf<MappingTable>()
        var currentMappings = mutableListOf<MappingEntry>()
        consumeInput { index, line ->
            if (index == 0) {
                tokenize(line.substring(7)).forEach { seeds.add(it.toLong()) }
            }
            if (line.length > 1) {
                if (line[0].isDigit()) {
                    val tokens = tokenize(line)
                    currentMappings.add(MappingEntry(tokens[0].toLong(), tokens[1].toLong(), tokens[2].toLong()))
                } else if (currentMappings.isNotEmpty()) {
                    mappingTables.add(MappingTable(currentMappings.sortedBy { it.from }))
                    currentMappings = mutableListOf()
                }
            }
        }
        if (currentMappings.isNotEmpty()) {
            mappingTables.add(MappingTable(currentMappings.sortedBy { it.from }))
        }
        return seeds to mappingTables
    }

    private fun testSplit() {
        val test = Seed(777L, mutableListOf(Range(239, 400)))
        splitR(test, 300).forEach {
            println(it.chain.last())
        }
        splitL(test, 300).forEach {
            println(it.chain.last())
        }
    }

    private fun tokenize(text: String): List<String> {
        return text.split(' ').filter { it.isNotEmpty() }
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-05.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
