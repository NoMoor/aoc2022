fun main() {
  fun part1(input: List<String>): Int {
    val elfs = input.joinToString("\n")
      .split("\n\n")
      .map { it.split("\n").sumOf { it.toInt() } }

    return elfs.max()
  }

  fun part2(input: List<String>): Int {
    val elfs = input.joinToString("\n")
      .split("\n\n")
      .map { it.split("\n").sumOf { it.toInt() } }

    return elfs.sorted().takeLast(3).sum()
  }

  val input = readInput(1)
  output(part1(input))
  output(part2(input))
}
