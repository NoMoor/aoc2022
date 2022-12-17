package aoc2022

import utils.*
import utils.Coord.Companion.get
import utils.Coord.Companion.set
import utils.CoordRange.Companion.toCoordRange

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
    var currentTime = 0
    var height = 0

    repeat(2022) {
      // Simulate rock
      val spawnLoc = Coord.xy(2, height + 3)
      var shape = shapes[nextShape % shapes.size].map { it + spawnLoc }.toList()

      while (true) {
        // Simulate left / right
        val goLeft = goLefts[currentTime % goLefts.size]
        currentTime++
        if (goLeft) {
          if (isAllowed(shape, Coord.LEFT, grid)) {
            shape = shape.map { it + Coord.LEFT }
          }
        } else if (isAllowed(shape, Coord.RIGHT, grid)) {
          shape = shape.map { it + Coord.RIGHT }
        }

        if (!isAllowed(shape, Coord.DOWN, grid)) {
          break
        }
        shape = shape.map { it + Coord.DOWN }
      }

      // Set the shape into the grid and update the height of the stack.
      shape.forEach { grid[it] = true }
      height = grid.indexOfLast { it.contains(true) } + 1

      nextShape += 1
    }

    return grid.indexOfLast { it.contains(true) }.toLong() + 1
  }

  /** Returns true if moving the given shape by the relative movement would be legal. */
  private fun isAllowed(shape: List<Coord>, movement: Coord, grid: MutableList<MutableList<Boolean>>): Boolean {
    val gridSize = grid.toCoordRange()
    return shape.map { it + movement }.all { it in gridSize && !grid[it] }
  }

  private fun printGrid(grid: List<List<Boolean>>, height: Int = grid.size, label: String = "") {
    println(label)
    grid.subList(0, height + 1).reversed().forEach {
      println(it.map { if (it) '#' else '.' }.joinToString(""))
    }
    println()
  }

  fun part2(): Long {
    val windCycleLength = goLefts.size

    // A set of wind cycles that we've already seen. Used to determine which wind numbers are in the cycle.
    val seenWindCycles = mutableSetOf<Int>()
    // Record the heights of the stack for a full cycle to compute the final height of the stack since the final height
    // will include a partial cycle and the 'non-cycle' part of the stack before the cycle stabilizes.
    val heightRecord = mutableListOf<Int>()
    // The wind element indicating the start of the cycle. -1 means unset.
    var windSyncNum = -1
    // The rock number indicating the start of the cycle. -1 means unset.
    var rockCycleNum = -1

    val grid = MutableList(100_000) { MutableList(7) { false } }
    var currentRockNum = 0
    var time = 0
    var height = 0

    repeat(100000) {
      // Simulate rock
      val spawnLoc = Coord.xy(2, height + 3)
      var shape = shapes[currentRockNum % shapes.size].map { it + spawnLoc }.toList()

      while (true) {
        // Simulate left / right
        val goLeft = goLefts[time % windCycleLength]
        time++
        if (goLeft) {
          if (isAllowed(shape, Coord.LEFT, grid)) {
            shape = shape.map { it + Coord.LEFT }
          }
        } else if (isAllowed(shape, Coord.RIGHT, grid)) {
          shape = shape.map { it + Coord.RIGHT }
        }

        if (!isAllowed(shape, Coord.DOWN, grid)) {
          break
        }
        shape = shape.map { it + Coord.DOWN }
      }

      // Set the shape into the grid and update the height of the stack.
      shape.forEach { grid[it] = true }
      height = grid.indexOfLast { it.contains(true) } + 1

      currentRockNum += 1

      // Find a cycle in block placement by looking at when we are about to place the first block.
      // Wait for wind cycles to stablize and then track what one stable cycle looks like.
      if (currentRockNum % shapes.size == 0) {
        if (windSyncNum == -1) {
          // Wait until we've seen the same wind position twice to be sure that it's a cycle
          if (!seenWindCycles.add(time % windCycleLength)) {
            windSyncNum = time % windCycleLength
            rockCycleNum = currentRockNum
          }
        } else if (time % windCycleLength == windSyncNum) {
          // If we have cycle data and this is in sync with the cycle
          val cycleHeight = heightRecord.last() - heightRecord.first()
          val cycleLength = currentRockNum - rockCycleNum

          val rocksRemaining = 1000000000000 - rockCycleNum
          val cyclesRemaining = rocksRemaining / cycleLength
          val extraRocks = (rocksRemaining % cycleLength).toInt()

          return (cyclesRemaining * cycleHeight) + heightRecord[extraRocks]
        }
      }
      if (windSyncNum != -1) {
        heightRecord.add(height)
      }
    }

    throw RuntimeException("TSNH!")
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
