private class Day07(val lines: List<String>) {

  val rootDir = Directory("/")
  var currentDirectory = rootDir
  var directories = mutableMapOf<Directory, Long>()

  init {
    for (l in lines) {
      if (l.startsWith("$ cd")) {
        val dir = l.split(" ").last()
        if (dir == "..") {
          currentDirectory = currentDirectory.parent!!
        } else if (dir == "/") {
          currentDirectory = rootDir
        } else {
          currentDirectory = currentDirectory.directories.first { it.name == dir }
        }
      } else if (l.startsWith("dir")) {
        val newDir = Directory(l)
        currentDirectory.addDir(newDir)
      } else if (l.startsWith("$ ls")) {
        continue
      } else {
        currentDirectory.addFile(File(l))
      }
    }

    rootDir.computeSizes(directories)
  }

  fun part1(): Any {
    println(directories)
    return directories.values.filter { it < 100000 }.sum()
  }

  fun part2(): Any {
    val totalSize = 70000000L
    val freeSize = totalSize - directories[rootDir]!!
    val neededSize = 30000000L
    val toDelete = neededSize - freeSize

    return directories.values.filter { it >= toDelete }.min()
  }

  data class Directory(var command: String) {
    val name = command.split(" ").last()

    fun addDir(newDir: Directory) {
      directories.add(newDir)
      newDir.parent = this
    }

    fun addFile(file: File) {
      files.add(file)
    }

    fun computeSizes(index: MutableMap<Directory, Long>) : Long {
      val directorySize = this.directories.sumOf { it.computeSizes(index) }
      val fileSize = files.sumOf { it.size }

      val size = directorySize + fileSize

      index[this] = size
      return size
    }

    override fun hashCode(): Int {
      return super.hashCode()
    }

    val files = mutableListOf<File>()
    val directories = mutableListOf<Directory>()
    var parent: Directory? = null
  }

  data class File(val command: String) {
    val name: String = command.split(" ")[1]
    val size: Long = command.split(" ")[0].toLong()
  }
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
