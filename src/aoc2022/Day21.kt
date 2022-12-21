package aoc2022

import utils.*

private class Day21(val lines: List<String>) {
  val monkeys = lines.associate {
    val parts = it.split(":")
    val name = parts[0]
    val value = parts[1].trim().split(" ")
    name to value
  }

  fun part1(): Long {
    val knownValues = mutableMapOf<String, Long>()
    val monkeyCopy = monkeys.toMutableMap()
    val toRemove = mutableListOf<String>()
    while (!knownValues.containsKey("root")) {

      for (p in monkeyCopy) {
        if (p.value.size == 1) {
          knownValues[p.key] = p.value[0].toLong()
          toRemove.add(p.key)
        } else if (p.value[0] in knownValues.keys && p.value[2] in knownValues.keys) {
          knownValues[p.key] = computeValue(p.value, knownValues)
          toRemove.add(p.key)
        }
      }

      toRemove.forEach { monkeyCopy.remove(it) }
      toRemove.clear()
    }
    return knownValues["root"]!!
  }

  private fun computeValue(value: List<String>, knownValues: MutableMap<String, Long>): Long {
    return when (value[1]) {
      "+" -> knownValues[value[0]]!! + knownValues[value[2]]!!
      "-" -> knownValues[value[0]]!! - knownValues[value[2]]!!
      "*" -> knownValues[value[0]]!! * knownValues[value[2]]!!
      "/" -> knownValues[value[0]]!! / knownValues[value[2]]!!
      else -> throw RuntimeException("What?!")
    }
  }

  fun part2(): Long {
    val rootParts = monkeys["root"]!!.toMutableList()
    rootParts.removeAt(1)

    val knownValues = mutableMapOf<String, Long>()
    val monkeyCopy = monkeys.toMutableMap()
    val toRemove = mutableListOf<String>()
    while (rootParts.none { it in knownValues.keys }) {
      for (p in monkeyCopy) {
        if (p.key == "humn") {
          // Do nothing
        } else if (p.value.size == 1) {
          knownValues[p.key] = p.value[0].toLong()
          toRemove.add(p.key)
        } else if (p.value[0] in knownValues.keys && p.value[2] in knownValues.keys) {
          knownValues[p.key] = computeValue(p.value, knownValues)
          toRemove.add(p.key)
        }
      }

      toRemove.forEach { monkeyCopy.remove(it) }
      toRemove.clear()
    }

    // We're trying to match this value.
    var otherSideValue = knownValues[rootParts.first { it in knownValues.keys }]!!
    var toExpand = rootParts.first { it !in knownValues.keys }

    while (toExpand != "humn") {
      val values = monkeyCopy[toExpand]!!
      val p = doInverse(otherSideValue, values, knownValues)
      otherSideValue = p.first
      toExpand = p.second
    }

    return otherSideValue
  }

  private fun doInverse(
    otherSide: Long,
    values: List<String>,
    knownValues: MutableMap<String, Long>
  ): Pair<Long, String> {
    if (values[0] in knownValues.keys) {
      val v1 = knownValues[values[0]]!!.toLong()
      return when (values[1]) {
        "+" -> otherSide - v1 to values[2]
        "-" -> -(otherSide - v1) to values[2]// v1 - v2 = otherSide -> -v2 = -(otherSide - v1)
        "*" -> otherSide / v1 to values[2]
        "/" -> v1 / otherSide to values[2]
        else -> throw RuntimeException("oops")
      }
    } else {
      val v2 = knownValues[values[2]]!!.toLong()
      return when (values[1]) {
        "+" -> otherSide - v2 to values[0]
        "-" -> otherSide + v2 to values[0]
        "*" -> otherSide / v2 to values[0]
        "/" -> otherSide * v2 to values[0]
        else -> throw RuntimeException("oops")
      }
    }
  }
}

fun main() {
  val day = "21".toInt()

  val todayTest = Day21(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1")

  val today = Day21(readInput(day, 2022))
  execute(today::part1, "Day $day: pt 1")

  execute(todayTest::part2, "Day[Test] $day: pt 2", 301L)
  execute(today::part2, "Day $day: pt 2", 3558714869436L)
}
