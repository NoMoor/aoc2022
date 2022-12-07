package utils

import formatNanos

class Stopwatch {

  private var startTime: Long? = null
  private var stopTime: Long? = null

  companion object {
    fun started(): Stopwatch {
      return Stopwatch().start()
    }
  }

  private fun start(): Stopwatch {
    this.startTime = System.nanoTime()
    return this
  }

  fun stop(): Stopwatch {
    this.stopTime = System.nanoTime()
    println(formatNanos(stopTime!! - startTime!!))
    return this
  }
}