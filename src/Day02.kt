private class Day02(lines: List<String>) {
  init { println(lines) }
  private val rounds = lines.map { listOf(it[0].code - 'A'.code, it[2].code - 'X'.code) }

  fun part1(): Int {
    return rounds.map { r ->
      // Computes the result of the round.
      // Lose = 0
      // Tie = 3
      // Win = 6
      val outcome = Math.floorMod(r[1] - r[0] + 1, 3) * 3

      (r[1] + 1) + outcome
    }.sum()
  }

  fun part2(): Int {
    return rounds
      .map { r ->
        val them = r[0]
        val result = r[1]

        val us = when (result) {
          0 -> Math.floorMod(them - 1, 3)
          2 -> Math.floorMod(them + 1, 3)
          else -> them
        }

        us + 1 + (result * 3)
      }.sum()
  }
}

fun main() {
  val day = 2

  val todayTest = Day02(readInput(day, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1")

  val today = Day02(readInput(day))
  execute(today::part1, "Day $day: pt 1")

  execute(todayTest::part2, "Day[Test] $day: pt 2")
  execute(today::part2, "Day $day: pt 2")
}
