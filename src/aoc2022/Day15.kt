package aoc2022

import utils.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private class Day15(val lines: List<String>) {
  val sensors = lines.map { it.removePrefix("Sensor at ").allInts() }
    .map { Coord.xy(it[0], it[1]) to Coord.xy(it[2], it[3]) }

  val sensorDist = lines.map { it.removePrefix("Sensor at ").allInts() }
    .map { Coord.xy(it[0], it[1]) to Coord.xy(it[2], it[3]) }
    .map {
      val diff = it.first - it.second
      it.first to abs(diff.x) + abs(diff.y)
    }
    .toList()

  fun part1(targetY: Int): Long {
    val notHereRange = mutableSetOf<IntRange>()
    for (s in sensorDist) {
      val sesorLoc = s.first

      val dy = targetY - sesorLoc.y // sensor at 20 and care about i = 10
      // only check when dy < -10

      val total = s.second

      if (abs(dy) > total) {
        continue
      }

      val dx = total - abs(dy)
      notHereRange.add(-dx + sesorLoc.x..dx + sesorLoc.x)
    }

    val lineII = notHereRange.flatMap { it }.distinct()
    val beaconsI = sensors.map { it.second }.filter { it.y == targetY }.distinct()

    return lineII.size - beaconsI.size.toLong()
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

  fun IntRange.contains(b: IntRange): Boolean {
    return this.first <= b.first && b.last <= this.last
  }

  fun IntRange.overlaps(b: IntRange): Boolean {
    return this.contains(b.first) || this.contains(b.last) || b.contains(this.first)
  }

  private fun findOpenSpot(i: Int): Long {
    val notHereRanges = mutableSetOf<IntRange>()
    for (s in sensorDist) {
      val sesorLoc = s.first

      val dSToI = i - sesorLoc.y // sensor at 20 and care about i = 10
      // only check when dy < -10

      val total = s.second

      if (abs(dSToI) > total) {
        continue
      }

      val y = dSToI
      val dx = total - abs(y)
      val notHereRange = -dx + sesorLoc.x..dx + sesorLoc.x
      notHereRanges.add(notHereRange)
    }

    val rangeList = notHereRanges.sortedBy { it.first }.toList()
    var bigRange = rangeList[0]
    if (bigRange.first > 0) {
      return 0
    }

    for (r in rangeList) {
      if (bigRange.contains(r)) {
        continue
      }

      if (bigRange.overlaps(r) || bigRange.last + 1 == r.first) {
        bigRange = min(bigRange.start, r.start)..max(bigRange.last, r.last)
      } else {
        return bigRange.last + 1.toLong()
      }
    }

    return -1
  }
}

fun main() {
  val day = "15".toInt()

  val todayTest = Day15(readInput(day, 2022, true))
//  execute({ todayTest.part1(10) }, "Day[Test] $day: pt 1", 26L)

  val today = Day15(readInput(day, 2022))
//  execute({ today.part1(2000000) }, "Day $day: pt 1")

  execute({ todayTest.part2(20) }, "Day[Test] $day: pt 2", 56000011L)
  execute({ today.part2(4_000_000) }, "Day $day: pt 2", 13615843289729) // 796961409
}
