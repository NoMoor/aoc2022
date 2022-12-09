import java.awt.datatransfer.StringSelection
import java.awt.Toolkit
import java.io.File
import java.math.BigInteger
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.MessageDigest
import java.util.concurrent.Callable
import kotlin.io.path.Path
import kotlin.system.measureNanoTime

const val year = 2022
const val defaultText = "100"

/**
 * Reads lines from the given input txt file.
 */
fun readInput(dayNum: Int, year: Int, test: Boolean = false, testIndex: Int = 0): List<String> {
  val paddedDay = dayNum.toString().padStart(2, '0')
  val fileName = if (test) {
    if (testIndex == 0) "Day${paddedDay}_test.txt" else "Day${paddedDay}_test$testIndex.txt"
  } else {
    "Day${paddedDay}.txt"
  }
  val file = File(Path("src", "aoc$year").toFile(), fileName)

  if (!file.exists() || file.readText().isEmpty()) {
    file.writeText(if (test) defaultText else tryGetInputFromSite(dayNum))
  }

  return file.readLines()
}

fun tryGetInputFromSite(day: Int): String {
  println("\n\nTry to get input input from Advent of code.\n")

  val client = HttpClient.newBuilder().build()
  val request = HttpRequest.newBuilder()
    .uri(URI.create("https://adventofcode.com/$year/day/$day/input"))
    .setHeader("User-Agent", "github.com/NoMoor/aoc2022 by andrewscotthat@gmail.com")
    .setHeader("cookie", File("data", ".cookie").readText())
    .build()

  val response = client.send(request, HttpResponse.BodyHandlers.ofString())
  return response.body()
}

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
  .toString(16)

fun execute(c: Callable<Any>, label: String = "", expectedAnswer: Any = "") {
  println("\n************* Start $label *************")

  var result: Any
  val nanos = measureNanoTime { result = c.call() }

  println(" Executed in ${formatNanos(nanos)}")

  if (expectedAnswer != "") {
    check(result == expectedAnswer) { "Expected $expectedAnswer but got $result" }
    println("Output `$result` matched expected answer ${if (label.isNotEmpty()) "for `$label`" else ""}")
  } else {
    println("${if (label.isNotEmpty()) "`$label` " else ""} $result")
    copyToClipboard(result)
  }

  println()
}

fun formatNanos(n: Long) : String {
  return if (n < 900_000) {
    "$n ns"
  } else if (n < 900_000_000) {
    "${"%.3f".format(n/1_000_000.0)} ms"
  } else {
    "${"%.3f".format(n/1_000_000_000.0)} s"
  }
}

fun copyToClipboard(o: Any) {
  Toolkit.getDefaultToolkit()
    .getSystemClipboard()
    .setContents(
      StringSelection(o.toString()),
      null
    )
}

fun <T> MutableList<T>.removeLast(n: Int): List<T> {
  return (0 until n).map { this.removeLast() }.reversed().toList()
}

fun <T> Iterable<T>.toPair(): Pair<T, T> {
  return this.elementAt(0) to this.elementAt(1)
}

fun Iterable<Int>.toRange(): IntRange {
  return this.first()..this.last()
}

/** Split the list of strings with groupings separated by a blank string. */
fun blankDelimited(l: List<String>): List<List<String>> {
  return l.joinToString("\n").split("\n\n").map { it.split("\n") }
}

/** Splits the list using the predicate splitter. Elements matching the given predicate are dropped. */
fun <E> List<E>.splitBy(retainSplitter: Boolean = false, splitterPredicate: (E) -> Boolean): List<List<E>> {
  val returnLists = mutableListOf<List<E>>()
  var nextList = mutableListOf<E>()
  for (e in this) {
    if (splitterPredicate(e)) {

      returnLists.add(nextList)
      nextList = mutableListOf()

      if (retainSplitter) nextList.add(e)
      continue
    }
    nextList.add(e)
  }
  if (nextList.isNotEmpty()) {
    returnLists.add(nextList)
  }

  return returnLists
}

private operator fun <E> List<E>.component6(): E {
  return this[6]
}

fun <E, T> Iterable<Iterable<E>>.deepMap(mapper: (E) -> T): List<List<T>> {
  return this.map { it.map(mapper) }
}

/** Source: Mats56 on Reddit. https://www.reddit.com/r/adventofcode/comments/zcxid5/comment/iyz532y */
operator fun <E> Int.times(list: MutableList<E>): MutableList<MutableList<E>> {
  return MutableList(this) { list.toMutableList() }
}

fun String.allInts() = allIntsInString(this)
fun allIntsInString(line: String): List<Int> {
  return """-?\d+""".toRegex().findAll(line)
    .map { it.value.toInt() }
    .toList()
}

data class Coord(val x: Int, val y: Int) {
  constructor(p: Pair<Int, Int>) : this(p.first, p.second)

  operator fun plus(o: Coord): Coord = Coord(x + o.x, y + o.y)
  operator fun plus(o: Pair<Int, Int>): Coord = this + Coord(o)
  operator fun minus(o: Coord): Coord = Coord(x - o.x, y - o.y)
  operator fun minus(o: Pair<Int, Int>): Coord = this - Coord(o)

  operator fun times(i: Int): Coord = Coord(x * i, y * i)
  operator fun div(i: Int): Coord = Coord(x / i, y / i)

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

  companion object {
    val LEFT = Coord(-1, 0)
    val RIGHT = Coord(1, 0)
    val UP = Coord(0, 1)
    val DOWN = Coord(0, -1)
  }
}
