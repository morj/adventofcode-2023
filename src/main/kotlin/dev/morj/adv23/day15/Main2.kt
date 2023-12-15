package dev.morj.adv23.day15

object Main2 {

    data class Lens(val label: String, val focalLength: Int) {
        override fun toString() = "[$label $focalLength]"
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val inputs = mutableListOf<String>()
        consumeInput { _, s ->
            inputs.addAll(s.split(","))
        }
        val boxes = mutableListOf<MutableList<Lens>>()
        (1..256).forEach { boxes.add(mutableListOf()) }
        inputs.forEach { line ->
            if (line.contains("=")) {
                val tokens = line.split("=")
                val label = tokens[0]
                val strength = tokens[1].toInt()
                val box = boxes[hash(label)]
                val existing = box.indexOfFirst { it.label == label }
                if (existing >= 0) {
                    box[existing] = Lens(label, strength) // replace
                } else {
                    box.add(Lens(label, strength))
                }
            } else {
                val label = line.substringBefore("-")
                boxes[hash(label)].removeIf { it.label == label }
            }
            //debug(line, boxes)
        }
        var sum = 0L
        boxes.forEachIndexed { box, lenses ->
            lenses.forEachIndexed { offset, lens ->
                val power = (box + 1) * (offset + 1) * lens.focalLength
                println("power: $power")
                sum += power
            }
        }
        println("total: $sum")
    }

    private fun debug(line: String, boxes: MutableList<MutableList<Lens>>) {
        println("After \"$line\":")
        boxes.forEachIndexed { index, box ->
            if (box.isNotEmpty()) {
                println(box.joinToString(prefix = "Box $index: ", separator = " "))
            }
        }
        println()
    }

    private fun hash(s: String): Int {
        var result = 0
        s.forEach {
            result += it.code
            result *= 17
            result %= 256
        }
        return result
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-15.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
