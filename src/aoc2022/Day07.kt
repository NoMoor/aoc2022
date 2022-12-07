private class Day07(lines: List<String>) {

  // Map absolute-path -> Pair(files size, list of sub-dirs)
  private val totalSizes: Map<String, Long>
  private val directories: Map<String, Directory>

  init {
    val interactions = lines
      .splitBy(retainSplitter = true) { it.startsWith("$") }
      .filter { it.isNotEmpty() }

    val currentPath = mutableListOf<String>()
    directories = mutableMapOf()

    for (interaction in interactions) {
      val commandParts = interaction[0].split(" ").drop(1)

      when (commandParts[0]) {
        "ls" -> {
          val commandOutput = interaction.drop(1)
          val size = commandOutput
            .filter { !it.startsWith("dir") }
            .sumOf { it.split(" ")[0].toLong() }
          val path = currentPath.joinToString("/")
          val absPaths = commandOutput
            .filter { it.startsWith("dir") }
            .map { it.split(" ")[1] }
            .map { "$path/$it" }

          directories[path] = Directory(size, absPaths)
        }
        "cd" -> {
          if (commandParts[1] == "..") {
            currentPath.removeLast()
          } else {
            currentPath.add(commandParts[1])
          }
        }
        else -> assert(false) { "This shouldn't happen. $interaction" }
      }
    }

    totalSizes = directories.keys.associateWith { getTotalDirectorySize(it) }
  }

  private fun getTotalDirectorySize(path: String): Long {
    val directory = directories[path]!!
    return directory.size + directory.subDirs.map { getTotalDirectorySize(it) }.sum()
  }

  fun part1(): Any {
    return totalSizes.values.filter { it < 100000 }.sum()
  }

  fun part2(): Any {
    val totalDisk = totalSizes["/"]!!
    val freeSpace = 70000000L - totalDisk
    val deleteNeed = 30000000 - freeSpace

    directories.keys.forEach { println(it) }

    return totalSizes.values.filter { it >= deleteNeed }.min() // 38090606
  }

  data class Directory(val size: Long, val subDirs: List<String>)
}

fun main() {
  val day = "07".toInt()

  val todayTest = Day07(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1", 95437L)

   val today = Day07(readInput(day, 2022))
   execute(today::part1, "Day $day: pt 1", 1845346L) // 1523801 // 1540861

   execute(todayTest::part2, "Day[Test] $day: pt 2", 24933642L)
   execute( { Day07(readInput(day, 2022)).part2() } , "Day $day: pt 2", 3636703L)
}
