package aoc2022

import utils.*
import kotlin.math.max

private class Day25(val lines: List<String>) {

  /** Maps from SNAFU character to decimal digit. */
  fun map(c: Char) : Int {
    return when (c) {
      '-' -> -1
      '=' -> -2
      else -> c.digitToInt()
    }
  }

  /** Maps from decimal digit to SNAFU character. */
  fun map(i: Int) : Char {
    return when (i) {
      -1 -> '-'
      -2 -> '='
      else -> i.digitToChar()
    }
  }

  fun snafuToDec(value: String): Long {
    return value.mapIndexed { index, c ->
      val v = map(c)
      val exp = value.length - index - 1
      (Math.pow(5.0, exp.toDouble()) * v).toLong()
    }.sum()
  }

  fun decToSnafu(value: Long): String {
    var s = ""
    var modValue = value
    while (modValue != 0L) {
      var m5 = modValue % 5
      if (m5 > 2) {
        m5 -= 5
      }
      s = map(m5.toInt()) + s
      modValue -= m5
      modValue /= 5
    }

    return s
  }

  /** Sums SNAFU numbers digit by digit. */
  fun sum(a: String, b: String) : String {
    val aa = a.reversed()
    val bb = b.reversed()

    var result = ""
    var cc = 0
    for (i in 0 .. max(a.length, b.length)) {
      val ac = map(aa.getOrElse(i) { '0' })
      val bc = map(bb.getOrElse(i) { '0' })
      val abc = ac + bc + cc

      var abcMod = abc % 5
      if (abcMod > 2) {
        abcMod -= 5
      } else if (abcMod < -2) {
        abcMod += 5
      }

      result = map(abcMod) + result
      cc = (abc - abcMod) / 5
    }

    return result.trimStart { it == '0' }
  }

  fun part1DecToSnafuToDec(): String {
        val decValue = lines.map {
      snafuToDec(it)
    }.sum()

    return decToSnafu(decValue)
  }

  fun part1(): String {
    return lines.reduce(this::sum)
  }
}

fun main() {
  val day = "25".toInt()

  val todayTest = Day25(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1", "2=-1=0")

  val today = Day25(readInput(day, 2022))
  execute(today::part1DecToSnafuToDec, "Day $day: pt 1", "2-2=21=0021=-02-1=-0")
  execute(today::part1, "Day $day: pt 1", "2-2=21=0021=-02-1=-0")
}
