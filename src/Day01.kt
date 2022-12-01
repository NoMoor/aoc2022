fun main() {
  fun part1(input: String): Int {
    var elfs = mutableListOf<MutableList<Int>>()

    var elf = mutableListOf<Int>()
    for (s in input.split("\n")) {
      if (s.isEmpty()) {
        elfs.add(elf)
        elf = mutableListOf()
        continue
      }

      val i = s.toInt()
      elf.add(i)
    }
    elfs.add(elf)

    return elfs.map { it.sum() }.max()
  }

  fun part2(input: String): Int {
    val elfs = input.split("\n\n").map { it.split("\n").sumOf { it.toInt() } }

    return elfs.sorted().takeLast(3).sum()
  }

  val input = readInput(1)
  output(part1(input))
  output(part2(input))
}
