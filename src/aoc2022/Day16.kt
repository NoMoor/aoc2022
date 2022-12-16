package aoc2022

import utils.*

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

  var roomMap: Map<String, Room>

  val cache = mutableMapOf<State, State>()

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

    roomMap = roomsWithValves.associateBy { it.name }

    roomsWithValves.forEach { it.paths.sortByDescending {
      roomMap[it.destination]!!.rate / (it.weight + 1)
    } }
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
    val openValves: Map<String, Long>,
  ) {
    var bestScore: Long = 0
  }

  var count = 0L
  var bestResultYet: State = State(1, roomMap.get("AA")!!, mapOf())

  fun part1(): Long {
    val state = State(1, roomMap.get("AA")!!, mapOf())

    val result = state.currentRoom.paths.map { moveTo(it, state) }.maxBy { it.bestScore }

    println(result)

    return result.bestScore
  }

  private fun recordBest(state: State): State {
    if (state.bestScore > bestResultYet.bestScore) {
      bestResultYet = state
      println("New Best: $bestResultYet")
    }
    return state
  }

  private fun moveTo(edge: Edge, state: State): State {
    val location = roomMap.get(edge.destination)!!
    val turn = state.turnNum + 1
//    val scoredTurns = if (turn > 30) (edge.weight - (turn - 30)) else edge.weight

    val currentScore = state.bestScore + (state.openValves.values.sum())
    if (turn >= 30) {
      val returnVal = state.copy(turnNum = turn)
      returnVal.bestScore = currentScore
      return recordBest(returnVal)
    }

    val newState = state.copy(turnNum = turn, currentRoom = location)
    newState.bestScore = currentScore
    if (cache.containsKey(newState)) {
      println("Hit cache")
      return cache[newState]!!
    }

    var bestState = newState

    if (location.rate > 0 && !state.openValves.containsKey(location.name)) {
      val pathState = openValve(newState)
      if (pathState.bestScore > bestState.bestScore) bestState = pathState
    }

    for (p in location.paths.filter { !state.openValves.containsKey(it.destination) }) {
      val pathState = moveTo(p, newState)
      if (pathState.bestScore > bestState.bestScore) bestState = pathState
    }

    val turnsLeft = 30 - turn
    val idleScore = currentScore + (turnsLeft * newState.openValves.values.sum())
    if (idleScore > bestState.bestScore) {
      bestState = newState.copy(turnNum = 30)
      bestState.bestScore = idleScore
    }

    cache[newState] = bestState
    return recordBest(bestState)
  }

  private fun openValve(state: State): State {
    val turn = state.turnNum + 1
    val openValves = state.openValves.toMutableMap()
    openValves[state.currentRoom.name] = state.currentRoom.rate

    val currentScore = state.bestScore + openValves.values.sum()
    println("Score: $currentScore $turn $state")
    if (turn == 30) {
      val newState = state.copy(turnNum = turn, openValves = openValves)
      newState.bestScore = currentScore
      return recordBest(newState)
    }

    val newState = state.copy(turnNum = turn, openValves = openValves)
    newState.bestScore = currentScore
    if (cache.containsKey(newState)) {
      println("Hit cache")
      return cache[newState]!!
    }


    var bestState = newState
    for (p in state.currentRoom.paths.filter { !newState.openValves.containsKey(it.destination) }) {
      val pathState = moveTo(p, newState)
      if (pathState.bestScore > bestState.bestScore) bestState = pathState
    }

    val turnsLeft = 30 - turn
    val idleScore = currentScore + (turnsLeft * openValves.values.sum())
    if (idleScore > bestState.bestScore) {
      bestState = newState.copy(turnNum = 30)
      bestState.bestScore = idleScore
    }

    cache[newState] = bestState
    return recordBest(bestState)
  }

  fun part2(): Any? {
    return lines.size
  }
}

fun main() {
  val day = "16".toInt()

  val todayTest = Day16(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1")

//  val today = Day16(readInput(day, 2022))
//  execute(today::part1, "Day $day: pt 1")

  // execute(todayTest::part2, "Day[Test] $day: pt 2")
  // execute(today::part2, "Day $day: pt 2")
}
