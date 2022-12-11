import java.rmi.UnexpectedException

private class Day11(val lines: List<String>) {

  /** Parse the input into a number of monkeys. */
  fun parseInputs(): Pair<List<Monkey>, Long> {
    val monkeys = lines.splitBy { it == "" } // Groups the lines by the blank line.
      // Map the list of lines to a monkey.
      .map {
        val monkeyNum = it[0].split(" ")[1].removeSuffix(":").toLong()
        val startingItems = it[1].split(": ").last().split(", ").map { it.toLong() }
        val (op, second) = it[2].split("=")[1].removePrefix(" old ").split(" ")
        val divisor = it[3].split(" ").last().toLong()
        val trueMonkey = it[4].split(" ").last().toInt()
        val falseMonkey = it[5].split(" ").last().toInt()
        val m = Monkey(monkeyNum, op, second, divisor, trueMonkey, falseMonkey)
        m.items.addAll(startingItems)
        m
      }.toList()

    // Get the least common multiple of the test divisors to keep the total worry down.
    val lcm = monkeys.map { it.testDivisor }.reduce { acc, bigInteger -> acc * bigInteger }

    return monkeys to lcm
  }

  fun part1(): Any {
    val (monkeys) = parseInputs()

    repeat(20) {
      for (m in monkeys) {
        for (item in m.items) {
          m.inspectionCount++

          val newLevel = calcNewWorryLevel(item, m.op, m.op2)
          val relief = newLevel / 3

          monkeys[if (relief % m.testDivisor == 0L) m.trueMonkey else m.falseMonkey].items.add(relief)
        }
        m.items.clear()
      }
    }

    return computeMonkeyBusiness(monkeys)
  }

  fun part2(): Any {
    val (monkeys, lcm) = parseInputs()

    repeat(10000) {
      for (m in monkeys) {
        for (item in m.items) {
          m.inspectionCount++

          val newLevel = calcNewWorryLevel(item, m.op, m.op2)

          // Reduce the number using modulo of the lcm since addition and multiplication work the same under
          // modulo lcm since all the potential divisors are co-prime.
          // https://en.wikipedia.org/wiki/Modular_arithmetic#Properties
          val relief = newLevel % lcm

          monkeys[if (relief % m.testDivisor == 0L) m.trueMonkey else m.falseMonkey].items.add(relief)
        }
        m.items.clear()
      }
    }

    return computeMonkeyBusiness(monkeys)
  }

  private fun computeMonkeyBusiness(monkeys: List<Monkey>) : Long {
    println(monkeys.map { it.inspectionCount })

    return monkeys.map { it.inspectionCount }.sortedDescending().take(2).let { it[0] * it[1] }
  }

  private fun calcNewWorryLevel(worryLevel: Long, op: String, op2: String): Long {
    return when (op) {
      "*" -> {
        if (op2 == "old") worryLevel * worryLevel else worryLevel * op2.toLong()
      }
      "+" -> {
        if (op2 == "old") worryLevel + worryLevel else worryLevel + op2.toLong()
      }
      else -> throw UnexpectedException("This hsouldn't happen $op")
    }
  }

  data class Monkey(
    val id: Long,
    val op: String,
    val op2: String,
    val testDivisor: Long,
    val trueMonkey: Int,
    val falseMonkey: Int) {

    val items = mutableListOf<Long>()
    var inspectionCount = 0L
  }
}

fun main() {
  val day = "11".toInt()

  val todayTest = Day11(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1", 10605L)

  val today = Day11(readInput(day, 2022))
  execute(today::part1, "Day $day: pt 1", 55458L)

  execute(todayTest::part2, "Day[Test] $day: pt 2", 2713310158L)
  execute(today::part2, "Day $day: pt 2", 14508081294L)
}
