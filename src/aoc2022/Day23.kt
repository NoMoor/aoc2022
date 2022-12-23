package aoc2022

import utils.*
import java.util.*

private class Day23(val lines: List<String>) {
  val elfPositions = lines.mapIndexed { r, row ->
    row.mapIndexed { c, value ->
      if (value == '#') Coord.rc(-r, c) else Coord.rc(Int.MAX_VALUE, Int.MAX_VALUE)
    }
  }.flatten().filter { it != Coord.rc(Int.MAX_VALUE, Int.MAX_VALUE) }


  fun generateProposalDirections(c: Coord) : List<List<Coord>> {
    return listOf(
      listOf(c + Coord.xy(-1, 1), c + Coord.xy(0, 1), c + Coord.xy(1, 1)), // North
      listOf(c + Coord.xy(-1, -1), c + Coord.xy(0, -1), c + Coord.xy(1, -1)), // south
      listOf(c + Coord.xy(-1, -1), c + Coord.xy(-1, 0), c + Coord.xy(-1, 1)), // west
      listOf(c + Coord.xy(1, -1), c + Coord.xy(1, 0), c + Coord.xy(1, 1)), // east
    )
  }

  fun part1(): Long {
    var currElfPositions = elfPositions.toSet()

    println()
    println("Initial pos")
    visualizeElves(currElfPositions)

    repeat(10) { roundNum ->
      val proposedPositions = currElfPositions.map {
        val suggestions = generateProposalDirections(it).toMutableList()
        Collections.rotate(suggestions, -roundNum)

        if (suggestions.flatten().none { it in currElfPositions }) {
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
    var currElfPositions = elfPositions.toSet()

    println()
    println("Initial pos")

    var roundNum = 0
    while (true) {
      roundNum++

      val proposedPositions = currElfPositions.map {
        val suggestions = generateProposalDirections(it).toMutableList()
        // Rotate left 1 when we are on round 2, left 0 on round 5, etc.
        Collections.rotate(suggestions, -(roundNum - 1))

        // If no other elves are around this elf, stay here.
        if (suggestions.flatten().none { it in currElfPositions }) {
          return@map it to it
        }

        // Map the current position to the proposed position.
        it to (suggestions.firstOrNull { it.none { it in currElfPositions } }?.get(1) ?: it)
      }

      val proposedPositionToPairs = proposedPositions.groupBy { it.second }
      val newPositions = mutableListOf<Coord>()
      var movingCount = 0
      for (e in proposedPositionToPairs) {
        if (e.value.size == 1) {
          newPositions.add(e.key)

          // Only count moves when the new position is different from the old position.
          if (e.key != e.value[0].first) {
            movingCount++
          }
        } else {
          newPositions.addAll(e.value.map { it.first }) // Stay still
        }
      }

      println("Round $roundNum - $movingCount moving")
      if (movingCount == 0) {
        visualizeElves(currElfPositions)
        return roundNum.toLong()
      }

      currElfPositions = newPositions.toSet()
    }
  }
}

fun main() {
  val day = "23".toInt()

  val todayTest = Day23(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1", 110L)

  val today = Day23(readInput(day, 2022))
  execute(today::part1, "Day $day: pt 1", 3917L)

  execute(todayTest::part2, "Day[Test] $day: pt 2", 20L)
  execute(today::part2, "Day $day: pt 2", 988L)
}
