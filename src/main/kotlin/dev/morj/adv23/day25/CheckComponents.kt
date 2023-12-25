package dev.morj.adv23.day25

object CheckComponents {
    @JvmStatic
    fun main(args: Array<String>) {
        val edges = mutableMapOf<String, MutableSet<String>>()
        val allEdgesSet = mutableSetOf<Pair<String, String>>()
        consumeInput { _, line ->
            val key = line.substringBefore(':')
            val tokens = line.substringAfter(": ").split(' ')
            val pairs = tokens.map { key to it } + tokens.map { it to key }
            allEdgesSet.addAll(pairs)
            pairs.forEach { (from, to) ->
                edges[from]?.add(to) ?: run {
                    edges[from] = mutableSetOf(to)
                }
            }
        }
        // val removeEdges = mutableSetOf("hfx" to "pzl", "bvb" to "cmg", "nvd" to "jqt")
        val removeEdges = mutableSetOf("htb" to "bbg", "dlk" to "pjj", "htj" to "pcc")
        removeEdges.toList().forEach { (from, to) -> removeEdges.add(to to from) }
        val allVisited = mutableSetOf<String>()
        val components = mutableListOf<Int>()
        edges.keys.forEach { v ->
            if (!allVisited.contains(v)) {
                val visited = mutableSetOf<String>()
                dfs(v, visited, removeEdges, edges)
                components.add(visited.size)
                allVisited.addAll(visited)
            }
        }
        require(components.size == 2)
        println(components.first() * components.last())
    }

    private fun dfs(
        from: String,
        visited: MutableSet<String>,
        removed: MutableSet<Pair<String, String>>,
        edges: MutableMap<String, MutableSet<String>>
    ) {
        if (!visited.contains(from)) {
            visited.add(from)
            edges[from]?.forEach { to ->
                if (!removed.contains(from to to)) {
                    dfs(to, visited, removed, edges)
                }
            }
        }
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-25.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
