package dev.morj.adv23.day15

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val inputs = mutableListOf<String>()
        consumeInput { _, s ->
            inputs.addAll(s.split(","))
        }
        var sum = 0
        inputs.forEach {
            val h = hash(it)
            println(h)
            sum += h
        }
        println("total: $sum")
    }

    private fun hash(s: String): Int {
        var result = 0
        s.forEach {
            result += it.code
            result *= 17
            result %= 256
            println(result)
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
