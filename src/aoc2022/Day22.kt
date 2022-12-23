package aoc2022

import utils.*
import utils.Coord.Companion.DOWN
import utils.Coord.Companion.LEFT
import utils.Coord.Companion.RIGHT
import utils.Coord.Companion.UP
import utils.Coord.Companion.get

private class Day22(val lines: List<String>) {
  val map = lines.splitBy { it == "" }[0].map { it.toList() }
  val charMap = MutableList(map.size) { MutableList(map.maxOf { it.size }) { ' ' } }
  val directions = lines.splitBy { it == "" }[1][0].toList()

  fun part1(): Long {
    println("Map size: ${map.size} ${map.maxOf { it.size }}")
    println("charMap size: ${charMap.size} ${charMap.last().size}")
    for (r in map.indices) {
      val row = map[r]
      for (c in row.indices) {
        val char = row[c]
        if (char == '#') {
          charMap[r][c] = '#'
        } else if (charMap[r][c] == ' ') {
          charMap[r][c] = row[c]
        }
      }
    }

    var loc = Coord.xy(map[0].indexOfFirst { it == '.' }, 0)
    var dir = RIGHT

    val instructions = parseInstructions().debug()
    for (i in instructions) {
      println("Do instruction $i for loc $loc dir $dir")
      if (i[0].isLetter()) {
        dir = changeDirection(i, dir)
        drawDir(loc, dir)
      } else {
        printMap()

        val step = i.toInt()
        for (n in 0 until step) {
          if (map[loc] == ' ' || map[loc] == '#') {
            throw RuntimeException("Shouldn't be here $loc ${map[loc]}}")
          }

          drawDir(loc, dir)
          val forward = loc + dir
          println("Move forward $forward")

          var nextTile = ' '
          if (forward.y in map.indices && forward.x in map[forward.y].indices) {
            nextTile = map[forward]
          }

          if (nextTile.isWhitespace()) {
            println("Moving off the screen. $dir $loc")

            if (dir == UP) {
              val wrappedLoc = firstInCol(forward.x)

              if (wrappedLoc.second == '.') {
                loc = wrappedLoc.first
                drawDir(loc, dir)
                continue
              } else {
                println("Tried to wrap downward around to a wall.. ")
                break
              }
            } else if (dir == DOWN) {
              val wrappedLoc = lastInCol(forward.x)

              if (wrappedLoc.second == '.') {
                loc = wrappedLoc.first
                drawDir(loc, dir)
                continue
              } else {
                println("Tried to wrap upward around to a wall.. ")
                break
              }
            } else if (dir == RIGHT) {
              val wrappedLoc = firstInRow(forward.r)

              if (wrappedLoc.second == '.') {
                loc = wrappedLoc.first
                drawDir(loc, dir)
                continue
              } else {
                println("Tried to wrap righward around to a wall.. ")
                break
              }
            } else if (dir == LEFT) {
              val last = lastInRow(forward.y)

              if (last.second == '.') {
                loc = last.first
                drawDir(loc, dir)
                continue
              } else {
                println("Tried to wrap leftward around to a wall.. ")
                break
              }
            }
          } else if (map[loc + dir] == '.') {
            loc += dir
            drawDir(loc, dir)
          } else if (map[loc + dir] == '#') {
            println("Hit the wall")
            break
          }
        }
      }
    }

    val facingVal = dirValue(dir)

    printMap()

    val returnVal = 1000 * (loc.r + 1) + 4 * (loc.c + 1) + facingVal.toLong()

    println("loc $loc")
    println("dir $dir")

    return returnVal
  }

  private fun dirValue(dir: Coord) = when (dir) {
    RIGHT -> 0
    DOWN -> 3
    LEFT -> 2
    UP -> 1
    else -> throw RuntimeException("Nope $dir")
  }

  private fun drawDir(loc: Coord, dir: Coord) {
    val char = when (dir) {
      DOWN -> '^'
      RIGHT -> '>'
      LEFT -> '<'
      else -> 'V'
    }

    charMap[loc.r][loc.c] = char
  }

