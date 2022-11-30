fun main() {
  fun part1(input: List<String>): Int {
    return input.size
  }

  fun part2(input: List<String>): Int {
    return input.size
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput(1, test=true)
  output(part1(testInput), "test")

  val input = readInput(1)
  output(part1(input), "pt1")
  output(part2(input), "pt2")
}
