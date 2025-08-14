package com.example.krestiki

sealed class GameAction {
    data class MakeMove(val row: Int, val col: Int) : GameAction()
    object NewGame : GameAction()
    data class SelectSymbol(val symbol: PlayerSymbol) : GameAction()
    data class SwitchGridSize(val size: Int) : GameAction()
}
