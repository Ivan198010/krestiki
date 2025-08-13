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
            is GameAction.CellClick -> onCellClick(action.row, action.col)
            GameAction.NewGame -> resetGame()
            is GameAction.SwitchGridSize -> setGridSize(action.size)
        }
    }

    private fun onCellClick(row: Int, col: Int) {
        if (_gameState.value.board[row][col] == null && _gameState.value.gameStatus == GameStatus.IN_PROGRESS) {
            val newBoard = _gameState.value.board.map { it.toMutableList() }.toMutableList()
            newBoard[row][col] = 'X'
            updateGameState(newBoard)

            if (_gameState.value.gameStatus == GameStatus.IN_PROGRESS) {
                viewModelScope.launch {
                    makeComputerMove()
                }
            }
        }
    }

    private fun makeComputerMove() {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until _gameState.value.boardSize) {
            for (j in 0 until _gameState.value.boardSize) {
                if (_gameState.value.board[i][j] == null) {
                    emptyCells.add(Pair(i, j))
                }
            }
        }

        if (emptyCells.isNotEmpty()) {
            val (row, col) = emptyCells.random()
            val newBoard = _gameState.value.board.map { it.toMutableList() }.toMutableList()
            newBoard[row][col] = 'O'
            updateGameState(newBoard)
        }
    }

    private fun resetGame() {
        _gameState.value = GameState(boardSize = _gameState.value.boardSize)
    }

    private fun setGridSize(size: Int) {
        _gameState.value = GameState(boardSize = size, board = List(size) { List(size) { null } })
    }

    private fun updateGameState(newBoard: List<List<Char?>>) {
        val newStatus = if (checkWin(newBoard, _gameState.value.boardSize)) {
            if (_gameState.value.currentPlayer == 'X') GameStatus.WIN_X else GameStatus.WIN_O
        } else if (newBoard.all { row -> row.all { it != null } }) {
            GameStatus.DRAW
        } else {
            GameStatus.IN_PROGRESS
        }

        _gameState.value = _gameState.value.copy(
            board = newBoard,
            currentPlayer = if (_gameState.value.currentPlayer == 'X') 'O' else 'X',
            gameStatus = newStatus
        )
    }
}

sealed class GameAction {
    data class CellClick(val row: Int, val col: Int) : GameAction()
    object NewGame : GameAction()
    data class SwitchGridSize(val size: Int) : GameAction()
}
