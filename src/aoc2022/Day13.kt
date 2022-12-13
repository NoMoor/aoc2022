@file:Suppress("UNCHECKED_CAST")

package aoc2022

import utils.*
import kotlin.math.min

private class Day13(val lines: List<String>) {

  val pairs = lines.splitBy { it == "" }
    .map { parse(it[0].iterator()) to parse(it[1].iterator()) }
    .map { it.first[0] to it.second[0] }

  private fun parse(s: Iterator<Char>): List<Any> {
    val list = mutableListOf<Any>()
    while (s.hasNext()) {
      var c = s.next()

      if (c == '[') {
        list.add(parse(s))
      } else if (c.isDigit()) {
        var num = ""
        while (c.isDigit()) {
          num += c
          c = s.next()
        }
        list.add(num.toInt())
        // c is either , or ]
      }

      if (c == ']') {
        return list
      }
    }

    return list
  }

  fun compareList(a: List<Any>, b: List<Any>) : Boolean? {
    for (i in 0 until min(a.size, b.size)) {
      val firstItem = a[i]
      val secondItem = b[i]

      if (firstItem is Int && secondItem is Int) {
        if (firstItem != secondItem) {
          return firstItem < secondItem
        }
        continue
      }

      val firstList = if (firstItem is Int) listOf(firstItem) else firstItem
      val secondList = if (secondItem is Int) listOf(secondItem) else secondItem
      val result = compareList(firstList as List<Any>, secondList as List<Any>)
      if (result != null) {
        return result
      }
    }

    if (a.size == b.size) {
      return null
    }

    return a.size < b.size
  }

  fun part1(): Int {

    val correctIndices = mutableListOf<Int>()
    pairs.forEachIndexed { index, it ->
      val result = compareList(it.first as List<Any>, it.second as List<Any>)
      if (result!!) {
        correctIndices.add(index + 1)
      }
    }
    return correctIndices.sum()
  }

  fun part2(): Int {
    val sortedList = pairs.flatMap { it.toList() }.sortedWith {
      a, b -> if (compareList(a as List<Any>, b as List<Any>)!!) -1  else 1
    }

    val index1 = sortedList.indexOfFirst { it.toString() == "[[6]]" }
    val index2 = sortedList.indexOfFirst { it.toString() == "[[2]]" }

    return (index1 + 1) * (index2 + 1)
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
