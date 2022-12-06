private class Day06(val lines: List<String>) {
  init {
    lines.forEach { println(it) }
  }

  fun part1(): Any {
    return lines[0].windowed(4).indexOfFirst { it.toSet().size == 4 } + 4
  }

  fun part2(): Any {
    return lines[0].windowed(14).indexOfFirst { it.toSet().size == 14 } + 14
  }
}

fun main() {
  val day = "06".toInt()

  val todayTest = Day06(readInput(day, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1")

  val today = Day06(readInput(day))
  execute(today::part1, "Day $day: pt 1")

  execute(todayTest::part2, "Day[Test] $day: pt 2")
  execute(today::part2, "Day $day: pt 2")
}