  private fun parseInstructions(): MutableList<String> {
    val instructions = mutableListOf<String>()
    var next = ""
    for (c in directions) {
      if (next.isEmpty()) {
        next += c
      } else if (next[0].isDigit()) {
        if (c.isDigit()) {
          next += c
        } else {
          instructions.add(next)
          next = c.toString()
        }
      } else {
        if (c.isLetter()) {
          next += c
        } else {
          instructions.add(next)
          next = c.toString()
        }
      }
    }
    if (next.isNotEmpty()) {
      instructions.add(next)
    }
    return instructions
  }

  fun part2(): Long {
    println("Map size: ${map.size} ${map.maxOf { it.size }}")
    println("charMap size: ${charMap.size} ${charMap.last().size}")
    for (r in map.indices) {
      val row = map[r]
      for (c in row.indices) {
        val char = row[c]
        if (char == '#') {
          charMap[r][c] = '#'
        } else if (charMap[r][c] == ' ') {
          charMap[r][c] = row[c]
        }
      }
    }

    var loc = Coord.xy(map[0].indexOfFirst { it == '.' }, 0)
    var dir = RIGHT

    val instructions = parseInstructions().debug()
    for (i in instructions) {
      println("Do instruction $i for loc $loc dir $dir")
      if (i[0].isLetter()) {
        dir = changeDirection(i, dir)
        drawDir(loc, dir)
      } else {
        printMap()

        val step = i.toInt()
        for (n in 0 until step) {
          if (map[loc] == ' ' || map[loc] == '#') {
            throw RuntimeException("Shouldn't be here $loc ${map[loc]}}")
          }

          drawDir(loc, dir)
          val forward = loc + dir
          println("Move forward $forward")

          var nextTile = ' '
          if (forward.y in map.indices && forward.x in map[forward.y].indices) {
            nextTile = map[forward]
          }

          if (nextTile.isWhitespace()) {
            println("Moving off the screen. $dir $loc")

            var newDir: Coord? = null
            var wrappedLoc: Pair<Coord, Char>? = null

            if (dir == UP) {
              println("Move through wall up")
              when (forward.c) {
                in 0..49 -> {
                  newDir = UP
                  val newCol = forward.c + 100
                  wrappedLoc = firstInCol(newCol)
                }
                in 50 .. 99 -> {
                  newDir = LEFT
                  val newRow = (forward.c - 50) + 150
                  wrappedLoc = lastInRow(newRow)
                }
                in 100 .. 149 -> {
                  newDir = LEFT
                  val newRow = (forward.c - 100) + 50
                  wrappedLoc = lastInRow(newRow)
                }
                else -> throw RuntimeException("Nope")
              }
            } else if (dir == DOWN) {
              println("Move through wall down")
              when (forward.c) {
                in 0..49 -> {
                  newDir = RIGHT
                  val newRow = forward.c + 50
                  wrappedLoc = firstInRow(newRow)
                }
                in 50 .. 99 -> {
                  newDir = RIGHT
                  val newRow = (forward.c - 50) + 150
                  wrappedLoc = firstInRow(newRow)
                }
                in 100 .. 149 -> {
                  newDir = DOWN
                  val newCol = (forward.c - 100)
                  wrappedLoc = lastInCol(newCol)
                }
                else -> throw RuntimeException("Nope")
              }
            } else if (dir == RIGHT) {
              println("Move through wall right")
              when (forward.r) {
                in 0..49 -> {
                  newDir = LEFT
                  val newRow = 149 - forward.r
                  wrappedLoc = lastInRow(newRow)
                }
                in 50 .. 99 -> {
                  newDir = DOWN
                  val newCol = (forward.r - 50) + 100
                  wrappedLoc = lastInCol(newCol)
                }
                in 100 .. 149 -> {
                  newDir = LEFT
                  val newRow = 49 - (forward.r - 100)
                  wrappedLoc = lastInRow(newRow)
                }
                in 150 .. 199 -> {
                  newDir = DOWN
                  val newCol = (forward.r - 150) + 50
                  wrappedLoc = lastInCol(newCol)
                }
                else -> throw RuntimeException("Nope")
              }
            } else if (dir == LEFT) {
              println("Move through wall left")
              when (forward.r) {
                in 0..49 -> {
                  newDir = RIGHT
                  val newRow = 149 - forward.r
                  wrappedLoc = firstInRow(newRow)
                }
                in 50 .. 99 -> {
                  newDir = UP
                  val newCol = (forward.r - 50)
                  wrappedLoc = firstInCol(newCol)
                }
                in 100 .. 149 -> {
                  newDir = RIGHT
                  val newRow = 49 - (forward.r - 100)
                  wrappedLoc = firstInRow(newRow)
                }
                in 150 .. 199 -> {
                  newDir = UP
                  val newCol = (forward.r - 150) + 50
                  wrappedLoc = firstInCol(newCol)
                }
                else -> throw RuntimeException("Nope")
              }
            }

            if (wrappedLoc!!.second == '.') {
              loc = wrappedLoc.first
              dir = newDir!!
              drawDir(loc, dir)
              continue
            } else {
              println("Tried to wrap downward around to a wall.. ")
              break
            }

          } else if (map[loc + dir] == '.') {
            loc += dir
            drawDir(loc, dir)
          } else if (map[loc + dir] == '#') {
            println("Hit the wall")
            break
          }
        }
      }
    }

    val facingVal = dirValue(dir)

    printMap()

    val returnVal = 1000 * (loc.r + 1) + 4 * (loc.c + 1) + facingVal.toLong()

    println("loc $loc")
    println("dir $dir")

    return returnVal
  }

