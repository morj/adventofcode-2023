package dev.morj.adv23.day22

object Main {
    const val A = 'A'.code

    @JvmStatic
    fun main(args: Array<String>) {
        val bricks = mutableListOf<Brick>()
        var width = 0
        var height = 0
        consumeInput { index, line ->
            val tokens = line.split('~')
            val from = tokens.first().split(',').map { it.toInt() }
            val to = tokens.last().split(',').map { it.toInt() }
            val brick = Brick(Char(A + index), 0, from[0], from[1], from[2], to[0], to[1], to[2])
            require(brick.x1 <= brick.x2 && brick.y1 <= brick.y2 && brick.z1 <= brick.z2)
            width = width.coerceAtLeast(brick.x2 + 1)
            height = height.coerceAtLeast(brick.y2 + 1)
            bricks.add(brick)
        }
        println("width $width height $height")
        bricks.sortBy { it.z1 }
        val layers = mutableListOf<MutableMap<LayerPoint, Brick>>()
        // layoutBricks(bricks, layers)
        processBricks(bricks, layers) // compute initial Z values
        var result = 0L
        for (brick in bricks) {
            val source = bricks.filter { it != brick }
            val updated = source.map { it.copy() }
            layoutBricks(updated, mutableListOf())
            var fall = 0
            source.forEachIndexed { index, candidate ->
                if (updated[index].realZ != candidate.realZ) {
                    fall++
                }
            }
            println("brick ${brick.id} will cause $fall bricks to fall")
            result += fall
        }
        println(result)
    }

    private fun processBricks(bricks: List<Brick>, layers: MutableList<MutableMap<LayerPoint, Brick>>) {
        layoutBricks(bricks, layers)
        val supportedBy = bricks.associateWith { supportedBy(layers, it) }
        bricks.forEach { brick ->
            println("$brick ${brick.axis} supported by ${supportedBy[brick]?.joinToString { it.id.toString() }}")
        }
        val supports = mutableMapOf<Brick, MutableSet<Brick>>()
        supportedBy.forEach { (brick, sup) ->
            sup.forEach { support ->
                supports[support]?.add(brick) ?: run {
                    supports[support] = mutableSetOf(brick)
                }
            }
        }
        var result = 0
        bricks.forEach { brick ->
            val ourSupports = supports[brick]
            if (ourSupports.isNullOrEmpty() || ourSupports.all { supportedBy[it]!!.size > 1 }) {
                println("can be disintegrated: ${brick.id}")
                result++
            }
            println("$brick ${brick.axis} supports ${ourSupports?.joinToString { it.id.toString() }}")
        }
        println("disintegration total: $result")
    }

    private fun layoutBricks(bricks: List<Brick>, layers: MutableList<MutableMap<LayerPoint, Brick>>) {
        bricks.forEach { brick ->
            val axis = brick.axis
            val points = brick.points
            val start = points.first()
            var targetZ = start.z.coerceAtMost(layers.size)
            if (axis == Axis.Z) {
                val key = LayerPoint(start.x, start.y)
                while (targetZ > 0) {
                    if (layers[targetZ - 1].containsKey(key)) {
                        break
                    }
                    targetZ--
                }
            } else {
                while (targetZ > 0) {
                    if (points.any { layers[targetZ - 1].containsKey(LayerPoint(it.x, it.y)) }) {
                        break
                    }
                    targetZ--
                }
            }
            brick.realZ = targetZ
            points.forEach {
                val z = targetZ - start.z + it.z
                require(layers.size >= z)
                if (layers.size == z) {
                    layers.add(hashMapOf())
                }
                layers[z][LayerPoint(it.x, it.y)] = brick
            }
        }
    }

    private fun supportedBy(layers: MutableList<MutableMap<LayerPoint, Brick>>, brick: Brick): Collection<Brick> {
        val z = brick.realZ
        if (z == 0) {
            return emptyList()
        }
        return if (brick.axis == Axis.Z) {
            val supportingBrick = layers[z - 1][LayerPoint(brick.x1, brick.y1)] ?: run {
                error("brick $brick is floating")
            }
            listOf(supportingBrick)
        } else {
            hashSetOf<Brick>().also { result ->
                brick.points.forEach { p ->
                    layers[z - 1][LayerPoint(p.x, p.y)]?.let { result.add(it) }
                }
                if (result.isEmpty()) {
                    error("brick $brick is floating")
                }
            }
        }
    }

    data class Brick(
        val id: Char,
        var realZ: Int,
        val x1: Int, val y1: Int, val z1: Int,
        val x2: Int, val y2: Int, val z2: Int,
    ) {
        val axis: Axis
            get() = when {
                x1 == x2 && y1 == y2 -> Axis.Z
                x1 == x2 && z1 == z2 -> Axis.Y
                else -> Axis.X
            }

        val points: List<Point>
            get() = when (axis) {
                Axis.X -> (x1..x2).map { x -> Point(x, y1, z1) }
                Axis.Y -> (y1..y2).map { y -> Point(x1, y, z1) }
                Axis.Z -> (z1..z2).map { z -> Point(x1, y1, z) }
            }
    }

    data class LayerPoint(val x: Int, val y: Int)
    data class Point(val x: Int, val y: Int, val z: Int)

    enum class Axis { X, Y, Z }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-22.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
