fun main() {

  val m = mapOf('A' to 0, 'B' to 1, 'C' to 2)
  val z = mapOf('X' to 0, 'Y' to 1, 'Z' to 2)

  fun part1(input: List<String>): Int {
    return input.map { t ->
      val them = m[t[0]]!!
      val us = z[t[2]]!!

      var outcome = 0
      if (them == us)
        outcome = 3
      else if ((them + 1) % 3 == us)
        outcome = 6

      us + 1 + outcome
    }.sum()
  }

  fun part2(input: List<String>): Int {
    return input
      .map { t ->
        val them = m[t[0]]!!
        val result = z[t[2]]!!

        var us = them
        if (result == 0) {
          // lose
          us = them - 1
          if (us == -1) us = 2
        } else if (result == 2) {
          // win
          us = them + 1
          if (us == 3) us = 0
        }

        us + 1 + (result * 3)
      }.sum()
  }

  val testInput = readInput(2, true)
  output(part1(testInput))

  val input = readInput(2)
  output(part1(input))
  output(part2(input))
}
