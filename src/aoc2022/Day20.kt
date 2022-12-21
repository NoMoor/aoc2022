package aoc2022

import utils.*
import kotlin.math.abs

private class Day20(val lines: List<String>) {
  val nums = lines.map { it.toInt() }

  fun pad(a: Int, b: Int): Long =
    (a.toString() + b.toString().padStart(4, '0')).toLong()
  fun unpad(a: Long): Long = a / 10000

  fun part1(): Long {
    println()
    val mapLinksForward = mutableMapOf<Long, Long>()
    val mapLinksBackwards = mutableMapOf<Long, Long>()

    for (i in nums.indices) {
      val f = pad(nums[i], i)
      val s = pad(nums[(i + 1) % nums.size], (i + 1) % nums.size)

      mapLinksForward[f] = s
      mapLinksBackwards[s] = f
    }

//    mapLinksForward[nums.last()] = nums.first()
//    mapLinksBackwards[nums.first()] = nums.last()

    println("Nums: ${nums.size}")
    println("MapSize: ${mapLinksForward.size}")
    println("BackMapSize: ${mapLinksForward.size}")

    // Move stuff
    for (i in nums.indices) {
      val num = nums[i]
      val numPad = pad(num, i)
      if (num == 0) {
        // do nothing
      } else {
        val prev = mapLinksBackwards[numPad]!!
        val next = mapLinksForward[numPad]!!
        mapLinksForward[prev] = next
        mapLinksBackwards[next] = prev

        var newPrev = prev
        var newNext = next
        // Find the insertion point
        if (num > 0) {
          repeat(num) {
            newPrev = newNext
            newNext = mapLinksForward[newNext]!!
          }
        } else {
          repeat(abs(num)) {
            newNext = newPrev
            newPrev = mapLinksBackwards[newPrev]!!
          }
        }

        mapLinksForward[newPrev] = numPad
        mapLinksForward[numPad] = newNext
        mapLinksBackwards[newNext] = numPad
        mapLinksBackwards[numPad] = newPrev
      }

//      println("Num: $num")
//      println("Forward - " + makeList(mapLinksForward).joinToString())
//      println("Backward - " + makeList(mapLinksBackwards).joinToString())
//      println()
    }

    // Reconstruct the list
    val newList = makeList(mapLinksForward)
    newList.forEach { println(it) }

    val thou = newList[1000 % newList.size]
    val twothou = newList[2000 % newList.size]
    val theethou = newList[3000 % newList.size]

    println("List size: ${newList.size} 1000: $thou 2000: $twothou 3000: $theethou")

    return thou.toLong() + twothou + theethou
  }

  private fun makeList(mapLinksForward: MutableMap<Long, Long>): MutableList<Long> {
    val newList = mutableListOf<Long>()
    var next = mapLinksForward.values.first { unpad(it) == 0L }
    for (n in nums.indices) {
      newList.add(unpad(next))
      next = mapLinksForward[next]!!
    }
    return newList
  }

  fun part2(): Long {
    val multiple = 811589153L

    println()
    val mapLinksForward = mutableMapOf<Long, Long>()
    val mapLinksBackwards = mutableMapOf<Long, Long>()

    for (i in nums.indices) {
      val f = pad(nums[i], i)
      val s = pad(nums[(i + 1) % nums.size], (i + 1) % nums.size)

      mapLinksForward[f] = s
      mapLinksBackwards[s] = f
    }

    println("Nums: ${nums.size}")
    println("MapSize: ${mapLinksForward.size}")
    println("BackMapSize: ${mapLinksForward.size}")

    // 4869534918
    // Move stuff
    repeat(10) {
      for (i in nums.indices) {
        val numPad = pad(nums[i], i)
        val multiNum = nums[i] * multiple
        val multiNumModSize = Math.floorMod(multiNum, nums.size - 1)
//        val multiNumModSize = (multiNum % nums.size).toInt()

        if (multiNumModSize == 0) {
          // do nothing
        } else {
          val prev = mapLinksBackwards[numPad]!!
          val next = mapLinksForward[numPad]!!
          mapLinksForward[prev] = next
          mapLinksBackwards[next] = prev

          var newPrev = prev
          var newNext = next
          // Find the insertion point
          if (multiNumModSize > 0) {
            repeat(multiNumModSize) {
              newPrev = newNext
              newNext = mapLinksForward[newNext]!!
            }
          } else {
            repeat(abs(multiNumModSize)) {
              newNext = newPrev
              newPrev = mapLinksBackwards[newPrev]!!
            }
          }

          mapLinksForward[newPrev] = numPad
          mapLinksForward[numPad] = newNext
          mapLinksBackwards[newNext] = numPad
          mapLinksBackwards[numPad] = newPrev
        }
      }
      // 1623178306

      val printable = makeList(mapLinksForward).map { it * multiple }
      println("$it $printable")
      println()
    }

    // Reconstruct the list
    val newList = makeList(mapLinksForward)
    newList.forEach { println(it) }

    val thou = newList[1000 % newList.size] * multiple
    val twothou = newList[2000 % newList.size] * multiple
    val theethou = newList[3000 % newList.size] * multiple

    println("List size: ${newList.size} 1000: $thou 2000: $twothou 3000: $theethou")

    return thou.toLong() + twothou + theethou
  }
}

fun main() {
  val day = "20".toInt()

  val todayTest = Day20(readInput(day, 2022, true))
//  execute(todayTest::part1, "Day[Test] $day: pt 1")

  val today = Day20(readInput(day, 2022))
//  execute(today::part1, "Day $day: pt 1")
  // 18612
  // 25251

  // execute(todayTest::part2, "Day[Test] $day: pt 2", 1623178306)
 execute(today::part2, "Day $day: pt 2")
}
