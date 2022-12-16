package aoc2022

import utils.*
import java.util.*

private const val STARTING_ROOM = "AA"

private class Day16(val lines: List<String>) {

  var roomMap: Map<String, Room>
  val sparseEdges: Map<String, List<Edge>>
  val cache = mutableMapOf<State, Long>()

  init {
    val denseEdges = mutableMapOf<String, List<Edge>>()
    val denseRooms = lines.map {
      val (a, b) = it.split(";")
      val name = a.split(" ")[1]
      val flow = a.split("=")[1].toLong()
      val edges = b.split("valve")[1]
        .removePrefix("s")
        .removePrefix(" ")
        .split(", ")
        .map { Edge(it, 1) }

      denseEdges.put(name, edges)

      Room(name, flow)
    }

    roomMap = denseRooms.filter { it.rate >= 0 || it.name == STARTING_ROOM }.associateBy { it.name }

    // Make paths from every room to every other room
    sparseEdges = denseRooms.associate { r ->
      val shortestPath = computeSparseEdges(r, denseEdges)

      r.name to shortestPath
        .filter { r.name != it.key }
        .filter { roomMap[it.key]!!.rate > 0 }
        .map { Edge(it.key, it.value) }
    }
  }

  private fun computeSparseEdges(r: Room, denseEdges: Map<String, List<Edge>>): Map<String, Int> {
    // Create spanning tree for this room
    val shortestPath = mutableMapOf<String, Int>()
    val stack = PriorityQueue<Pair<String, Int>>(compareBy { it.second }) // PQ
    stack.add(r.name to 0)

    while (stack.isNotEmpty()) {
      val (name, distance) = stack.poll()

      // check
      if (shortestPath.containsKey(name) && shortestPath[name]!! <= distance) {
        continue
      }
      shortestPath[name] = distance

      stack.addAll(denseEdges[name]!!.map { it.dest to distance + 1 })
    }
    return shortestPath
  }

  data class Room(val name: String, val rate: Long)
  data class Edge(val dest: String, val weight: Int)

  data class State(
    val minute: Int,
    val location: Room,
    val openValves: Set<String>,
    val isEle: Boolean = false,
    val isPt2: Boolean = false
  )

  fun part1(): Long {
    cache.clear()
    val state = State(0, roomMap.get(STARTING_ROOM)!!, setOf())
    return sparseEdges[state.location.name]!!.maxOfOrNull { goto(it, state, 30) }!!
  }

  fun part2(): Long {
    cache.clear()
    val state = State(0, roomMap[STARTING_ROOM]!!, setOf(), isPt2 = true)
    return sparseEdges[state.location.name]!!.maxOfOrNull { goto(it, state, 26) }!!
  }

  /**
   * Traverse the edge taking n minutes. If there is time, turn the valve and score those points.
   *
   * Return the max pressure you can release after this state. This only includes valves opened after this state.
   * The total pressure released for a volve is counted on the round it is open.
   */
  private fun goto(edge: Edge, prevState: State, totalMinutes: Int): Long {
    // Move to the new location
    val loc = roomMap.get(edge.dest)!!
    // Put us on turn + traversal time + 1 turn to turn on the valve.
    // We can check the turn to change the valve now because if we don't have time to turn it, we can't score
    // any more points.
    val currentMinute = prevState.minute + edge.weight + 1

    if (currentMinute >= totalMinutes && prevState.isPt2 && !prevState.isEle) {
      // Once we've taken all our turns, Let the elephant take all its turns.
      val eleState = prevState.copy(minute = 0, location = roomMap[STARTING_ROOM]!!, isEle = true)
      val max = sparseEdges[eleState.location.name]!!.filter { it.dest !in prevState.openValves }
        .map { goto(it, eleState, totalMinutes) }
        .max()

      return max
    } else if (currentMinute >= totalMinutes) {
      return 0L
    }

    // Turn it on
    val openValves = buildSet { addAll(prevState.openValves); add(loc.name) }
    val pressureReleasedThisTurn = loc.rate * (totalMinutes - currentMinute)

    val newState = prevState.copy(minute = currentMinute, location = loc, openValves=openValves)

    if (cache.containsKey(newState)) {
      return cache[newState]!!
    }

    val maxPressureReleasedAfterThisTurn = sparseEdges[newState.location.name]!!
      .filter { !newState.openValves.contains(it.dest) }
      .map { goto(it, newState, totalMinutes) }
      .maxOrNull() ?: 0L

    val maxPressureReleased = maxPressureReleasedAfterThisTurn + pressureReleasedThisTurn
    cache[newState] = maxPressureReleased

    return maxPressureReleased
  }
}

fun main() {
  val day = "16".toInt()

  val todayTest = Day16(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1", 1651L)

  val today = Day16(readInput(day, 2022))
  execute(today::part1, "Day $day: pt 1")

//   execute(todayTest::part2, "Day[Test] $day: pt 2")
   execute(today::part2, "Day $day: pt 2", 2838L)
}
