package com.example.krestiki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.krestiki.composables.GameBoard
import com.example.krestiki.ui.theme.KrestikiTheme

// Предполагается, что эти определения существуют в ваших файлах GameState.kt и GameAction.kt
// enum class PlayerSymbol { X, O }
// data class GameState(
//     // ... другие состояния
//     val playerSymbol: PlayerSymbol? = null,
//     val symbolSelectionDone: Boolean = false,
//     val boardSize: Int = 3, // значение по умолчанию
//     val gameStatus: GameStatus = GameStatus.IN_PROGRESS // значение по умолчанию
// )
// sealed class GameAction {
//     object NewGame : GameAction()
//     data class SwitchGridSize(val size: Int) : GameAction()
//     data class SelectSymbol(val symbol: PlayerSymbol) : GameAction()
//     // ... другие действия
// }
// enum class GameStatus { IN_PROGRESS, WIN_X, WIN_O, DRAW }


class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KrestikiTheme {
                val state by viewModel.gameState.collectAsState()
                GameScreen(state = state, onAction = viewModel::onAction)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(state: GameState, onAction: (GameAction) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") }, 
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (state.symbolSelectionDone) { // Показываем только если символ выбран
                            GridSizeToggleButton(state.boardSize, onAction)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Button(onClick = { onAction(GameAction.NewGame) }) {
                            Text(if (state.symbolSelectionDone) "Новая игра" else "Сбросить выбор")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (!state.symbolSelectionDone) {
                SymbolSelectionScreen(onAction = onAction)
            } else {
                GameBoard(state = state, onAction = onAction)
                if (state.gameStatus != GameStatus.IN_PROGRESS) {
                    GameResultDialog(status = state.gameStatus, onAction = onAction)
                }
            }
        }
    }
}

@Composable
fun SymbolSelectionScreen(onAction: (GameAction) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Выберите, кем играть:", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = { onAction(GameAction.SelectSymbol(PlayerSymbol.X)) }) {
                Text("Играть за X")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { onAction(GameAction.SelectSymbol(PlayerSymbol.O)) }) {
                Text("Играть за O")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class) 
@Composable
fun GridSizeToggleButton(currentSize: Int, onAction: (GameAction) -> Unit) {
    val sizes = listOf(3, 5)
    SingleChoiceSegmentedButtonRow { 
        sizes.forEachIndexed { _, size ->
            SegmentedButton(
                selected = currentSize == size,
                onClick = { onAction(GameAction.SwitchGridSize(size)) },
                shape = RectangleShape
            ) {
                Text(text = "$size×$size")
            }
        }
    }
}

@Composable
fun GameResultDialog(status: GameStatus, onAction: (GameAction) -> Unit) {
    val message = when (status) {
        GameStatus.WIN_X -> "Победа X"
        GameStatus.WIN_O -> "Победа O"
        GameStatus.DRAW -> "Ничья"
        else -> ""
    }
    AlertDialog(
        onDismissRequest = { onAction(GameAction.NewGame) },
        title = { Text("Игра окончена") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = { onAction(GameAction.NewGame) }) {
                Text("Сыграть снова")
            }
        }
    )
}