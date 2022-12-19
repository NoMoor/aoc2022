@file:OptIn(ExperimentalStdlibApi::class, DelicateCoroutinesApi::class)

package aoc2022

import kotlinx.coroutines.*
import utils.*
import kotlin.math.max

private class Day19(val lines: List<String>) {

  var addedScoreByTimeLeft = (0..40).map {
    (0..it).sum()
  }.toList()

  fun parseInput(): List<Blueprint> {
    return lines
      .filter { it.isNotEmpty() }
      .map { l ->
        val n = l.allInts()
        Blueprint(n[0], n[1], n[2], n[3] to n[4], n[5] to n[6])
      }.debug()
  }

  data class Blueprint(
    val id: Int,
    val oreRobotCost: Int,
    val clayRobotCost: Int,
    val obsidianRobotCost: Pair<Int, Int>,
    val geodeRobotCost: Pair<Int, Int>
  )

  data class State(
    val time: Int,
    val resourceCount: List<Int>,
    val robotCount: List<Int>
  )

  fun findBestMove(blueprint: Blueprint, state: State, timeLimit: Int = 24): Int {
    val cache = mutableMapOf<State, Int>()
    var best = 0
    val bestLog = mutableListOf<String>()
    var stringLog = mutableListOf<String>()

    val maxOreRobotCost = listOf(
      blueprint.oreRobotCost,
      blueprint.clayRobotCost,
      blueprint.obsidianRobotCost.first,
      blueprint.geodeRobotCost.first
    ).max()
    val maxClayRobotCost = blueprint.obsidianRobotCost.second
    val maxObsidianRobotCost = blueprint.geodeRobotCost.second

    fun findBestMostInternal(state: State): Int {
      fun turnsToWait(remaining: Int, income: Int): Int {
        if (remaining <= 0) return 0
        val fullTurns = remaining / income
        return fullTurns + if (remaining % income == 0) 0 else 1
      }

      val turnsRemaining = timeLimit - state.time
      val currentGeodes = state.resourceCount[3]
      val addedGeodesFromCurrentRobots = state.robotCount[3] * (turnsRemaining + 1)
      val maxAddedGeodesFromFutureRobots = addedScoreByTimeLeft[turnsRemaining]
      if (currentGeodes + addedGeodesFromCurrentRobots + maxAddedGeodesFromFutureRobots < best - 1) {
        return 0
      }

      if (state.time == timeLimit) {
        val score = state.resourceCount[3] + state.robotCount[3]
        if (score > best) {
          best = score
          bestLog.clear()
          bestLog.addAll(stringLog)
        }
        return score
      }

      val resultList = mutableListOf<Int>()
      if (state.robotCount[2] > 0) {
        // Simulate wait for geode
        val oreTurns = turnsToWait(blueprint.geodeRobotCost.first - state.resourceCount[0], state.robotCount[0])
        val obsidianTurns = turnsToWait(blueprint.geodeRobotCost.second - state.resourceCount[2], state.robotCount[2])
        val turns = 1 + max(oreTurns, obsidianTurns)

        if (turns + state.time <= timeLimit) {
          val resources =
            state.resourceCount.zip(state.robotCount).map { it.first + (it.second * turns) }.toMutableList()
          resources[0] -= blueprint.geodeRobotCost.first
          resources[2] -= blueprint.geodeRobotCost.second
          val robots = state.robotCount.toMutableList()
          robots[3]++

          stringLog.add("t${state.time + turns - 1} - Make Geode Robot")
          resultList.add(findBestMostInternal(State(state.time + turns, resources, robots)))
          stringLog.removeLast()
        }
      }
      if (state.resourceCount[1] > 0 && state.robotCount[2] < maxObsidianRobotCost) {
        // Wait for obisidian
        val oreTurns = turnsToWait(blueprint.obsidianRobotCost.first - state.resourceCount[0], state.robotCount[0])
        val clayTurns = turnsToWait(blueprint.obsidianRobotCost.second - state.resourceCount[1], state.robotCount[1])
        val turns = 1 + max(oreTurns, clayTurns)

        if (turns + state.time <= timeLimit) {
          val resources =
            state.resourceCount.zip(state.robotCount).map { it.first + (it.second * turns) }.toMutableList()
          resources[0] -= blueprint.obsidianRobotCost.first
          resources[1] -= blueprint.obsidianRobotCost.second
          val robots = state.robotCount.toMutableList()
          robots[2]++

          stringLog.add("t${state.time + turns - 1} - Make Obsidian Robot")
          resultList.add(findBestMostInternal(State(state.time + turns, resources, robots)))
          stringLog.removeLast()
        }
      }

      if (state.robotCount[1] < maxClayRobotCost) {
        val oreTurns = turnsToWait(blueprint.clayRobotCost - state.resourceCount[0], state.robotCount[0])
        val turns = 1 + oreTurns

        if (turns + state.time <= timeLimit) {
          val resources =
            state.resourceCount.zip(state.robotCount).map { it.first + (it.second * turns) }.toMutableList()
          resources[0] -= blueprint.clayRobotCost
          val robots = state.robotCount.toMutableList()
          robots[1]++

          stringLog.add("t${state.time + turns - 1} - Make Clay Robot")
          resultList.add(findBestMostInternal(State(state.time + turns, resources, robots)))
          stringLog.removeLast()
        }
      }

      // Make a state if we bought
      if (state.robotCount[0] < maxOreRobotCost) {
        val oreTurns = turnsToWait(blueprint.oreRobotCost - state.resourceCount[0], state.robotCount[0])
        val turns = 1 + oreTurns

        if (turns + state.time <= timeLimit) {
          val resources =
            state.resourceCount.zip(state.robotCount).map { it.first + (it.second * turns) }.toMutableList()
          resources[0] -= blueprint.oreRobotCost
          val robots = state.robotCount.toMutableList()
          robots[0]++

          stringLog.add("t${state.time + turns - 1} - Make Ore Robot")
          resultList.add(findBestMostInternal(State(state.time + turns, resources, robots)))
          stringLog.removeLast()
        }
      }

      val waitRounds = timeLimit - state.time + 1
      val waitScore = state.resourceCount[3] + (state.robotCount[3] * waitRounds)
      resultList.add(waitScore)
      val result = resultList.max()

      if (result == waitScore && waitScore > best) {
        best = result
        stringLog.add("t${state.time} - Wait $waitRounds")
        bestLog.clear()
        bestLog.addAll(stringLog)
        stringLog.removeLast()
      }

      return result
    }

    val result = findBestMostInternal(state)
    bestLog.debug()
    println("Result $result")
    return result
  }

