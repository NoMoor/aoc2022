package aoc2022

import utils.*

private class Day18(val lines: List<String>) {

  data class Coord3d(val x:Int, val y:Int, val z:Int) {
    fun getCrossNeighbors() : List<Coord3d> {
      return listOf(
        this.offset(dx = 1),
        this.offset(dx = -1),
        this.offset(dy = 1),
        this.offset(dy = -1),
        this.offset(dz = 1),
        this.offset(dz = -1))
    }

    fun getAllNeighbors() : List<Coord3d> {
      return (-1..1).flatMap { x ->
        (-1..1).flatMap { y ->
          (-1..1).filter { z -> x != 0 || y != 0 || z != 0 }
            .map { z ->
              Coord3d(this.x + x, this.y + y, this.z + z)
          }
        }
      }
    }

    fun offset(dx: Int = 0, dy: Int = 0, dz: Int = 0) : Coord3d {
      return Coord3d(x + dx, y + dy, z + dz)
    }
  }

  fun part1(): Int {
    val drops = lines.map {
      val (x, y, z) = it.split(",").map { it.toInt() }
      Coord3d(x, y, z)
    }.toSet()

    return drops.sumOf {
      6 - it.getCrossNeighbors().count { it in drops }
    }
  }

  fun part2(): Int {
    val drops = lines.map {
      val (x, y, z) = it.split(",").map { it.toInt() }
      Coord3d(x, y, z)
    }.toSet()

    val surfaceCount = drops.sumOf {
      6 - it.getCrossNeighbors().count { it in drops }
    }

    // These are the ones I'm trying to figure out for surface area
    val allCrossNeighbors = drops.flatMap { it.getCrossNeighbors() }.filter { it !in drops }.distinct()

    // For traversing around
    val allNonLavaNeighborsIncludingDiagonals = drops.flatMap { it.getAllNeighbors() }.filter { it !in drops }.toSet()
    val definitelyOutsideNeighbor = drops.maxBy { it.x }.offset(dx = 1)

    val visitedOutside = mutableSetOf<Coord3d>()
    val toVisit = mutableListOf<Coord3d>()
    toVisit.add(definitelyOutsideNeighbor)
    while (toVisit.isNotEmpty()) {
      val curr = toVisit.removeFirst()

      if (curr in visitedOutside) {
        continue
      }
      visitedOutside.add(curr)
      toVisit.addAll(curr.getCrossNeighbors().filter { it in allNonLavaNeighborsIncludingDiagonals })
    }

    val insideNeighbors = allCrossNeighbors.toMutableList().filter { it !in visitedOutside }
    val insidesSurfaces = insideNeighbors.sumOf {
      it.getCrossNeighbors().count { it in drops }
    }

    return surfaceCount - insidesSurfaces
  }
}

fun main() {
  val day = "18".toInt()

  val todayTest = Day18(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1", 64)

  val today = Day18(readInput(day, 2022))
  execute(today::part1, "Day $day: pt 1")

  execute(todayTest::part2, "Day[Test] $day: pt 2", 58)
  execute(today::part2, "Day $day: pt 2")
  // Wrong guesses = 3374
}
