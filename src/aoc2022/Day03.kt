package aoc2022

import utils.*

private class Day03(lines: List<String>) {
  init { lines.forEach { println(it) } }

  private val lines = lines
    .map {
      val s = it.length / 2
      listOf(it.toList().subList(0, s), it.toList().subList(s, it.length))
    }.map {
      it[0].intersect(it[1]).first()
    }.map {
      score(it)
    }

  private val lines2 = lines.windowed(3, step = 3)
    .map {
      it.map { it.toMutableList() }.reduce { acc, chars -> acc.intersect(chars).toMutableList() }.first()
    }.map {
      score(it)
    }

  fun score(it: Char): Int {
    return if (it.isUpperCase()) it.code - 'A'.code + 27 else it.code - 'a'.code + 1
  }

  fun part1(): Int {
    return lines.sum()
  }

  fun part2(): Int {
    return lines2.sum()
  }

  companion object {
    fun runDay() {
      val day = 3

      val todayTest = Day03(readInput(day, 2022, true))
      execute(todayTest::part1, "Day[Test] $day: pt 1", 157)

      val today = Day03(readInput(day, 2022))
      execute(today::part1, "Day $day: pt 1", 7908)

      execute(todayTest::part2, "Day[Test] $day: pt 2", 70)
      execute(today::part2, "Day $day: pt 2", 2838)
    }
  }
}

fun main() {
  Day03.runDay()
}