package com.example.krestiki.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.krestiki.GameAction
import com.example.krestiki.GameState

@Composable
fun GameBoard(
    state: GameState,
    onAction: (GameAction) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .pointerInput(state.boardSize) {
                detectTapGestures { offset ->
                    val row = (offset.y / (size.height / state.boardSize)).toInt()
                    val col = (offset.x / (size.width / state.boardSize)).toInt()
                    onAction(GameAction.MakeMove(row, col))
                }
            }
    ) {
        drawGrid(state.boardSize)
        drawSymbols(state.board, textMeasurer)
    }
}

private fun DrawScope.drawGrid(boardSize: Int) {
    val cellSize = size.width / boardSize
    for (i in 1 until boardSize) {
        // Vertical lines
        drawLine(
            color = Color.Gray,
            start = Offset(x = cellSize * i, y = 0f),
            end = Offset(x = cellSize * i, y = size.height),
            strokeWidth = 2f
        )
        // Horizontal lines
        drawLine(
            color = Color.Gray,
            start = Offset(x = 0f, y = cellSize * i),
            end = Offset(x = size.width, y = cellSize * i),
            strokeWidth = 2f
        )
    }
}

private fun DrawScope.drawSymbols(
    board: List<List<Char?>>,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    val cellSize = size.width / board.size
    board.forEachIndexed { i, row ->
        row.forEachIndexed { j, symbol ->
            if (symbol != null) {
                val textLayoutResult = textMeasurer.measure(
                    text = symbol.toString(),
                    style = TextStyle(fontSize = 64.sp)
                )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        x = j * cellSize + (cellSize - textLayoutResult.size.width) / 2,
                        y = i * cellSize + (cellSize - textLayoutResult.size.height) / 2
                    )
                )
            }
        }
    }
}
