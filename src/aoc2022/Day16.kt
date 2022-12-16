package aoc2022

import utils.*
import java.util.*
import kotlin.math.max

private class Day16(val lines: List<String>) {

  var roomsWithValves = lines.map {
    val (a, b) = it.split(";")
    val name = a.split(" ")[1]
    val flow = a.split("=")[1].toLong()
    val edges = b.split("valve")[1]
      .removePrefix("s")
      .removePrefix(" ")
      .split(", ")
      .map { Edge(it, 1) }
    val r = Room(name, flow)
    r.paths.addAll(edges)
    r
  }.toMutableList()

  var roomMap = roomsWithValves.associateBy { it.name }

  val cache = mutableMapOf<State, Long>()

  init {
//    val deadRooms = roomsWithValves.filter { it.rate == 0L }.filter { it.name != "AA" }.toList()
//    deadRooms.forEach { r ->
//      val source = roomsWithValves.first { it.name == r.paths[0].destination }
//      val destination = roomsWithValves.first { it.name == r.paths[1].destination }
//
//      val sourceEdge = source.paths.find { it.destination == r.name }!!
//      source.paths.remove(sourceEdge)
//      source.paths.add(Edge(destination.name, sourceEdge.weight + 1))
//
//      val destinationEdge = destination.paths.find { it.destination == r.name }!!
//      destination.paths.remove(destinationEdge)
//      destination.paths.add(Edge(source.name, destinationEdge.weight + 1))
//
//      roomsWithValves.remove(r)
//    }

    // Make paths from every room to every other room
    roomsWithValves.forEach { r ->
      // Create spanning tree for this room
      val shortestPath = mutableMapOf<String, Int>()
      val stack = PriorityQueue<Pair<String, Int>>(compareBy { it.second } ) // PQ
      stack.add(r.name to 0)

      while (stack.isNotEmpty()) {
        // grab
        val next = stack.poll()
        val nextRoom = roomMap[next.first]!!
        // increment
        val distance = next.second

        // check
        if (shortestPath.containsKey(next.first) && shortestPath[next.first]!! <= distance) {
          continue
        }

        shortestPath[next.first] = next.second

        // add neighbors
        nextRoom.paths.forEach { stack.add(it.destination to next.second + 1) }
      }

      r.paths.clear()
      shortestPath
        .filter { it.key != r.name }
        .filter { roomMap[it.key]!!.rate > 0 }
        .forEach { r.paths.add(Edge(it.key, it.value))}
    }
//
//    roomsWithValves.forEach { it.paths.sortByDescending {
//      roomMap[it.destination]!!.rate / (it.weight + 1)
//    } }
    roomsWithValves.debug()
  }

  data class Room(val name: String, val rate: Long) {
    val paths = mutableListOf<Edge>()

    override fun toString(): String {
      return "$name $rate $paths"
    }
  }

  data class Edge(val destination: String, val weight: Int)

  data class State(
    val turnNum: Int,
    val currentRoom: Room,
    val openValves: Set<String>,
  )

  fun part1(): Long {
    val state = State(0, roomMap.get("AA")!!, setOf())

    val result = state.currentRoom.paths.map { moveTo(it, state, 0) }.max()

    return result
  }

  /**
   * MOve to traverse the edge. Traversing takes n minutes. Score the ones before 30.
   * Then turn on the hose thing and score that. Then move more?
   *
   * Return the max result we've found
   */
  private fun moveTo(edge: Edge, prevState: State, score: Long): Long {
    // Move and score the valid minutes
    val location = roomMap.get(edge.destination)!!
    var turn = prevState.turnNum + edge.weight + 1
    if (turn >= 30) {
      return score
    }

    // Turn it on
    val openValves = prevState.openValves.toMutableSet()
    openValves.add(location.name)
    val addedScore = location.rate * (30 - turn)
    val newState = prevState.copy(turnNum = turn, currentRoom = location, openValves=openValves)

    if (cache.containsKey(newState)) {
      return cache[newState]!!
    }

    var bestPath = location.paths
      .filter { !newState.openValves.contains(it.destination) }
      .map { moveTo(it, newState, score + addedScore) }
      .maxOrNull()
    if (bestPath == null) {
      bestPath = 0
    }

    val maxScore = max(score + addedScore, bestPath)
    cache[newState] = maxScore
    return maxScore
  }

  fun part2(): Any? {
    return lines.size
  }
}

fun main() {
  val day = "16".toInt()

  val todayTest = Day16(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1", 1651)

//  val today = Day16(readInput(day, 2022))
//  execute(today::part1, "Day $day: pt 1")

  // execute(todayTest::part2, "Day[Test] $day: pt 2")
  // execute(today::part2, "Day $day: pt 2")
}
