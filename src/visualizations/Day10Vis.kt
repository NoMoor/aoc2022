@file:OptIn(ExperimentalComposeUiApi::class)

package visualizations

// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

import kotlinx.coroutines.*
import readInput

private const val pixelWidth = 30
private const val pixelHeight = 45
private const val pixelRows = 6
private const val pixelColumns = 40
private const val windowFrameHeight = 36
private const val buttonHeight = 45
private const val scanDelay = 100L

private var coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

@Composable
@Preview
fun Day10Vis() {
  Crt()
}

@Composable
fun Crt() {
  MaterialTheme {
    val crtState = remember { CrtState() }

    Column( horizontalAlignment = Alignment.CenterHorizontally) {
      Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        Button(crtState::start) { Text("Start") }
        Button(crtState::stop) { Text("Stop") }
      }
      repeat(pixelRows) { r -> Row { repeat(pixelColumns) { c -> Pixel(crtState.pixelStates[r][c]) } } }
    }
  }
}

@Composable
fun Pixel(pixelState: PixelState) {
  Canvas(
    modifier = Modifier
      .size(pixelWidth.dp, pixelHeight.dp)
      .background(color = pixelState.getColor())
      .border(color = Color.Gray, width = 2.dp)
  ) {}
}

data class PixelState(val isOn: Boolean, val isScan: Boolean, val isSprite: Boolean) {

  fun getColor() : Color {
    if (isScan) {
      return Color.Red
    } else if (isOn) {
      return Color.DarkGray
    } else if (isSprite) {
      return Color.Green
    }
    return Color.LightGray
  }

  companion object {
    val OFF = PixelState(isOn = false, isScan = false, isSprite = false)
  }
}

class CrtState {
  val tokens = readInput(10, 2022).joinToString(" ").split(" ")

  val pixelStates = MutableList(pixelRows) { MutableList(pixelColumns) { PixelState.OFF }.toMutableStateList() }
  var cycle = 1
  var running = false
  var x = 1

  fun start() {
    if (running) return

    cycle = 1
    x = 1

    fun spriteInRange() : Boolean {
      val spriteRange = x - 1..x + 1
      val drawCol = (cycle - 1) % 40

      return drawCol in spriteRange
    }

    fun updateSpriteLoc(newLoc: Int) {
      // Clear old
      repeat(pixelRows) { r ->
        (-1..1).forEach { offset ->
          val c = x-1 + offset
          if (c in 0 until pixelColumns) {
            pixelStates[r][c] = pixelStates[r][c].copy(isSprite = false)
          }
        }
      }
      x = newLoc
      // Set new
      repeat(pixelRows) { r ->
        (-1..1).forEach { offset ->
          val c = x-1 + offset
          if (c in 0 until pixelColumns) {
            pixelStates[r][c] = pixelStates[r][c].copy(isSprite = true)
          }
        }
      }
    }

    coroutineScope.launch {
      running = true
      while (running) {
        val row = (cycle - 1) / pixelColumns
        val column = (cycle - 1) % pixelColumns
        pixelStates[row][column] = pixelStates[row][column].copy(isScan = true)
        val parts = tokens[cycle - 1]

        delay(scanDelay)
        pixelStates[row][column] = pixelStates[row][column].copy(isOn = spriteInRange(), isScan = false)

        if (parts[0] == '-' || parts[0].isDigit()) {
          updateSpriteLoc(x + parts.toInt())
          // restore pixels
        }
        if (cycle == (pixelColumns * pixelRows)) {
          cycle = 1
          updateSpriteLoc(1) // TODO: Only do this if we don't have more input
        } else {
          cycle++
        }
      }
      pixelStates.forEach { it.replaceAll { PixelState.OFF } }
      running = false
    }
  }

  fun stop() {
    running = false
  }
}

fun main() = application {
  Window(
    title = "Day 10 Visualization",
    state = WindowState(
      width = (pixelWidth * pixelColumns).dp,
      height = (pixelHeight * pixelRows + windowFrameHeight + buttonHeight).dp
    ),
    onCloseRequest = ::exitApplication
  ) {
    Day10Vis()
  }
}
