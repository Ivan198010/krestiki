package com.example.krestiki

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState

    fun onAction(action: GameAction) {
        when (action) {
            is GameAction.MakeMove -> onCellClick(action.row, action.col) // Изменено с CellClick на MakeMove
            is GameAction.NewGame -> resetGame()
            is GameAction.SwitchGridSize -> setGridSize(action.size)
            is GameAction.SelectSymbol -> selectSymbol(action.symbol)
        }
    }

    private fun onCellClick(row: Int, col: Int) {
        val currentState = _gameState.value
        if (currentState.board[row][col] == null && currentState.gameStatus == GameStatus.IN_PROGRESS && currentState.symbolSelectionDone) {
            val newBoard = currentState.board.map { it.toMutableList() }.toMutableList()
            // Используем playerSymbol для хода игрока, если он выбран
            val playerActualSymbol = currentState.playerSymbol?.name?.first() ?: currentState.currentPlayer

            newBoard[row][col] = playerActualSymbol
            
            // Определяем символ компьютера на основе выбора игрока
            val computerSymbol = if (playerActualSymbol == 'X') 'O' else 'X'

            updateGameState(newBoard, playerActualSymbol) // Передаем текущего игрока

            // Ход компьютера, если игра продолжается и игрок сделал ход
            if (_gameState.value.gameStatus == GameStatus.IN_PROGRESS) {
                viewModelScope.launch {
                    makeComputerMove(computerSymbol)
                }
            }
        }
    }

    private fun makeComputerMove(computerSymbol: Char) {
        val currentState = _gameState.value
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until currentState.boardSize) {
            for (j in 0 until currentState.boardSize) {
                if (currentState.board[i][j] == null) {
                    emptyCells.add(Pair(i, j))
                }
            }
        }

        if (emptyCells.isNotEmpty()) {
            val (row, col) = emptyCells.random()
            val newBoard = currentState.board.map { it.toMutableList() }.toMutableList()
            newBoard[row][col] = computerSymbol
            updateGameState(newBoard, computerSymbol) // Передаем символ компьютера
        }
    }

    private fun resetGame() {
        // Сбрасываем также выбор символа и флаг выбора
        _gameState.value = GameState(
            boardSize = _gameState.value.boardSize,
            playerSymbol = null,
            symbolSelectionDone = false
        )
    }

    private fun setGridSize(size: Int) {
        // При смене размера сетки также сбрасываем выбор символа
        _gameState.value = GameState(
            boardSize = size,
            board = List(size) { List(size) { null } },
            playerSymbol = null,
            symbolSelectionDone = false
        )
    }

    private fun selectSymbol(symbol: PlayerSymbol) {
        _gameState.value = _gameState.value.copy(
            playerSymbol = symbol,
            symbolSelectionDone = true,
            // Устанавливаем currentPlayer на основе выбора игрока,
            // или оставляем 'X', если символ игрока 'X', чтобы он ходил первым.
            // Если игрок выбрал 'O', то первый ход делает компьютер (currentPlayer останется 'X' 
            // и сразу после выбора символа может сработать makeComputerMove, если это желательно)
            currentPlayer = if (symbol == PlayerSymbol.X) 'X' else 'O'
        )
         // Если игрок выбрал 'O', и компьютер должен ходить первым
        if (symbol == PlayerSymbol.O && _gameState.value.gameStatus == GameStatus.IN_PROGRESS) {
            viewModelScope.launch {
                makeComputerMove('X') // Компьютер ходит крестиком
            }
        }
    }

    private fun updateGameState(newBoard: List<List<Char?>>, lastPlayerSymbol: Char) {
        val newStatus = checkGameStatus(newBoard, _gameState.value.boardSize, lastPlayerSymbol)

        _gameState.value = _gameState.value.copy(
            board = newBoard.map { it.toList() }, // Убедимся, что это неизменяемый список
            // currentPlayer не меняется здесь, так как следующий игрок определяется логикой хода
            gameStatus = newStatus
        )
    }

    private fun checkGameStatus(board: List<List<Char?>>, boardSize: Int, lastPlayerSymbol: Char): GameStatus {
        // Проверка победителя
        for (i in 0 until boardSize) {
            // Проверка строк
            if (board[i].all { it == lastPlayerSymbol }) return if (lastPlayerSymbol == 'X') GameStatus.WIN_X else GameStatus.WIN_O
            // Проверка столбцов
            if (board.all { it[i] == lastPlayerSymbol }) return if (lastPlayerSymbol == 'X') GameStatus.WIN_X else GameStatus.WIN_O
        }
        // Проверка диагоналей
        if ((0 until boardSize).all { board[it][it] == lastPlayerSymbol }) return if (lastPlayerSymbol == 'X') GameStatus.WIN_X else GameStatus.WIN_O
        if ((0 until boardSize).all { board[it][boardSize - 1 - it] == lastPlayerSymbol }) return if (lastPlayerSymbol == 'X') GameStatus.WIN_X else GameStatus.WIN_O

        // Проверка на ничью
        if (board.all { row -> row.all { it != null } }) return GameStatus.DRAW

        return GameStatus.IN_PROGRESS
    }
}
