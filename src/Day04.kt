private class Day04(lines: List<String>) {
  init {
    println(lines)
  }

  private val lines = lines.map {
    it.split(",").map {
      val s = it.split("-")
      s[0].toInt()..s[1].toInt()
    }
  }

  fun part1(): Int {
    return lines.count {
      return@count it[0].all(it[1]::contains) || it[1].all(it[0]::contains)
    }
  }

  fun part2(): Int {
    return lines.count {
      return@count it[0].any(it[1]::contains) || it[1].any(it[0]::contains)
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
