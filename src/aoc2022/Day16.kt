package aoc2022

import utils.*
import java.util.*
import kotlin.math.max

private const val STARTING_ROOM = "AA"

private class Day16(val lines: List<String>) {

  var roomMap: Map<String, Room>
  val sparseEdges: Map<String, List<Edge>>
  val cache = mutableMapOf<State, Long>()

  init {
    val denseEdges = mutableMapOf<String, List<Edge>>()
    val denseRooms = lines.map { l ->
      val (a, b) = l.split(";")
      val name = a.split(" ")[1]
      val flow = a.split("=")[1].toLong()

      denseEdges[name] = b.split("valve")[1]
        .removePrefix("s")
        .removePrefix(" ")
        .split(", ")
        .map { Edge(it, 1) }

      Room(name, flow)
    }

    roomMap = denseRooms
      .filter { it.rate >= 0 || it.name == STARTING_ROOM }
      .associateBy { it.name }

    // Make shortest paths from every room to every other room
    sparseEdges = denseRooms.associate { r ->
      val shortestPath = computeSparseEdges(r, denseEdges)

      r.name to shortestPath
        .filter { r.name != it.key }
        .filter { roomMap[it.key]!!.rate > 0 }
        .map { Edge(it.key, it.value) }
    }
  }

  /** Create min spanning tree for this room. */
  private fun computeSparseEdges(r: Room, denseEdges: Map<String, List<Edge>>): Map<String, Int> {
    val shortestPaths = mutableMapOf<String, Int>()
    val pq = PriorityQueue<Pair<String, Int>>(compareBy { it.second }) // PQ
    pq.add(r.name to 0)

    while (pq.isNotEmpty()) {
      val (name, distance) = pq.poll()

      // check
      if (shortestPaths.containsKey(name) && shortestPaths[name]!! <= distance) {
        continue
      }
      shortestPaths[name] = distance

      pq.addAll(denseEdges[name]!!.map { it.dest to distance + 1 })
    }
    return shortestPaths
  }

  data class Room(val name: String, val rate: Long)
  data class Edge(val dest: String, val weight: Int)
  data class Actor(val minute: Int, val location: String)

  data class State(
    val actors: List<Actor>,
    val openValves: Set<String>,
    val isEle: Boolean = false,
    val isPt2: Boolean = false
  ) {
    constructor(actor: Actor, openValves: Set<String>, isEle: Boolean = false, isPt2: Boolean = false)
        : this(listOf(actor), openValves, isEle, isPt2)

    fun leadActor(): Actor {
      return actors[0]
    }
  }

  fun part1(): Long {
    cache.clear()
    val state = State(Actor(0, STARTING_ROOM), setOf())
    val result = sparseEdges[state.leadActor().location]!!.maxOfOrNull {
      goto(it, state, 30)
    }!!
    cache.clear()
    return result
  }

  fun part2(): Long {
    cache.clear()

    val me = Actor(0, STARTING_ROOM)
    val elephant = Actor(0, STARTING_ROOM)

    val state = State(listOf(me, elephant), setOf())
    val result = sparseEdges[state.leadActor().location]!!.maxOfOrNull {
      goto(it, state, 26)
    }!!

    cache.clear()
    return result
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
    val currentMinute = prevState.leadActor().minute + edge.weight + 1

    // Turn on the valve
    val openValves = buildSet { addAll(prevState.openValves); add(loc.name) }
    val pressureReleasedThisTurn = loc.rate * (totalMinutes - currentMinute)

    val actors = mutableListOf<Actor>()
    actors.addAll(prevState.actors.drop(1))
    actors.add(Actor(currentMinute, loc.name))
    actors.sortedWith(compareBy<Actor> { it.minute }.thenBy { it.location })

    val newState = prevState.copy(actors = actors, openValves=openValves)

    if (newState.actors.any { it.minute >= totalMinutes }) {
      return 0L
    }

    if (cache.containsKey(newState)) {
      return cache[newState]!!
    }

    val maxPressureReleasedAfterThisTurn = sparseEdges[newState.leadActor().location]!!
      .filter { !newState.openValves.contains(it.dest) }
      .map { goto(it, newState, totalMinutes) }
      .maxOrNull() ?: 0L

    // If we have more than one actor, consider the case where the leader goes solo.
    var maxPressureReleasedAfterThisTurnSolo = 0L
    if (actors.size > 1) {
      val soloState = newState.copy(actors = actors.take(1))
      maxPressureReleasedAfterThisTurnSolo = sparseEdges[soloState.leadActor().location]!!
        .filter { !soloState.openValves.contains(it.dest) }
        .map { goto(it, soloState, totalMinutes) }
        .maxOrNull() ?: 0L
    }

    val maxPressureReleased = max(maxPressureReleasedAfterThisTurnSolo, maxPressureReleasedAfterThisTurn) + pressureReleasedThisTurn

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

  execute(todayTest::part2, "Day[Test] $day: pt 2", 1707L)
  execute(today::part2, "Day $day: pt 2", 2838L)
}
