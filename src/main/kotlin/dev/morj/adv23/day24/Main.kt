package dev.morj.adv23.day24

object Main {
    private const val A = 'A'.code

    private const val MIN = 200000000000000L
    // private const val MIN = 7L

    private const val MAX = 400000000000000L
    // private const val MAX = 27L

    @JvmStatic
    fun main(args: Array<String>) {
        val hails = mutableListOf<Hailstone>()
        consumeInput { index, line ->
            val parts = line.split("@").map { it.replace(" ", "") }
            val c = parts[0].split(",").map { it.toLong() }
            val v = parts[1].split(",").map { it.toLong() }
            hails.add(Hailstone(Char(A + index), c[0], c[1], c[2], v[0], v[1], v[2]))
        }
        // hails.forEach { println(it) }
        var total = 0L
        hails.forEach { a ->
            hails.forEach { b ->
                if (a != b) {
                    println(a)
                    println(b)
                    intersect(a, b)?.let {
                        total++
                        println(it)
                    }
                    println()
                }
            }
        }
        println("total intersections: ${total / 2}")
    }

    private fun intersect(p: Hailstone, q: Hailstone): Pair<Double, Double>? {
        val (a, c) = p.eq
        val (b, d) = q.eq
        if (a == b) return null
        val diff = a - b
        val x = (d - c) / diff
        val y = (a * (d - c) / diff) + c
        if (x < MIN || x > MAX || y < MIN || y > MAX) return null
        if (p.isAfter(x, y) || q.isAfter(x, y)) return null
        return x to y
    }

    data class Hailstone(val id: Char, val x: Long, val y: Long, val z: Long, val vx: Long, val vy: Long, val vz: Long) {
        fun isAfter(x0: Double, y0: Double): Boolean {
            if (vx > 0 && x > x0) return true
            if (vy > 0 && y > y0) return true
            if (vx < 0 && x < x0) return true
            if (vy < 0 && y < y0) return true
            return false
        }

        val eq: Pair<Double, Double>
            get() {
                require(vx != 0L)
                require(vy != 0L)
                val a = vy.toDouble() / vx
                val b = y - x.toDouble() * a
                return a to b
            }
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-24.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
