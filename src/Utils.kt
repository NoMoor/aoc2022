import java.awt.datatransfer.StringSelection
import java.awt.Toolkit
import java.io.File
import java.math.BigInteger
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.MessageDigest

const val year = 2022


/**
 * Reads lines from the given input txt file.
 */
fun readInput(dayNum: Int, test: Boolean = false): List<String> {
    val paddedDay = dayNum.toString().padStart(2, '0')
    val fileName = if (test) "Day${paddedDay}_test.txt" else "Day${paddedDay}.txt"
    val file = File("src", "$fileName.txt")

    if (!file.exists()) {
        file.writeText(if (test) "" else tryGetInputFromSite(dayNum))
    }

    return file.readLines()
}

fun tryGetInputFromSite(day: Int): String {
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://adventofcode.com/$year/day/$day/input"))
        .setHeader("cookie", File("src", ".cookie").readText())
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    return response.body()
}

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

fun output(o: Any, prefix: String = "") {
    println("$prefix $o")

    Toolkit.getDefaultToolkit()
        .getSystemClipboard()
        .setContents(
            StringSelection(o.toString()),
            null
        )
}
