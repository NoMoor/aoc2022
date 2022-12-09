import java.lang.Math.abs
import kotlin.math.sign

private class Day09(val lines: List<String>) {
  init {
    lines.forEach { println(it) }
  }

  data class Coord(val x: Int, val y: Int)

  fun part1(): Any {
    val ps = mutableSetOf<Coord>()

    var head = Coord(0, 0)
    var tail = Coord(0, 0)

    for (l in lines) {
      val (dir, n) = l.split(" ")
      val num = n.toInt()

      // Move num steps in dir direction
      for (number in 0 until num) {
        head = moveHead(dir, head)

        tail = follow(head, tail)
        ps.add(tail)
      }
    }
    return ps.size
  }

  fun part2(): Any {
    val tailVisitedCoordinates = mutableSetOf<Coord>()

    var head = Coord(0, 0)
    // Make some knots
    val knots = (1 until 10).map { Coord(0, 0) }.toMutableList()

    for (l in lines) {
      val (dir, numS) = l.split(" ")
      val num = numS.toInt()

      // Move num steps in dir direction
      for (n in 0 until num) {
        head = moveHead(dir, head)

        // Move each follower knot
        for (i in knots.indices) {
          val first = if (i == 0) head else knots[i -1]
          knots[i] = follow(first, knots[i])
        }

        tailVisitedCoordinates.add(knots.last())
      }
    }
    return tailVisitedCoordinates.size
  }

  /** Moves the head one step in the specified direction. */
  private fun moveHead(dir: String, head: Coord): Coord {
    var head1 = head
    when (dir) {
      "R" -> head1 = Coord(head1.x + 1, head1.y)
      "L" -> head1 = Coord(head1.x - 1, head1.y)
      "U" -> head1 = Coord(head1.x, head1.y + 1)
      "D" -> head1 = Coord(head1.x, head1.y - 1)
    }
    return head1
  }

  /** Outputs where b is after following a. If a is adjacent to b, b does not move. */
  fun follow(a: Coord, b: Coord) : Coord {
    if (abs(a.x - b.x) > 1 || abs(a.y - b.y) > 1) {
      return Coord(b.x + (a.x - b.x).sign, b.y + (a.y - b.y).sign)
    }
    return b
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
