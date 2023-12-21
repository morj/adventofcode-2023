package dev.morj.adv23.day21

import java.io.File

object Mul {
    const val REP_X = 9
    const val REP_Y = 9

    @JvmStatic
    fun main(args: Array<String>) {
        File("day-239.txt").printWriter().use { pw ->
            repeat(REP_Y / 2) {
                consumeInput { x, line ->
                    val r = line.replace('S', '.')
                    repeat(REP_X) {
                        pw.print(r)
                    }
                    pw.println()
                }
            }
            consumeInput { x, line ->
                val r = line.replace('S', '.')
                repeat(REP_X / 2) {
                    pw.print(r)
                }
                pw.print(line)
                repeat(REP_X / 2) {
                    pw.print(r)
                }
                pw.println()
            }
            repeat(REP_Y / 2) {
                consumeInput { x, line ->
                    val r = line.replace('S', '.')
                    repeat(REP_X) {
                        pw.print(r)
                    }
                    pw.println()
                }
            }
        }
    }

    private fun consumeInput(action: (Int, String) -> Unit) {
        javaClass.classLoader.getResource("day-21.txt")!!.openStream().use { stream ->
            var index = 0
            stream.bufferedReader().forEachLine {
                action(index, it)
                index++
            }
        }
    }
}
