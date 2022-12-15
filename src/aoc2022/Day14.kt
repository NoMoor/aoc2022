package aoc2022

import utils.*
import utils.Coord.Companion.get
import utils.Coord.Companion.set
import kotlin.math.max
import kotlin.math.min

private class Day14(val lines: List<String>) {
  val rocks: List<List<Coord>> = lines
    .map {
      it.split(" -> ")
        .map {
          val (x, y) = it.split(",").map { it.toInt() }

          Coord.xy(x, y)
        }
    }

  val down = Coord.xy(0, 1)
  val downLeft = Coord.xy(-1, 1)
  val downRight = Coord.xy(1, 1)

  private fun setRockLines(grid: MutableList<MutableList<Boolean>>) {
    rocks.forEach {
      it.zipWithNext { a, b ->
        val xRange = min(a.x, b.x)..max(a.x, b.x)
        val yRange = min(a.y, b.y)..max(a.y, b.y)

        for (x in xRange) {
          for (y in yRange) {
            grid[y][x] = false
          }
        }
      }
    }
  }

  fun part1(): Any {
    // Grid where all true means that spot is open.
    val grid = MutableList(1000) { MutableList(1000) { true } }
    setRockLines(grid)

    val maxY = rocks.flatMap { it }.maxOf { it.y }

    var sandCount = 0
    while (true) {
      // Simulate sand falling.
      var pos = Coord.xy(500, 0)
      while (true) {
        if (grid[pos + down]) {
          pos += down
        } else if (grid[pos + downLeft]) {
          pos += downLeft
        } else if (grid[pos + downRight]) {
          pos += downRight
        } else {
          // Sand can't move
          grid[pos] = false
          break
        }

        // Check if the sand is falling below the last rocks.
        if (pos.y > maxY) {
          return sandCount
        }
      }

      sandCount++
    }
  }

  fun part2(): Any {
    // Grid where all true means that spot is open.
    val grid = MutableList(1000) { MutableList(1000) { true } }
    setRockLines(grid)

    // Draw the floor
    val floor = rocks.flatMap { it }.maxOf { it.y } + 2
    grid[0].indices.forEach { x -> grid[floor][x] = false }

    var sandCount = 0
    while (true) {
      sandCount++

      var pos = Coord.xy(500, 0)
      // simulate sand
      while (true) {
        if (grid[pos + down]) { // We can move
          pos += down
        } else if (grid[pos + downLeft]) { // We can move
          pos += downLeft
        } else if (grid[pos + downRight]) { // We can move
          pos += downRight
        } else {
          grid[pos] = false

          // Sand plugs the source
          if (pos.y <= 0) {
            return sandCount
          }
          break
        }
      }
    }
  }
}

fun main() {
  val day = "14".toInt()

  val todayTest = Day14(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1", 24)

  val today = Day14(readInput(day, 2022))
  execute(today::part1, "Day $day: pt 1", 873)

  execute(todayTest::part2, "Day[Test] $day: pt 2", 93)
  execute(today::part2, "Day $day: pt 2", 24813)
}
