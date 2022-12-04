private class Day04(lines: List<String>) {
  init {
    println(lines)
  }

  private val lines = lines
    .map { it.split(",")
      .map {
        val s = it.split("-")
        s[0].toInt() .. s[1].toInt()
      }}

  fun IntRange.contains(b: IntRange): Boolean {
    return this.first <= b.first && b.last <= this.last
  }

  fun IntRange.overlaps(b: IntRange): Boolean {
    return this.contains(b.first) || this.contains(b.last) || b.contains(this.first)
  }

  fun part1(): Int {
    return lines.count {
      return@count it[0].contains(it[1]) || it[1].contains(it[0])
    }
  }

  fun part2(): Int {
    return lines.count {
      return@count it[0].overlaps(it[1])
    }
  }
}

fun main() {
  val day = "04".toInt()

  val todayTest = Day04(readInput(day, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1")

  val today = Day04(readInput(day))
  execute(today::part1, "Day $day: pt 1")

  execute(todayTest::part2, "Day[Test] $day: pt 2")
  execute(today::part2, "Day $day: pt 2")
}
