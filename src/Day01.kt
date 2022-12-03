private class Day01(lines: List<String>) {
  init { println(lines) }

  private val elfs = lines.joinToString("\n")
    .split("\n\n")
    .map { it.split("\n").map { s -> s.toInt() } }

  fun part1() : Int {
    return elfs.maxOf { it.sum() }
  }

  fun part2() : Int {
    return elfs.map { it.sum() }.sortedDescending().take(3).sum()
  }
}


fun main() {
  val day = 1

  val todayTest = Day01(readInput(day, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1")

  val today = Day01(readInput(day))
  execute(today::part1, "Day $day: pt 1")

  execute(todayTest::part2, "Day[Test] $day: pt 2")
  execute(today::part2, "Day $day: pt 2")
}
