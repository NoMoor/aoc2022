import java.lang.Math.abs
import kotlin.math.sign

private class Day09(val lines: List<String>) {
  init {
    lines.forEach { println(it) }
  }

  data class Coord(val x: Int, val y: Int) {

  }

  fun part1(): Any {
    val ps = mutableSetOf<Coord>()

    var head = Coord(0, 0)
    var tail = Coord(0, 0)

    for (l in lines) {
      val (dir, n) = l.split(" ")
      val num = n.toInt()

      for (number in 0 until num) {
        when (dir) {
          "R" -> head = Coord(head.x + 1, head.y)
          "L" -> head = Coord(head.x - 1, head.y)
          "U" -> head = Coord(head.x, head.y + 1)
          "D" -> head = Coord(head.x, head.y - 1)
        }

        tail = follow(head, tail)

        ps.add(tail)
      }
    }
    return ps.size
  }

  // Output where b is afterward
  fun follow(a: Coord, b: Coord) : Coord {
    if (a.x == b.x && abs(a.y - b.y) > 1) {
      // Move up or down
      return Coord(b.x, b.y + (a.y - b.y).sign)
    } else if (a.y == b.y && abs(a.x - b.x) > 1) {
      // Move left or right
      return Coord(b.x + (a.x - b.x).sign, b.y)
    } else if (abs(a.y - b.y) + abs(a.x - b.x) >= 3) {
      // move diagonally
      return Coord(b.x + (a.x - b.x).sign, b.y + (a.y - b.y).sign)
    }
    return b
  }

  fun part2(): Any {
    val ps = mutableSetOf<Coord>()

    var head = Coord(0, 0)
    // Make some knots
    val knots = (1 until 10).map { Coord(0, 0) }.toMutableList()

    for (l in lines) {
      println(l)
      val (dir, n) = l.split(" ")
      val num = n.toInt()

      for (number in 0 until num) {
        when (dir) {
          "R" -> head = Coord(head.x + 1, head.y)
          "L" -> head = Coord(head.x - 1, head.y)
          "U" -> head = Coord(head.x, head.y + 1)
          "D" -> head = Coord(head.x, head.y - 1)
        }

        for (i in knots.indices) {
          val first = if (i == 0) head else knots[i -1]
          val second = knots[i]

          val newPosition = follow(first, second)
          knots[i] = newPosition
        }

        ps.add(knots.last())
      }
    }
    return ps.size
  }
}


fun main() {
  val day = "09".toInt()

  val todayTest = Day09(readInput(9, 2022, true))
  execute(todayTest::part1, "Day[Test] $day: pt 1", 13)

  val today = Day09(readInput(day, 2022))
  execute(today::part1, "Day $day: pt 1", 6464)

  val todayTest2 = Day09(readInput(9, 2022, true, 2))
  execute(todayTest2::part2, "Day[Test] $day: pt 2", 36)
  execute(today::part2, "Day $day: pt 2", 2604)
}
