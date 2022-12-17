package aoc2022

import utils.*
import utils.Coord.Companion.get
import utils.Coord.Companion.set

private class Day17(val lines: List<String>) {
  val goLefts = lines[0].toList().map { it == '<' }

  // 2 units from left wall
  // 3 units from the ground or highest point

  val hline = listOf(Coord.xy(0, 0), Coord.xy(1, 0), Coord.xy(2, 0), Coord.xy(3, 0))
  val vline = listOf(Coord.xy(0, 0), Coord.xy(0, 1), Coord.xy(0, 2), Coord.xy(0, 3))
  val box = listOf(Coord.xy(0, 0), Coord.xy(1, 0), Coord.xy(0, 1), Coord.xy(1, 1))
  val ell = listOf(Coord.xy(0, 0), Coord.xy(1, 0), Coord.xy(2, 0), Coord.xy(2, 1), Coord.xy(2, 2))
  val cross = listOf(Coord.xy(0, 1), Coord.xy(1, 0), Coord.xy(1, 1), Coord.xy(2, 1), Coord.xy(1, 2))
  val shapes = listOf(hline, cross, ell, vline, box)

  fun part1(): Long {
    val grid = MutableList(10_000) { MutableList(7) { false } }
    var nextShape = 0
    var time = 0
    var highestSpot = -1

    repeat(2022) {
      // Simulate rock
      val spawn_loc = Coord.xy(2, highestSpot + 4)
      var shape = shapes[nextShape % shapes.size].map { it + spawn_loc }.toList()
      // Spawn

      while (true) {
        // Simulate left / right
        val goLeft = goLefts[time % goLefts.size]
        time++
        if (goLeft) {
          if (isOpen(shape, Coord.LEFT, grid)) {
            shape = shape.map { it + Coord.LEFT }
          }
        } else if (isOpen(shape, Coord.RIGHT, grid)) {
          shape = shape.map { it + Coord.RIGHT }
        }

        // simulate down
        if (isOpen(shape, Coord.DOWN, grid)) {
          shape = shape.map { it + Coord.DOWN }
        } else {
          break
        }
      }

      shape.forEach { grid[it] = true }
      highestSpot = grid.indexOfLast { it.contains(true) }

      nextShape += 1
    }

    return grid.indexOfLast { it.contains(true) }.toLong() + 1
  }

  private fun isOpen(shape: List<Coord>, offset: Coord, grid: MutableList<MutableList<Boolean>>): Boolean {
    return shape.none {
      val spot = it + offset
      !(spot.x in 0 until grid[0].size) || !(spot.y in 0 until grid.size) || grid[spot]
    }
  }

  private fun printGrid(
    round: Int,
    highestSpot: Int,
    grid: MutableList<MutableList<Boolean>>
  ) {
    println("Round: $round")
    for (i in highestSpot downTo 0) {
      val row = grid[i].map { if (it) '#' else '.' }.joinToString("")
      println(row)
    }
    println()
  }

  fun part2(): Long {
    val windCycle = goLefts.size
    val shapeCycle = shapes.size

    val seenCycles = mutableSetOf<Int>()
    var cycleSyncNum: Int? = null

    val partialCycleHeights = mutableListOf<Int>()
    var ogCoord: Coord? = null
    var ogRockNum = -1

    val grid = MutableList(100_000) { MutableList(7) { false } }
    var rockCount = 0
    var time = 0
    var highestSpot = -1

    repeat(100000) {
      // Simulate rock
      val spawn_loc = Coord.xy(2, highestSpot + 4)
      var shape = shapes[rockCount % shapes.size].map { it + spawn_loc }.toList()
      // Spawn

      while (true) {
        // Simulate left / right
        val goLeft = goLefts[time % goLefts.size]
        time++
        if (goLeft) {
          if (isOpen(shape, Coord.LEFT, grid)) {
            shape = shape.map { it + Coord.LEFT }
          }
        } else if (isOpen(shape, Coord.RIGHT, grid)) {
          shape = shape.map { it + Coord.RIGHT }
        }

        if (isOpen(shape, Coord.DOWN, grid)) {
          shape = shape.map { it + Coord.DOWN }
        } else {
          break
        }
      }

      shape.forEach { grid[it] = true }
      highestSpot = grid.indexOfLast { it.contains(true) }

      // When the shape that's set is the last shape in the shape list
      if (rockCount % shapes.size == (shapeCycle - 1)) {
        // If we have cycle data and this is in sync with the cycle
        if (cycleSyncNum != null && time % windCycle == cycleSyncNum) {
          val cycleHeight = shape[0].y - ogCoord!!.y
          val cycleLength = rockCount - ogRockNum

          val rocksToJumpOver = 1000000000000 - ogRockNum
          val cyclesToJump = rocksToJumpOver / cycleLength
          val extraRocks = (rocksToJumpOver % cycleLength).toInt()

          return (cyclesToJump * cycleHeight) + partialCycleHeights[extraRocks]
        } else if (cycleSyncNum == null) {
          val windNum = time % windCycle
          if (!seenCycles.add(windNum)) {
            cycleSyncNum = windNum
            ogCoord = shape[0].copy()
            ogRockNum = rockCount

            println("Catching cycle $windNum")
          }
        }
      }

      if (ogCoord != null) {
        partialCycleHeights.add(highestSpot)
      }

      rockCount += 1
    }

    return lines.size.toLong()
  }
}

fun main() {
  val day = "17".toInt()

  val todayTest = Day17(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1")

  val today = Day17(readInput(day, 2022))
  execute(today::part1, "Day $day: pt 1")

  execute(todayTest::part2, "Day[Test] $day: pt 2", 1514285714288)
  execute(today::part2, "Day $day: pt 2", 1602881844347L)
}
