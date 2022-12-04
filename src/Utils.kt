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

const val year = 2022
const val defaultText = "100"

/**
 * Reads lines from the given input txt file.
 */
fun readInput(dayNum: Int, test: Boolean = false): List<String> {
    val paddedDay = dayNum.toString().padStart(2, '0')
    val fileName = if (test) "Day${paddedDay}_test.txt" else "Day${paddedDay}.txt"
    val file = File("src", fileName)

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
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

fun execute(c: Callable<Any>, label: String = "") {
    println("************* Start $label *************")
    val out = c.call()
    output(out, "Output: ")
    println("************** End $label **************")
    println()
}

fun output(o: Any, prefix: String = "") {
    println("$prefix $o")

    Toolkit.getDefaultToolkit()
        .getSystemClipboard()
        .setContents(
            StringSelection(o.toString()),
            null
        )
}

fun <T> Iterable<T>.toPair(): Pair<T, T> {
    return Pair(this.elementAt(0), this.elementAt(1))
}

fun Iterable<Int>.toRange(): IntRange {
    return this.elementAt(0) .. this.elementAt(1)
}

/** Split the list of strings with groupings separated by a blank string. */
fun blankDelimited(l: List<String>) : List<List<String>> {
    return l.joinToString("\n").split("\n\n").map { it.split("\n") }
}