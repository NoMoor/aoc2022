package aoc2022

import utils.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private class Day15(val lines: List<String>) {

  val sensors = lines.map { it.removePrefix("Sensor at ").allInts() }
    .map { Sensor(Coord.xy(it[0], it[1]), Coord.xy(it[2], it[3])) }
    .toList()

  data class Sensor(val sensorLoc: Coord, val beaconLoc: Coord) {
    fun range() : Int {
      return abs(sensorLoc.x - beaconLoc.x) + abs(sensorLoc.y - beaconLoc.y)
    }
  }

  fun part1(targetY: Int): Long {
    val coveredRanges = coveredRanges(targetY)

    val invalidLocations = coveredRanges.flatMap { it }.distinct().debug()
    val beacons = sensors.map { it.beaconLoc }.filter { it.y == targetY }.distinct()

    return invalidLocations.size - beacons.size.toLong()
  }


  fun part2(dimension: Int): Long {
    for (y in 0..dimension) {
      val x = findOpenSpot(y)
      if (x != -1L) {
        println("Found it! $x $y")
        println("Answer: ${x * 4_000_000 + y}")
        return x * 4_000_000 + y
      }
    }
    throw RuntimeException("Solution not found")
  }

  /** Finds the open x value in the target y row or returns -1 to indicate that there is no opening. */
  private fun findOpenSpot(y: Int): Long {
    val coveredRanges = coveredRanges(y).sortedBy { it.first }.toList()
    coveredRanges.reduce { a, b ->
      if (a.overlaps(b) || a.last + 1 == b.first) {
        return@reduce min(a.first, b.first)..max(a.last, b.last)
      }
      return a.last + 1.toLong()
    }

    return -1
  }

  /** Returns a list of ranges that are covered by sensors for the give row y. */
  private fun coveredRanges(targetY: Int): List<IntRange> {
    val notHereRanges = sensors.filter { s ->
      abs(targetY - s.sensorLoc.y) <= s.range()
    }.map { s ->
      val dx = s.range() - abs(targetY - s.sensorLoc.y)
      -dx + s.sensorLoc.x..dx + s.sensorLoc.x
    }
    return notHereRanges
  }
}

fun main() {
  val day = "15".toInt()

  val todayTest = Day15(readInput(day, 2022, true))
  execute({ todayTest.part1(10) }, "Day[Test] $day: pt 1", 26L)

  val today = Day15(readInput(day, 2022))
//  execute({ today.part1(2000000) }, "Day $day: pt 1")

  execute({ todayTest.part2(20) }, "Day[Test] $day: pt 2", 56000011L)
  execute({ today.part2(4_000_000) }, "Day $day: pt 2", 13615843289729) // 796961409
}
