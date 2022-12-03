private class Day03(lines: List<String>) {
  init { println(lines) }

  private val lines = lines

  fun part1(): Int {
    return lines.size
  }

  fun part2(): Int {
    return lines.size
  }
}

fun main() {
  val day = 3

  val todayTest = Day03(readInput(day, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1")

  val today = Day03(readInput(day))
  execute(today::part1, "Day $day: pt 1")

  execute(todayTest::part2, "Day[Test] $day: pt 2")
  execute(today::part2, "Day $day: pt 2")
}