  private fun printMap() {
    println()
    charMap.forEach { println(it.joinToString("")) }
    println()
  }

  private fun firstInRow(row: Int) : Pair<Coord, Char> {
    val first = map[row].withIndex().first {
      val v = it.value
      v == '.' || v == '#'
    }

    val newLoc = Coord.rc(row, first.index)

    return newLoc to map[newLoc]
  }

  private fun lastInRow(row: Int) : Pair<Coord, Char> {
    val last = map[row].withIndex().last {
      val v = it.value
      v == '.' || v == '#'
    }

    val newLoc = Coord.rc(row, last.index)

    return newLoc to map[newLoc]
  }

  private fun firstInCol(col: Int) : Pair<Coord, Char> {
    val first = map.withIndex().first {
      if (it.value.size <= col) {
        return@first false
      }

      val v = it.value[col]
      v == '.' || v == '#'
    }

    val newLoc = Coord.rc(first.index, col)

    return newLoc to map[newLoc]
  }

  private fun lastInCol(col: Int) : Pair<Coord, Char> {
    val last = map.withIndex().last {
      if (it.value.size <= col) {
        return@last false
      }

      val v = it.value[col]
      v == '.' || v == '#'
    }

    val newLoc = Coord.rc(last.index, col)

    return newLoc to map[newLoc]
  }

  private fun changeDirection(i: String, dir: Coord): Coord {
    var dir1 = dir
    if (i == "R") {
      dir1 = when (dir1) {
        RIGHT -> UP
        UP -> LEFT
        LEFT -> DOWN
        DOWN -> RIGHT
        else -> throw RuntimeException("noooo")
      }
    } else {
      dir1 = when (dir1) {
        RIGHT -> DOWN
        UP -> RIGHT
        LEFT -> UP
        DOWN -> LEFT
        else -> throw RuntimeException("noooo")
      }
    }
    return dir1
  }
}

fun main() {
  val day = "22".toInt()

   val todayTest = Day22(readInput(day, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1", 6032L)

   val today = Day22(readInput(day, 2022))
   execute(today::part1, "Day $day: pt 1", 149250L)
  // Wrong answer: 149258

//  val todayTest2 = Day22(readInput(day, 2022, true, 2))
//  execute(todayTest2::part2, "Day[Test] $day: pt 2")
   execute(today::part2, "Day $day: pt 2", 12462)

  // Wrong answer: 165005 -- too high
}
