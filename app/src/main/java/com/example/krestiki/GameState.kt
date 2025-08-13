package com.example.krestiki

data class GameState(
    val boardSize: Int = 3,
    val board: List<List<Char?>> = List(3) { List(3) { null } },
    val currentPlayer: Char = 'X',
    val gameStatus: GameStatus = GameStatus.IN_PROGRESS
)

enum class GameStatus {
    IN_PROGRESS,
    WIN_X,
    WIN_O,
    DRAW
}
