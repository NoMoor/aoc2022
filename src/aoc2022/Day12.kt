package aoc2022

import utils.*
import utils.Coord.Companion.get
import java.rmi.UnexpectedException
import java.util.*

private class Day12(val lines: List<String>) {

  fun getHeight(c: Char) : Char {
    if (c == 'S') {
      return 'a'
    } else if (c == 'E') {
      return 'z'
    }
    return c
  }

  data class StepCount(val loc: Coord, val steps: Int) {}

  fun part1(): Any {
    val heightMap = lines.map { it.toList() }.toList()
    val minStepsToLoc = mutableMapOf<Coord, Int>()
    val pq = PriorityQueue<StepCount>(compareBy { it.steps })

    val height = heightMap.size
    val width = heightMap[0].size

    val start = find(heightMap, 'S')
    pq.add(StepCount(start, 0))

    while (pq.isNotEmpty()) {
      val next = pq.poll()

      // We've been here before
      if (minStepsToLoc[next.loc] != null) {
        continue
      } else {
        // Mark this spot as visited.
        minStepsToLoc[next.loc] = next.steps
      }

      // Check if this is the end.
      val c = heightMap[next.loc]
      if (c == 'E') {
        return next.steps
      }

      val cHeight = getHeight(c)
      val neighbors = next.loc
        .neighborsBounded(0 until width, 0 until height, "+")

      for (n in neighbors) {
        val cnHeight = getHeight(heightMap[n])
        if ((cnHeight.code - cHeight.code) <= 1) {
          // We can go here
          pq.add(StepCount(n, next.steps + 1))
        }
      }
    }

    throw UnexpectedException("We didn't find an answer.")
  }

  /** Finds the given character on the map. */
  fun find(map: List<List<Char>>, char: Char): Coord {
    map.indices.forEach { r ->
      map[r].indices.forEach { c ->
        if (map[r][c] == char) {
          return Coord.rc(r, c)
        }
      }
    }
    throw UnexpectedException("Oops")
  }

  fun part2(): Any {
    val heightMap = lines.map { it.toList() }.toList()
    val minStepsToLoc = mutableMapOf<Coord, Int>()
    val pq = PriorityQueue<StepCount>(compareBy { it.steps })

    val height = heightMap.size
    val width = heightMap[0].size

    val end = find(heightMap, 'E')
    pq.add(StepCount(end, 0))

    while (pq.isNotEmpty()) {
      val curr = pq.poll()

      // We've been here before
      if (minStepsToLoc[curr.loc] != null) {
        continue
      } else {
        minStepsToLoc[curr.loc] = curr.steps
      }

      // Check if this is the end.
      val currHeight = getHeight(heightMap[curr.loc])
      if (currHeight == 'a') {
        return curr.steps
      }

      val neighbors = curr.loc
        .neighborsBounded(0 until width, 0 until height, "+")

      for (neighbor in neighbors) {
        val neighborHeight = getHeight(heightMap[neighbor])
        if ((neighborHeight.code - currHeight.code) >= -1) {
          pq.add(StepCount(neighbor, curr.steps + 1))
        }
      }
    }

    throw UnexpectedException("Oops")
  }
}

fun main() {
  val day = "12".toInt()

  val todayTest = Day12(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1", 31)

  val today = Day12(readInput(day, 2022))
  execute(today::part1, "Day $day: pt 1", 380)

  execute(todayTest::part2, "Day[Test] $day: pt 2", 29)
  execute(today::part2, "Day $day: pt 2", 375)
}
