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
                title = { Text("") }, // ✅ ИСПРАВЛЕНИЕ: Передаем пустой текст в качестве заголовка
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GridSizeToggleButton(state.boardSize, onAction)
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { onAction(GameAction.NewGame) }) {
                            Text("Новая игра")
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
            GameBoard(state = state, onAction = onAction)
            if (state.gameStatus != GameStatus.IN_PROGRESS) {
                GameResultDialog(status = state.gameStatus, onAction = onAction)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Добавлена аннотация для SingleChoiceSegmentedButtonRow
@Composable
fun GridSizeToggleButton(currentSize: Int, onAction: (GameAction) -> Unit) {
    val sizes = listOf(3, 5)
    SingleChoiceSegmentedButtonRow { // Этот компонент также является экспериментальным
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