@file:Suppress("UNCHECKED_CAST")

package aoc2022

import utils.*
import java.rmi.UnexpectedException
import kotlin.math.min

private class Day13(val lines: List<String>) {

  val pairs = lines.filter { it.isNotEmpty() }
    .map { parseList(it).first }

  /**
   * Recursively parses a list where each element is a sub-list or an int.
   *
   * Returns the first list encountered and the remainder of the unparsed string.
   *
   * Example: [4, [2], 2] -> a list of 3 elements and ""
   * Example: [2], 2] -> a list of one element [2] and ", 2]" as the unparsed string.
   */
  private fun parseList(input: String): Pair<List<Any>, String> {

    var currS = input
    val list = mutableListOf<Any>()

    assert(input.startsWith("["))
    currS = currS.removePrefix("[")

    // Go until we hit the end of this list.
    // Nested lists are handled by the parse sublist branch so we know
    // when this loop hits end bracket that this is the end of this lis.
    while (currS.first() != ']') {
      when {
        // Parse separator
        currS.first() == ',' -> {
          currS = currS.substring(1)
        }

        // Parse number
        currS.first().isDigit() -> {
          val numS = currS.takeWhile { it !in setOf(',', ']') }
          list.add(numS.toInt())
          currS = currS.substring(numS.length)
        }

        // Parse sublist
        currS.first() == '[' -> {
          val (sublistElement, unparsedString) = parseList(currS)
          list.add(sublistElement)
          currS = unparsedString
        }
        else -> throw UnexpectedException("oops")
      }
    }

    assert(input.startsWith("["))
    currS = currS.removePrefix("]")

    return list to currS
  }

  fun compare(a: List<Any>, b: List<Any>): Int {
    for (i in 0 until min(a.size, b.size)) {
      val firstItem = a[i]
      val secondItem = b[i]

      val result = if (firstItem is Int && secondItem is Int) {
        compareValues(firstItem, secondItem)
      } else {
        val firstList = if (firstItem is Int) listOf(firstItem) else firstItem
        val secondList = if (secondItem is Int) listOf(secondItem) else secondItem
        compare(firstList as List<Any>, secondList as List<Any>)
      }

      if (result != 0) {
        return result
      }
    }

    return compareValues(a.size, b.size)
  }

  fun part1(): Int {
    return pairs
      .chunked(2)
      .mapIndexed { index, it ->
        if (compare(it[0], it[1]) < 0) index + 1 else 0
      }.sum()
  }

  fun part2(): Int {
    return pairs
      .sortedWith { a, b ->
        compare(a as List<Any>, b as List<Any>)
      }.mapIndexed { index, it ->
        // Keep indexes of the two elements
        if (it.toString() in setOf("[[2]]", "[[6]]")) index + 1 else 1
      }.reduce { a, b -> a * b } // Compute the product of the list.
  }
}

fun main() {
  val day = "13".toInt()

  val todayTest = Day13(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1", 13)

  val today = Day13(readInput(day, 2022))
  execute(today::part1, "Day $day: pt 1", 6420) // 6485

  val todayTest2 = Day13(readInput(day, 2022, true, 2))
  execute(todayTest2::part2, "Day[Test] $day: pt 2", 140)

  val todayPt2 = Day13(readInput(day, 2022, inputIndex = 2))
  execute(todayPt2::part2, "Day $day: pt 2", 22000)
}
