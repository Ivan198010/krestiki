package com.example.krestiki

enum class PlayerSymbol { X, O }

enum class GameStatus { IN_PROGRESS, WIN_X, WIN_O, DRAW }

data class GameState(
    val board: List<List<PlayerSymbol?>> = List(3) { List(3) { null } },
    val currentPlayer: PlayerSymbol = PlayerSymbol.X,
    val gameStatus: GameStatus = GameStatus.IN_PROGRESS,
    val playerSymbol: PlayerSymbol? = null,
    val symbolSelectionDone: Boolean = false,
    val boardSize: Int = 3,
    val showResultDialog: Boolean = false
)
