package aoc2022

import utils.*
import java.rmi.UnexpectedException

internal class Day06(val lines: List<String>) {
  init {
    lines.forEach { println(it) }
  }

  fun part1(): Any {
    return lines[0].windowed(4).indexOfFirst { it.toSet().size == 4 } + 4
  }

  fun part2(): Any {
    return lines[0].windowed(14).indexOfFirst { it.toSet().size == 14 } + 14
  }

  fun solve(targetUniqueCount: Int): Any {
    val message = lines.first()
    val charMask = IntArray(26) { -1 }
    var uniqueCount = 0

    fun toIndex(c: Char) : Int {
      return c.code - 'a'.code
    }

    (message.indices).forEach { currIndex ->
      if (currIndex >= targetUniqueCount) {
        val backMarkerIndex = currIndex - targetUniqueCount
        val backMarker = toIndex(message[backMarkerIndex])
        // Decrement the pointer at the back of the window if
        // the last time that value was seen is the back marker index
        if (charMask[backMarker] == backMarkerIndex) {
          uniqueCount--
          charMask[backMarker] = -1
        }
      }

      val currCharBucket = toIndex(message[currIndex])

      // Check if this letter is new. Increment and check the counter.
      // Return if at target
      if (charMask[currCharBucket] == -1) {
        uniqueCount++
        if (uniqueCount == targetUniqueCount) {
          return currIndex + 1
        }
      }

      charMask[currCharBucket] = currIndex
    }

    throw UnexpectedException("This should not happen")
  }

  companion object {
    fun runDay() {
      val day = "06".toInt()

      val todayTest = Day06(readInput(day, 2022, true))
      execute(todayTest::part1, "Day[Test] $day: pt 1")

      val today = Day06(readInput(day, 2022))
      execute(today::part1, "Day $day: pt 1", 1655)

      execute(todayTest::part2, "Day[Test] $day: pt 2")
      execute(today::part2, "Day $day: pt 2", 2665)

      execute({ today.solve(4) }, "Day $day: pt 3", 1655)
      execute({ today.solve(14) }, "Day $day: pt 3", 2665)
    }

  }
}

fun main() {
  Day06.runDay()
}