@file:Suppress("DataClassPrivateConstructor")

package utils

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Coord private constructor(val c: Int, val r: Int) {
  val x = c
  val y = r

  private constructor(p: Pair<Int, Int>) : this(p.first, p.second)

  operator fun plus(o: Coord): Coord = Coord(c + o.c, r + o.r)
  operator fun plus(o: Pair<Int, Int>): Coord = this + Coord(o)
  operator fun minus(o: Coord): Coord = Coord(c - o.c, r - o.r)
  operator fun minus(o: Pair<Int, Int>): Coord = this - Coord(o)

  operator fun times(i: Int): Coord = Coord(c * i, r * i)
  operator fun div(i: Int): Coord = Coord(c / i, r / i)

  private val horizontalSpecs = listOf("o", "O", "0", "+", "-", "_")
  private val verticalSpecs = listOf("o", "O", "0", "+", "i", "I", "|")
  private val rightDiagonalSpecs = listOf("o", "O", "0", "x", "X", "/")
  private val leftDiagonalSpecs = listOf("o", "O", "0", "x", "X", "\\")

  /**
   * Neighbors based on the provided string spec.
   * Strings are based on the shape of the neighbors (ex: o is all adjacent neighbors. - is horizontal neighborss only).
   */
  fun neighbors(spec: String = "o") : List<Coord> {
    val adjacent = mutableListOf<Coord>()
    if (spec in horizontalSpecs) {
      adjacent.addAll(listOf(-1 to 0, 1 to 0).map { this + it })
    }
    if (spec in verticalSpecs) {
      adjacent.addAll(listOf(0 to -1, 0 to 1).map { this + it })
    }
    if (spec in rightDiagonalSpecs) {
      adjacent.addAll(listOf(-1 to -1, 1 to 1).map { this + it })
    }
    if (spec in leftDiagonalSpecs) {
      adjacent.addAll(listOf(-1 to 1, 1 to -1).map { this + it })
    }
    return adjacent
  }

  fun neighborsBounded(xRange: IntRange, yRange: IntRange, spec: String = "o"): List<Coord> {
    return neighbors(spec)
      .filter { it.c in xRange && it.r in yRange }
      .toList()
  }

  fun neighborsBounded(width: Int, height: Int, spec: String = "o"): List<Coord> {
    return neighborsBounded(0 until width, 0 until height, spec)
  }

  operator fun compareTo(o: Coord) : Int {
    return compareBy<Coord> { it.x }.thenComparingInt { it.y }.compare(this, o)
  }

  operator fun rangeTo(b: Coord) : CoordRange {
    return CoordRange.from(this, b)
  }

  companion object {
    val LEFT = Coord(-1, 0)
    val RIGHT = Coord(1, 0)
    val UP = Coord(0, 1)
    val DOWN = Coord(0, -1)

    fun xy(x: Int, y: Int): Coord {
      return Coord(x, y)
    }

    fun rc(r: Int, c: Int): Coord {
      return Coord(c, r)
    }

    operator fun <E> List<List<E>>.get(c: Coord) : E {
      return this[c.r][c.c]
    }

    operator fun <E> MutableList<MutableList<E>>.set(c: Coord, e: E) {
      this[c.r][c.c] = e
    }

    operator fun Pair<Coord, Coord>.contains(o: Coord) : Boolean {
      val xRange = if (first.c <= second.c) (first.c..second.c) else (second.c..first.c)
      val yRange = if (first.r <= second.r) (first.r..second.r) else (second.r..first.r)

      return o.c in xRange && o.r in yRange
    }
  }
}

data class CoordRange private constructor(val xRange: IntRange, val yRange: IntRange) {
  val cRange = xRange
  val rRange = yRange

  companion object {
    fun from(xRange: IntRange, yRange: IntRange) : CoordRange {
      return CoordRange(
        min(xRange.first, xRange.last)..max(xRange.first, xRange.last),
        min(yRange.first, yRange.last)..max(yRange.first, yRange.last))
    }

    fun from(a: Coord, b: Coord) : CoordRange {
      return CoordRange(min(a.x, b.x)..max(a.x, b.x), min(a.y, b.y)..max(a.y, b.y))
    }


    fun List<List<*>>.toCoordRange() : CoordRange {
      val xEnd = if (this.isEmpty()) 0 else this[0].size
      return from(0 until xEnd, 0 until this.size)
    }
  }

  operator fun contains(o: Coord) : Boolean {
    return o.x in xRange && o.y in yRange
  }

  fun expand(left: Int = 0, right: Int = 0, up: Int = 0, down: Int = 0) : CoordRange {
    return Coord.xy(xRange.first + left, yRange.first + up) .. Coord.xy(xRange.last + right, yRange.last + down)
  }
}