private class Day08(val lines: List<String>) {

  private val grid: List<List<Int>>

  init {
    lines.forEach { println(it) }

    grid = lines.map { it.map { it.digitToInt() } }
  }

  fun part1(): Any {
    return grid.indices.sumOf { r ->
      grid[r].indices.count { c ->
        isVisible(r, c)
      }
    }
  }

  private fun isVisible(r: Int, c: Int): Boolean {
    val height = grid[r][c]
    val row = grid[r]
    if (r == 0 || r == row.size -1) {
      return true
    }
    if (c == 0 || c == grid.size -1) {
      return true
    }

    val left = row.subList(0, c).max()
    if (height > left) {
      return true
    }
    val right = row.subList(c + 1, grid.size).max()
    if (height > right) {
      return true
    }

    val up = grid.map { it[c] }.subList(0, r).max()
    if (height > up) {
      return true
    }
    val down = grid.map { it[c] }.subList(r + 1, grid.size).max()
    if (height > down) {
      return true
    }
    return false
  }

  private fun treeScore(r: Int, c: Int): Int {
    val height = grid[r][c]
    val row = grid[r]
    if (r == 0 || r == row.size -1 || c == 0 || c == grid.size -1) {
      return 0
    }

    fun getScore(treeLine: List<Int>) : Int {
      var count = 0
      for (t in treeLine) {
        count++
        if (t >= height) {
          break
        }
      }
      return count
    }

    val left = getScore(row.subList(0, c).reversed())
    val right = getScore(row.subList(c + 1, grid.size))
    val up = getScore(grid.map { it[c] }.subList(0, r).reversed())
    val down = getScore(grid.map { it[c] }.subList(r + 1, grid.size))

    return left * right * up * down
  }

  fun part2(): Any {
    return grid.indices.maxOf { r ->
      grid[r].indices.maxOf { c ->
        treeScore(r, c)
      }
    }
  }

  companion object {
    fun doDay() {
      val day = "08".toInt()

      val todayTest = Day08(readInput(day, 2022, true))
      execute(todayTest::part1, "Day[Test] $day: pt 1", 21)

      val today = Day08(readInput(day, 2022))
      execute(today::part1, "Day $day: pt 1", 1798)

      execute(todayTest::part2, "Day[Test] $day: pt 2", 8)
      execute(today::part2, "Day $day: pt 2", 259308)
    }
  }
}

fun main() {
  Day08.doDay()
}
