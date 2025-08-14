package com.example.krestiki

data class GameState(
    val boardSize: Int = 3,
    val board: List<List<Char?>> = List(boardSize) { List(boardSize) { null } }, // Используем boardSize для инициализации
    val currentPlayer: Char = 'X', // Это может стать символом ИИ, если игрок выбирает O
    val gameStatus: GameStatus = GameStatus.IN_PROGRESS,
    val playerSymbol: PlayerSymbol? = null, // Символ, выбранный игроком
    val symbolSelectionDone: Boolean = false // Флаг, что выбор сделан
)

enum class GameStatus {
    IN_PROGRESS,
    WIN_X,
    WIN_O,
    DRAW
}

// Новый enum для выбора символа игроком
enum class PlayerSymbol {
    X, O
}
