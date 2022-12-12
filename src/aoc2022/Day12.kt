package aoc2022

import utils.*
import utils.mapDeepIndexed
import utils.Coord.Companion.get
import utils.Coord.Companion.rc
import java.rmi.UnexpectedException
import java.util.*

private class Day12(val lines: List<String>) {
  /** Parse lines into a list of interconnected nodes. */
  fun parseInput(filter: (a: Node, b: Node) -> Boolean): List<Node> {
    // Create nodes
    val nodes = lines.map { it.toList() }.mapDeepIndexed { r, c, value -> Node(rc( r, c), value) }

    // Map out neighbors
    nodes.forEach { row ->
      row.forEach { node ->
        node.neighbors.addAll(
          node.loc.neighborsBounded(row.size, nodes.size, "+")
            .map { nodes[it] }
            .filter { neighbor -> filter(node, neighbor) }
        )
      }
    }

    return nodes.flatten()
  }

  data class Node(val loc: Coord, val char: Char) {
    val neighbors = mutableListOf<Node>()
    var minDistance = Int.MAX_VALUE

    // Returns the height of this node.
    fun height(): Int {
      if (char == 'S') return 'a'.code
      if (char == 'E') return 'z'.code
      return char.code
    }
  }

  fun part1(): Any {
    val nodes = parseInput { source, dest -> dest.height() - 1 <= source.height() }
    val toVisit = PriorityQueue<Pair<Node, Int>>(compareBy { it.second })

    toVisit.add(nodes.first { it.char == 'S' } to 0)

    while (toVisit.isNotEmpty()) {
      val (curr, dist) = toVisit.poll()

      // We've been here before
      if (curr.minDistance <= dist) {
        continue
      } else {
        curr.minDistance = dist
      }

      // Check if this is the end.
      if (curr.char == 'E') {
        return curr.minDistance
      }

      // Add all the neighbors to visit at distance + 1
      curr.neighbors.map { it to curr.minDistance + 1 }.forEach { toVisit.add(it) }
    }

    throw UnexpectedException("We didn't find an answer.")
  }

  fun part2(): Any {
    // Swap source and dest in the filter to get the reverse traversal graph
    val nodes = parseInput { dest, source -> dest.height() - 1 <= source.height() }
    val toVisit = PriorityQueue<Pair<Node, Int>>(compareBy { it.second })

    toVisit.add(nodes.first { it.char == 'E' } to 0)

    while (toVisit.isNotEmpty()) {
      val (curr, dist) = toVisit.poll()

      // We've been here before.
      if (curr.minDistance <= dist) {
        continue
      } else {
        curr.minDistance = dist
      }

      // Check if this is the end.
      if (curr.height() == 'a'.code) {
        return curr.minDistance
      }

      // Add all the neighbors to visit at distance + 1
      curr.neighbors.map { it to curr.minDistance + 1 }.forEach { toVisit.add(it) }
    }

    throw UnexpectedException("We didn't find an answer.")
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
