package aoc2022

import deepMap
import execute
import readInput
import splitBy

class Day01(lines: List<String>) {
  init { lines.forEach { println(it) } }

  val elves = lines.splitBy{ it.isEmpty() }.deepMap { it.toInt() }

  fun part1() : Any {
    return elves.maxOf { it.sum() }
  }

  fun part2() : Any {
    return elves.map { it.sum() }.sortedDescending().take(3).sum()
  }

  companion object {
    fun runDay() {
      val day = 1

      val todayTest = Day01(readInput(day, 2022, true))
      execute(todayTest::part1, "Day[Test] $day: pt 1", 24000)

      val today = Day01(readInput(day, 2022))
      execute(today::part1, "Day $day: pt 1", 71300)

      execute(todayTest::part2, "Day[Test] $day: pt 2", 45000)
      execute(today::part2, "Day $day: pt 2", 209691)
    }
  }
}

fun main() {
  Day01.runDay()
}
