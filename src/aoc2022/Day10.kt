private class Day10(val lines: List<String>) {
  init {
    lines.forEach { println(it) }
  }

  fun part1(): Any {
    val signalStrengths = mutableListOf<Int>()
    var x = 1
    var cycle = 0

    fun doCycle() {
      cycle += 1
      if (cycle % 40 == 20) signalStrengths.add(x * cycle)
    }

    for (l in lines) {
      val parts = l.split(" ")
      doCycle()
      if (parts[0].startsWith("addx")) {
        doCycle()

        x += parts[1].toInt()
      }
    }

    return signalStrengths.sum()
  }

  fun part2(): Any {
    val crt = MutableList(6) { MutableList(40) { '.' } }
    var x = 1
    var cycle = 0

    fun doCycle() {
      cycle += 1
      val spriteRange = x - 1..x + 1
      val drawRow = (cycle - 1) / 40
      val drawCol = (cycle - 1) % 40

      crt[drawRow][drawCol] = if (drawCol in spriteRange) '#' else '.'
    }

    for (l in lines) {
      val parts = l.split(" ")
      doCycle()
      if (parts[0].startsWith("addx")) {
        doCycle()
        x += parts[1].toInt()
      }
    }

    return crt.joinToString("\n") { (it.joinToString("")) }
  }
}

fun main() {
  val day = "10".toInt()

  val todayTest = Day10(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1", 13140)

  val today = Day10(readInput(day, 2022))
  execute(today::part1, "Day $day: pt 1")

  execute(todayTest::part2, "Day[Test] $day: pt 2", part2TestSolution)
  execute(today::part2, "Day $day: pt 2", part2Solution)
}

private const val part2TestSolution = """##..##..##..##..##..##..##..##..##..##..
###...###...###...###...###...###...###.
####....####....####....####....####....
#####.....#####.....#####.....#####.....
######......######......######......####
#######.......#######.......#######....."""

private const val part2Solution = """####.###...##..###..#....####.####.#..#.
...#.#..#.#..#.#..#.#....#.......#.#..#.
..#..#..#.#..#.#..#.#....###....#..#..#.
.#...###..####.###..#....#.....#...#..#.
#....#.#..#..#.#.#..#....#....#....#..#.
####.#..#.#..#.#..#.####.#....####..##.."""