  fun part1(): Int {
    val blueprints = parseInput()

    val result = blueprints.map {
      println("Try best score: ${it.id}")
      val initialState = State(1, mutableListOf(0, 0, 0, 0), mutableListOf(1, 0, 0, 0))
      val bestScore = findBestMove(it, initialState)
      println()
      println("Best Score $bestScore ${it.id}")
      it.id * bestScore
    }.sum()

    println("Potential answer: $result")

    return result
  }

  fun part2(): Long {
    val stopwatch = Stopwatch.started()
    val blueprints = parseInput()

    val result = blueprints
      .take(3)
      .map {
        println("Try best score: ${it.id}")
        val initialState = State(1, mutableListOf(0, 0, 0, 0), mutableListOf(1, 0, 0, 0))
        val bestScore = findBestMove(it, initialState, 32)
        println()
        println("${it.id} - Best Score $bestScore")
        bestScore.toLong()
      }
      .reduce { a, b -> a * b }

    println("Potential answer: $result")
    stopwatch.stop()
    return result
  }
}

fun main() {
  val day = "19".toInt()

  val todayTest = Day19(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1", 33)

  val today = Day19(readInput(day, 2022))
  execute(today::part1, "Day $day: pt 1", 1150)

  execute(todayTest::part2, "Day[Test] $day: pt 2", 56 * 62.toLong())
  execute(today::part2, "Day $day: pt 2", 37367L)
}
