private class Day05(val lines: List<String>) {
  init { lines.forEach { println(it) } }

  fun part1(): String {
    val (startingStacks, moves) = lines.splitBy { it == "" }
    val stacks = startingStacks.reversed()[0].last().digitToInt() * mutableListOf<Char>()

    startingStacks
      .dropLast(1)
      .forEach { it.chunked(4).forEachIndexed { i, str ->
          if (str[1].isLetter()) stacks[i].add(0, str[1])
        }
      }

    moves.forEach { m ->
      val (num, s, d) = m.allInts()
      stacks[d - 1].addAll(stacks[s - 1].removeLast(num).reversed())
    }

    return stacks.joinToString(separator = "") { if (it.isNotEmpty()) it.last().toString() else "" }
  }

  fun part2(): String {
    val (startingStacks, moves) = lines.splitBy { it == "" }
    val stacks = startingStacks.reversed()[0].last().digitToInt() * mutableListOf<Char>()

    startingStacks.reversed().drop(1)
      .forEach {
        it.chunked(4).forEachIndexed { i, str ->
          if (str[1].isLetter()) stacks[i].add(str[1])
        }
      }

    moves.forEach { m ->
      val (num, s, d) = m.allInts()
      stacks[d - 1].addAll(stacks[s - 1].removeLast(num))
    }

    return stacks.joinToString(separator = "") { if (it.isNotEmpty()) it.last().toString() else "" }
  }
}

fun main() {
  val day = "05".toInt()

  val todayTest = Day05(readInput(day, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1", "CMZ")

  val today = Day05(readInput(day))
  execute(today::part1, "Day $day: pt 1", "FWNSHLDNZ")

  execute(todayTest::part2, "Day[Test] $day: pt 2", "MCD")
  execute(today::part2, "Day $day: pt 2", "RNRGDNFQG") // Wrong guess: FWNSHLDNZ
}
