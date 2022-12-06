private class Day07(val lines: List<String>) {
  init {
    lines.forEach { println(it) }
  }

  fun part1(): Any {
    return lines.size
  }

  fun part2(): Any {
    return lines.size
  }
}

fun main() {
  val day = "07".toInt()

  val todayTest = Day07(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1")

  val today = Day07(readInput(day, 2022))
  execute(today::part1, "Day $day: pt 1")

  // execute(todayTest::part2, "Day[Test] $day: pt 2")
  // execute(today::part2, "Day $day: pt 2")
}
