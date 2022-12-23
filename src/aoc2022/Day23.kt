package aoc2022

import utils.*
import java.util.*
import kotlin.system.measureNanoTime

private class Day23(val lines: List<String>) {
  val elfPositions = lines.mapIndexed { r, row ->
    row.mapIndexed { c, value ->
      if (value == '#') Coord.rc(-r, c) else Coord.rc(Int.MAX_VALUE, Int.MAX_VALUE)
    }
  }.flatten().filter { it != Coord.rc(Int.MAX_VALUE, Int.MAX_VALUE) }

  private val proposalOffsets = listOf(
    listOf(Coord.xy(-1, 1), Coord.xy(0, 1), Coord.xy(1, 1)), // North
    listOf(Coord.xy(-1, -1), Coord.xy(0, -1), Coord.xy(1, -1)), // south
    listOf(Coord.xy(-1, -1), Coord.xy(-1, 0), Coord.xy(-1, 1)), // west
    listOf(Coord.xy(1, -1), Coord.xy(1, 0), Coord.xy(1, 1)), // east
  )

  fun generateProposalDirections(c: Coord, roundNum: Int) : List<List<Coord>> {
    val p = proposalOffsets.toMutableList()
    Collections.rotate(p, -roundNum)

    return p.map { it.map { it + c } }
  }

  fun part1(): Long {
    var currElfPositions = elfPositions.toSet()

    println()
    println("Initial pos")
    visualizeElves(currElfPositions)

    repeat(10) { roundNum ->
      val proposedPositions = currElfPositions.map {
        val suggestions = generateProposalDirections(it, roundNum)

        if (suggestions.all { it.none { it in currElfPositions } }) {
          return@map it to it
        }

        it to (suggestions.firstOrNull { it.none { it in currElfPositions } }?.get(1) ?: it)
      }

      val proposedPositionToPairs = proposedPositions.groupBy { it.second }
      val newPositions = mutableListOf<Coord>()
      for (e in proposedPositionToPairs) {
        if (e.value.size == 1) {
          newPositions.add(e.key) // Maybe move
        } else {
          newPositions.addAll(e.value.map { it.first }) // Stay still
        }
      }

      currElfPositions = newPositions.toSet()

      println("Round ${roundNum + 1}")
      visualizeElves(currElfPositions)
    }

    val minX = currElfPositions.minOf { it.x }
    val maxX = currElfPositions.maxOf { it.x }
    val minY = currElfPositions.minOf { it.y }
    val maxY = currElfPositions.maxOf { it.y }

    return ((maxX - minX + 1).toLong() * (maxY - minY + 1).toLong()) - currElfPositions.size
  }

  private fun visualizeElves(currElfPositions: Collection<Coord>) {
    val set = currElfPositions.toSet()

    val minX = set.minOf { it.x }
    val maxX = set.maxOf { it.x }
    val minY = set.minOf { it.y }
    val maxY = set.maxOf { it.y }

    for (y in maxY downTo minY) {
      println((minX..maxX).joinToString("") { x -> if (Coord.xy(x, y) in set) "#" else "." })
    }
  }

  fun part2(): Long {
    val currElfPositions = elfPositions.toMutableSet()

    println()
    println("Initial pos")
    visualizeElves(currElfPositions)

    var roundNum = 0
    while (true) {
      roundNum++

      val proposedPositions = currElfPositions.groupBy {
        val suggestions = generateProposalDirections(it, roundNum - 1)

        // If no other elves are around this elf, stay here.
        if (suggestions.all { it.none { it in currElfPositions } }) {
          return@groupBy it
        }

        // Map the current position to the proposed position.
        suggestions.firstOrNull { it.none { it in currElfPositions } }?.get(1) ?: it
      }

      var movingCount = 0

      currElfPositions.clear()

      for (e in proposedPositions) {
        if (e.value.size == 1) {
          currElfPositions.add(e.key)

          // Only count moves when the new position is different from the old position.
          if (e.key != e.value[0]) {
            movingCount++
          }
        } else {
          currElfPositions.addAll(e.value) // Stay still
        }
      }

      println("Round $roundNum - $movingCount moving")
      if (movingCount == 0) {
        visualizeElves(currElfPositions)
        return roundNum.toLong()
      }
    }
  }
}

fun main() {
  val totalTime = measureNanoTime {
    val day = "23".toInt()

    val todayTest = Day23(readInput(day, 2022, true))
    execute(todayTest::part1, "Day[Test] $day: pt 1", 110L)

    val today = Day23(readInput(day, 2022))
    execute(today::part1, "Day $day: pt 1", 3917L)

    execute(todayTest::part2, "Day[Test] $day: pt 2", 20L)
    execute(today::part2, "Day $day: pt 2", 988L)
  }

  println("Total time: ${formatNanos(totalTime)}")
}
