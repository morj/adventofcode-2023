package dev.morj.adv23.day24

import kotlin.math.abs
import kotlin.math.roundToLong

object Main2 {
    private const val A = 'A'.code
    private const val EPSILON = 1e-10
    private const val EPSILON_ROUGH = 1e-1

    @JvmStatic
    fun main(args: Array<String>) {
        val input = mutableListOf<Hailstone>()
        consumeInput { index, line ->
            val parts = line.split("@").map { it.replace(" ", "") }
            val c = parts[0].split(",").map { it.toLong() }
            val v = parts[1].split(",").map { it.toLong() }
            input.add(Hailstone(Char(A + index), c[0], c[1], c[2], v[0], v[1], v[2]))
        }
        val reFramed = reFrame(input, input[0])
        val direction = findDirection(reFramed)
        val start = input[0].p1 - direction * (2 * 10673400863.toDouble())
        println("direction $direction")
        val intersections = reFramed.filter { it !== reFramed[0] }.map { other ->
            val (tStart, tOther) = intersect(start, direction, other.p1, other.v)
            val int1 = start + direction * tStart
            val int2 = other.p1 + other.v * tOther
            require((int2 - int1).isSmall) // sanity check
            other to tOther.roundToLong()
        }.sortedBy { it.second }
        val (h0, t0) = intersections[77]
        val (h1, t1) = intersections[78]
        val target0 = h0.p1 + h0.v * t0.toDouble()
        val distance = (h1.p1 + h1.v * t1.toDouble()) - target0
        val speed = distance / (t1 - t0).toDouble()
        println("speed: $speed")
        val actualStart = target0 - speed * t0.toDouble()
        println("actual start: $actualStart")
        val x = actualStart.x.roundToLong()
        val y = actualStart.y.roundToLong()
        val z = actualStart.z.roundToLong()
        println(x + y + z)
    }

    private fun findDirection(stones: List<Hailstone>): V {
        val start = stones[0]
        val planes = stones.filter { it !== start }.map {
            val v1 = it.p1 - start.p1
            val v2 = it.v
            cross(v1, v2)
        }
        val normals = mutableListOf<V>()
        planes.zipWithNext { a, b -> normals.add(cross(a, b)) }
        val directions = normals.map { it * 125.toDouble() / it.x }
        // directions.forEach { println(it) }
        return directions[0]
    }

    private fun reFrame(input: MutableList<Hailstone>, reference: Hailstone): List<Hailstone> {
        // change the frame of reference
        val stones = input.map {
            it.copy(vx = it.vx - reference.vx, vy = it.vy - reference.vy, vz = it.vz - reference.vz)
        }
        return stones
    }

    // https://paulbourke.net/geometry/pointlineplane/
    private fun intersect(p1: V, v1: V, p3: V, v2: V): Pair<Double, Double> {
        val p13 = p1 - p3

        require(!v1.isZero)
        require(!v2.isZero)

        val d1343 = dot(p13, v2)
        val d4321 = dot(v2, v1)
        val d1321 = dot(p13, v1)
        val d4343 = dot(v2, v2)
        val d2121 = dot(v1, v1)

        val d = d2121 * d4343 - d4321 * d4321
        require(abs(d) > EPSILON)
        val n = d1343 * d4321 - d1321 * d4343

        val mua = n / d
        val mub = (d1343 + d4321 * (mua)) / d4343

        val t1 = abs(mua - mua.roundToLong())
        val t2 = abs(mub - mub.roundToLong())
        require(t1 < EPSILON_ROUGH)
        require(t2 < EPSILON_ROUGH)

        return mua to mub
    }

    data class Hailstone(
        val id: Char,
        val x: Long, val y: Long, val z: Long,
        val vx: Long, val vy: Long, val vz: Long
    ) {
        val v: V get() = V(vx.toDouble(), vy.toDouble(), vz.toDouble())
        val p1: V get() = V(x.toDouble(), y.toDouble(), z.toDouble())
    }

    data class V(val x: Double, val y: Double, val z: Double) {
        operator fun minus(t: V): V {
            return V(x - t.x, y - t.y, z - t.z)
        }

        operator fun plus(t: V): V {
            return V(x + t.x, y + t.y, z + t.z)
        }

        operator fun times(s: Double): V {
            return V(x * s, y * s, z * s)
        }

        operator fun div(s: Double): V {
            return V(x / s, y / s, z / s)
        }

        val isZero: Boolean get() = abs(x) < EPSILON && abs(y) < EPSILON && abs(z) < EPSILON
        val isSmall: Boolean get() = abs(x) < 1 && abs(y) < 1 && abs(z) < 1
    }

    private fun dot(a: V, b: V): Double {
        return a.x * b.x + a.y * b.y + a.z * b.z
    }

    private fun cross(a: V, b: V): V {
        return V(a.y * b.z - b.y * a.z, a.z * b.x - b.z * a.x, a.x * b.y - b.x * a.y)
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
