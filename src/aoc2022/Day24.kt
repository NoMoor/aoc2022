package aoc2022

import utils.*

private class Day24(val lines: List<String>) {

  fun parseInput(): Triple<Coord, Coord, List<Blizz>> {
    val start = Coord.xy(lines[0].indexOfFirst { it == '.' }, 0)
    val end = Coord.xy(lines.last().indexOfFirst { it == '.' }, lines.size - 1)

    val blizzards = mutableListOf<Blizz>()
    for (y in lines.indices) {
      for (x in lines[y].indices) {
        if (lines[y][x] == '>') {
          blizzards.add(Blizz(Coord.xy(x, y), Coord.RIGHT))
        } else if (lines[y][x] == '<') {
          blizzards.add(Blizz(Coord.xy(x, y), Coord.LEFT))
        } else if (lines[y][x] == '^') {
          blizzards.add(Blizz(Coord.xy(x, y), Coord.DOWN))
        } else if (lines[y][x] == 'v') {
          blizzards.add(Blizz(Coord.xy(x, y), Coord.UP))
        }
      }
    }

    return Triple(start, end, blizzards)
  }

  data class Blizz(val loc: Coord, val dir: Coord)

  fun part1(): Long {
    println()
    val (start, end, blizzards) = parseInput()
    val endAdjacent = (end + Coord.DOWN)

    val topLeft = Coord.xy(1, 1)
    val bottomRight = Coord.xy(lines[0].length - 2, lines.size - 2)
    val gridBounds = (topLeft..bottomRight)

    var currentPositions = setOf(start)
    var currentBlizzards = blizzards

    var roundCount = 0
    while (true) {
      // Update the grid to where it will be
      val newBlizzards = currentBlizzards.map { updateBlizzardLocation(it, gridBounds) }
      val newBlizzardLocations = newBlizzards.map { it.loc }.toSet()

      if (endAdjacent in currentPositions) {
        return roundCount + 1.toLong()
      }

      // for each spot, add all the spots that we could potentially move
      val newPossibleLocations = currentPositions.flatMap {
        buildList {
          add(it)
          addAll(it.neighborsBounded(gridBounds.xRange, gridBounds.yRange, "+"))
        }.filter { it !in newBlizzardLocations }
      }.toSet()

      currentBlizzards = newBlizzards
      currentPositions = newPossibleLocations

      roundCount++
    }
  }

  private fun updateBlizzardLocation(it: Blizz, gridBounds: CoordRange): Blizz {
    var newLoc = it.loc + it.dir
    if (newLoc !in gridBounds) {
      if (newLoc.x < gridBounds.xRange.first) {
        newLoc = Coord.xy(gridBounds.xRange.last, newLoc.y)
      } else if (newLoc.x > gridBounds.xRange.last) {
        newLoc = Coord.xy(gridBounds.xRange.first, newLoc.y)
      } else if (newLoc.y < gridBounds.yRange.first) {
        newLoc = Coord.xy(newLoc.x, gridBounds.yRange.last)
      } else if (newLoc.y > gridBounds.yRange.last) {
        newLoc = Coord.xy(newLoc.x, gridBounds.yRange.first)
      }
    }
    return Blizz(newLoc, it.dir)
  }

  fun part2(): Long {
    println()
    val (start, end, blizzards) = parseInput()
    val endAdjacent = (end + Coord.DOWN)
    val startAdjacent = (start + Coord.UP)

    val topLeft = Coord.xy(1, 1)
    val bottomRight = Coord.xy(lines[0].length - 2, lines.size - 2)
    val gridBounds = (topLeft..bottomRight)

    var currentPositions = setOf(start)
    var currentBlizzard = blizzards

    var traversals = 0

    var roundCount = 0
    while (true) {
      // Update the grid to where it will be
      val newBlizzards = currentBlizzard.map { updateBlizzardLocation(it, gridBounds) }
      val newBlizzardLocations = newBlizzards.map { it.loc }.toSet()

      // Going toward the end
      if (endAdjacent in currentPositions && traversals % 2 == 0) {
        currentPositions = setOf(end)

        if (traversals == 2) {
          return roundCount + 1.toLong()
        }

        traversals++
        // Going back toward the start
      } else if (startAdjacent in currentPositions && traversals % 2 == 1) {
        currentPositions = setOf(start)
        traversals++
      } else {
        val newPossibleLocations = currentPositions.flatMap {
          buildList {
            add(it)
            addAll(it.neighborsBounded(gridBounds.xRange, gridBounds.yRange, "+"))
          }.filter { it !in newBlizzardLocations }
        }.toMutableSet()

        currentPositions = newPossibleLocations
      }

      currentBlizzard = newBlizzards
      roundCount++
    }
  }
}

fun main() {
  val day = "24".toInt()

  val todayTest = Day24(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1", 18L)

  val today = Day24(readInput(day, 2022))
  execute(today::part1, "Day $day: pt 1", 299L)

  execute(todayTest::part2, "Day[Test] $day: pt 2", 54L)
  execute(today::part2, "Day $day: pt 2", 899L)
}
