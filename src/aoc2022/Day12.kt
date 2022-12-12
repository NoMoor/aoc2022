package aoc2022

import utils.*
import utils.Coord.Companion.get
import java.util.*

private class Day12(val lines: List<String>) {

  private fun parseInput(filter: (Node, Node) -> Boolean): List<Node> {
    val nodes = lines.map { it.toList() }.mapIndexed { r, row ->
      row.mapIndexed { c, value ->
        Node(r, c, value)
      }
    }

    nodes.forEach { row ->
      row.forEach { node ->
        node.edges.addAll(node.loc.neighborsBounded(row.size, nodes.size, "+")
          .map { neighbor -> nodes[neighbor] }
          .filter { neighbor -> filter(node, neighbor) }
        )
      }
    }

    return nodes.flatten()
  }

  fun part1(): Any? {
    val nodes = parseInput { node, neighbor -> neighbor.height() - 1 <= node.height() }
    val pq = PriorityQueue<Pair<Node, Int>>(compareBy { it.second })
    val distances = mutableMapOf<Node, Int>()
    pq.add(nodes.first { it.value == 'S' } to 0)

    while (pq.isNotEmpty()) {
      val (node, dist) = pq.poll()

      if (distances.getOrDefault(node, Int.MAX_VALUE) <= dist) {
        continue
      }
      distances[node] = dist

      if (node.value == 'E') {
        return dist
      }

      pq.addAll(node.edges.map { it to dist + 1 })
    }

    return null
  }

  fun part2(): Any? {
    val nodes = parseInput { neighbor, node -> neighbor.height() - 1 <= node.height() }
    val pq = PriorityQueue<Pair<Node, Int>>(compareBy { it.second })
    val distances = mutableMapOf<Node, Int>()
    pq.add(nodes.first { it.value == 'E' } to 0)

    while (pq.isNotEmpty()) {
      val (node, dist) = pq.poll()

      if (distances.getOrDefault(node, Int.MAX_VALUE) <= dist) {
        continue
      }
      distances[node] = dist

      if (node.height() == 'a'.code) {
        return dist
      }

      pq.addAll(node.edges.map { it to dist + 1 })
    }

    return null
  }
}

data class Node(val r: Int, val c: Int, val value: Char) {
  val loc = Coord.rc(r, c)
  val edges = mutableListOf<Node>()

  fun height(): Int {
    return when (value) {
      'S' -> 'a'.code
      'E' -> 'z'.code
      else -> value.code
    }
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
