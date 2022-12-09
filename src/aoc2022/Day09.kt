import java.rmi.UnexpectedException
import kotlin.math.sign

private class Day09(val lines: List<String>) {
  init {
    lines.forEach { println(it) }
  }

  fun part1(): Any {
    val tailVisitedCoordinates = mutableSetOf<Coord>()

    var head = Coord(0, 0)
    var tail = Coord(0, 0)

    for (l in lines) {
      val (dir, n) = l.split(" ")

      // Move num steps in dir direction
      repeat(n.toInt()) {
        head = moveHead(dir, head)
        tail = follow(head, tail)

        tailVisitedCoordinates.add(tail)
      }
    }
    return tailVisitedCoordinates.size
  }

  fun part2(): Any {
    val tailVisitedCoordinates = mutableSetOf<Coord>()

    // Make some knots
    val knots = (0 until 10).map { Coord(0, 0) }.toMutableList()

    for (l in lines) {
      val (dir, n) = l.split(" ")

      // Move num steps in dir direction
      repeat(n.toInt()) {
        knots[0] = moveHead(dir, knots[0])

        // Move each follower knot
        for (i in 1 until knots.size) {
          knots[i] = follow(knots[i - 1], knots[i])
        }

        tailVisitedCoordinates.add(knots.last())
      }
    }
    return tailVisitedCoordinates.size
  }

  /** Moves the head one step in the specified direction. */
  private fun moveHead(dir: String, head: Coord): Coord {
    return head + when (dir) {
      "R" -> Coord.RIGHT
      "L" -> Coord.LEFT
      "U" -> Coord.UP
      "D" -> Coord.DOWN
      else -> throw UnexpectedException("This should not happen $dir")
    }
  }

  /** Outputs where b is after following a. If a is adjacent to b, b does not move. */
  fun follow(a: Coord, b: Coord) : Coord {
    if (b !in a.neighbors()) {
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
