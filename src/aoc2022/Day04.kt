package aoc2022

import utils.*

private class Day04(lines: List<String>) {
  init { lines.forEach { println(it) } }

  private val lines = lines
    .map {
      val (a, b, c, d) = it.split('-', ',').map { it.toInt() }
      (a .. b) to (c .. d)
    }

  fun IntRange.contains(b: IntRange): Boolean {
    return this.first <= b.first && b.last <= this.last
  }

  fun IntRange.overlaps(b: IntRange): Boolean {
    return this.contains(b.first) || this.contains(b.last) || b.contains(this.first)
  }

  fun part1(): Int {

    return lines.count {
      if (it.first.contains(it.second)) {
        println("${it.first} contains ${it.second}")
        return@count true
      }

      if (it.second.contains(it.first)) {
        println("${it.second} contains ${it.first}")
        return@count true
      }

      return@count false
    }
  }

  fun part2(): Int {
    return lines.count {
      return@count it.first.overlaps(it.second)
    }
  }

  companion object {
    fun runDay() {
      val day = "04".toInt()

      val todayTest = Day04(readInput(day, 2022, true))
      execute(todayTest::part1, "Day[Test] $day: pt 1", 2)

      val today = Day04(readInput(day, 2022))
      execute(today::part1, "Day $day: pt 1", 599)

      execute(todayTest::part2, "Day[Test] $day: pt 2", 4)
      execute(today::part2, "Day $day: pt 2", 928)
    }
  }
}

fun main() {
  Day04.runDay()
}
