package com.example.krestiki

fun checkWin(board: List<List<Char?>>, size: Int): Boolean {
    // Check rows
    for (i in 0 until size) {
        if (board[i].all { it == 'X' } || board[i].all { it == 'O' }) {
            return true
        }
    }

    // Check columns
    for (j in 0 until size) {
        if ((0 until size).all { i -> board[i][j] == 'X' } || (0 until size).all { i -> board[i][j] == 'O' }) {
            return true
        }
    }

    // Check diagonals
    if ((0 until size).all { i -> board[i][i] == 'X' } || (0 until size).all { i -> board[i][i] == 'O' }) {
        return true
    }
    if ((0 until size).all { i -> board[i][size - 1 - i] == 'X' } || (0 until size).all { i -> board[i][size - 1 - i] == 'O' }) {
        return true
    }

    return false
}
