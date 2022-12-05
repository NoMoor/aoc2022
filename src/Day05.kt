private class Day05(l: List<String>) {
  init {
    println(l)
  }

  private val lines = l.joinToString("\n").split("\n\n")

  private val startingStacks = this.lines[0].lines()
  private val stackCount = startingStacks.reversed()[0].last().digitToInt()
  private val moves = this.lines[1].lines()

  fun part1(): String {
    val stacks = MutableList<MutableList<Char>>(stackCount) { mutableListOf() }
    startingStacks.reversed().drop(1)
      .forEach {
        it.chunked(4).forEachIndexed { i, str ->
          if (str[1].isLetter()) stacks[i].add(str[1])
        }
      }

    for (m in moves) {
      val parts = m.split(" ")
      val num = parts[1].toInt()
      val s = parts[3].toInt() - 1
      val d = parts[5].toInt() - 1

      stacks[d].addAll(stacks[s].takeLast(num).reversed())
      repeat(num) { stacks[s].removeLast() }
    }

    return stacks.joinToString(separator = "") { if (it.isNotEmpty()) it.last().toString() else "" }
  }

  fun part2(): String {
    val stacks = MutableList<MutableList<Char>>(stackCount) { mutableListOf() }
    startingStacks.reversed().drop(1)
      .forEach {
        it.chunked(4).forEachIndexed { i, str ->
          if (str[1].isLetter()) stacks[i].add(str[1])
        }
      }

    for (m in moves) {
      val parts = m.split(" ")
      val num = parts[1].toInt()
      val s = parts[3].toInt() - 1
      val d = parts[5].toInt() - 1

      stacks[d].addAll(stacks[s].takeLast(num))
      repeat(num) { stacks[s].removeLast() }
    }

    return stacks.joinToString(separator = "") { if (it.isNotEmpty()) it.last().toString() else "" }
  }
}

fun main() {
  val day = "05".toInt()

  val todayTest = Day05(readInput(day, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1")

  val today = Day05(readInput(day))
  execute(today::part1, "Day $day: pt 1")

  execute(todayTest::part2, "Day[Test] $day: pt 2")
  execute(today::part2, "Day $day: pt 2") // Wrong guess: FWNSHLDNZ
}
