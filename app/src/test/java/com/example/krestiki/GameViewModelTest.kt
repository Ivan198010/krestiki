package com.example.krestiki

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameViewModelTest {

    @Test
    fun `checkWin returns true for horizontal win on 3x3`() {
        val board = listOf(
            listOf('X', 'X', 'X'),
            listOf(null, 'O', null),
            listOf('O', null, null)
        )
        assertTrue(checkWin(board, 3))
    }

    @Test
    fun `checkWin returns true for vertical win on 3x3`() {
        val board = listOf(
            listOf('X', 'O', null),
            listOf('X', 'O', null),
            listOf('X', null, null)
        )
        assertTrue(checkWin(board, 3))
    }

    @Test
    fun `checkWin returns true for diagonal win on 3x3`() {
        val board = listOf(
            listOf('X', 'O', null),
            listOf('O', 'X', null),
            listOf(null, null, 'X')
        )
        assertTrue(checkWin(board, 3))
    }

    @Test
    fun `checkWin returns false for no win on 3x3`() {
        val board = listOf(
            listOf('X', 'O', 'X'),
            listOf('O', 'O', 'X'),
            listOf('X', 'X', 'O')
        )
        assertFalse(checkWin(board, 3))
    }

    @Test
    fun `checkWin returns true for horizontal win on 5x5`() {
        val board = List(5) { MutableList<Char?>(5) { null } }
        for (i in 0..4) {
            board[0][i] = 'O'
        }
        assertTrue(checkWin(board, 5))
    }

    @Test
    fun `checkWin returns true for vertical win on 5x5`() {
        val board = List(5) { MutableList<Char?>(5) { null } }
        for (i in 0..4) {
            board[i][0] = 'X'
        }
        assertTrue(checkWin(board, 5))
    }

    @Test
    fun `checkWin returns true for diagonal win on 5x5`() {
        val board = List(5) { MutableList<Char?>(5) { null } }
        for (i in 0..4) {
            board[i][i] = 'X'
        }
        assertTrue(checkWin(board, 5))
    }
}
