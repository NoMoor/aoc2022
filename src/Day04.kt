private class Day04(lines: List<String>) {
  init {
    println(lines)
  }

  private val lines = lines.map {
    it.split(",").map { it.split("-").map { it.toInt() }.let { it.toRange() } }.let { it.toPair() }
  }

  fun IntRange.contains(b: IntRange): Boolean {
    return this.first <= b.first && b.last <= this.last
  }

  fun IntRange.overlaps(b: IntRange): Boolean {
    return this.contains(b.first) || this.contains(b.last) || b.contains(this.first)
  }

  fun part1(): Int {
    return lines.count {
      return@count it.first.contains(it.second) || it.second.contains(it.first)
    }
  }

  fun part2(): Int {
    return lines.count {
      return@count it.first.overlaps(it.second)
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
