private class Day07(lines: List<String>) {
  data class Directory(var size: Long = 0) // Container to be able to change the value in dirs and path

  val dirs = mutableListOf<Directory>() // A list containing the sizes of all directories
  val path = mutableListOf<Directory>() // A stack containing the current path of directories

  init {
    lines.forEach {
      val parts = it.split(" ")
      when (parts[0]) {
        "$" -> // Commands
          when (parts[1]) {
            "cd" ->
              when (parts[2]) {
                ".." -> path.removeLast()
                else -> {
                  val d = Directory()
                  dirs.add(d)
                  path.add(d)
                }
              }
            else -> Unit // Do nothing for ls
          }
        "dir" -> Unit // Do nothing for dir
        // File
        else -> path.forEach { it.size += parts[0].toLong() }
      }
    }
  }

  fun part1(): Any {
    return dirs.map { it.size }.filter { it <= 100000 }.sum()
  }

  fun part2(): Any {
    val usedSpace = dirs.first().size
    val totalSpace = 70000000
    val freeSpace = totalSpace - usedSpace
    val toDelete = 30000000 - freeSpace
    return dirs.map { it.size }.filter { it >= toDelete }.min()
  }
}

fun main() {
  val day = "07".toInt()

  val todayTest = Day07(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1", 95437L)

  val today = Day07(readInput(day, 2022))
  execute(today::part1, "Day $day: pt 1", 1845346L) // 1523801 // 1540861

  execute(todayTest::part2, "Day[Test] $day: pt 2", 24933642L)
  execute({ Day07(readInput(day, 2022)).part2() }, "Day $day: pt 2", 3636703L)
}
