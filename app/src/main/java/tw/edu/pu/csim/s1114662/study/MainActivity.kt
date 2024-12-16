import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnakeGame()
        }
    }
}

enum class Direction { UP, DOWN, LEFT, RIGHT }
data class SnakePart(val x: Int, val y: Int)

@Composable
fun SnakeGame() {
    val gridSize = 20
    val boxSize = 20.dp

    var snake by remember {
        mutableStateOf(
            listOf(
                SnakePart(10, 10),
                SnakePart(10, 11),
                SnakePart(10, 12),
                SnakePart(10, 13)
            )
        )
    }
    var direction by remember { mutableStateOf(Direction.RIGHT) }
    var reward by remember {
        mutableStateOf(
            SnakePart(Random.nextInt(0, gridSize), Random.nextInt(0, gridSize))
        )
    }
    var justAteReward by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(200L)
            snake = moveSnake(snake, direction, justAteReward)
            justAteReward = false

            val head = snake.first()
            val wrappedHead = wrapPosition(head, gridSize)
            if (wrappedHead == reward) {
                justAteReward = true
                reward = generateNewReward(snake, gridSize)
            }

            if (snake.drop(1).contains(wrappedHead)) {
                snake = snake.dropWhile { it != wrappedHead }.drop(1)
            }

            snake = snake.map { wrapPosition(it, gridSize) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // 遊戲畫面
        Canvas(
            modifier = Modifier
                .size((gridSize * boxSize.value).dp) // 修正類型錯誤
                .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
        ) {
            snake.forEach { part ->
                drawRoundRect(
                    Color.Green,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        part.x * boxSize.toPx(), part.y * boxSize.toPx()
                    ),
                    size = androidx.compose.ui.geometry.Size(boxSize.toPx(), boxSize.toPx()),
                    cornerRadius = CornerRadius(10f, 10f)
                )
            }

            drawRoundRect(
                Color.Red,
                topLeft = androidx.compose.ui.geometry.Offset(
                    reward.x * boxSize.toPx(), reward.y * boxSize.toPx()
                ),
                size = androidx.compose.ui.geometry.Size(boxSize.toPx(), boxSize.toPx()),
                cornerRadius = CornerRadius(10f, 10f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 方向控制區域
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 上方按鈕
            Button(
                onClick = { if (direction != Direction.DOWN) direction = Direction.UP },
                modifier = Modifier.size(70.dp)
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Up")
            }

            // 左、中、右按鈕
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            ) {
                Button(
                    onClick = { if (direction != Direction.RIGHT) direction = Direction.LEFT },
                    modifier = Modifier.size(70.dp)
                ) {
                    Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "Left")
                }

                Spacer(modifier = Modifier.size(70.dp)) // 中間的空間

                Button(
                    onClick = { if (direction != Direction.RIGHT) direction = Direction.RIGHT },
                    modifier = Modifier.size(70.dp)
                ) {
                    Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Right")
                }
            }

            // 下方按鈕
            Button(
                onClick = { if (direction != Direction.UP) direction = Direction.DOWN },
                modifier = Modifier
                    .size(70.dp)
                    .padding(top = 10.dp)
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Down")
            }
        }
    }
}

fun moveSnake(snake: List<SnakePart>, direction: Direction, grow: Boolean): List<SnakePart> {
    val head = snake.first()
    val newHead = when (direction) {
        Direction.UP -> SnakePart(head.x, head.y - 1)
        Direction.DOWN -> SnakePart(head.x, head.y + 1)
        Direction.LEFT -> SnakePart(head.x - 1, head.y)
        Direction.RIGHT -> SnakePart(head.x + 1, head.y)
    }
    return if (grow) listOf(newHead) + snake else listOf(newHead) + snake.dropLast(1)
}

fun wrapPosition(part: SnakePart, gridSize: Int): SnakePart {
    return SnakePart((part.x + gridSize) % gridSize, (part.y + gridSize) % gridSize)
}

fun generateNewReward(snake: List<SnakePart>, gridSize: Int): SnakePart {
    var newReward: SnakePart
    do {
        newReward = SnakePart(Random.nextInt(0, gridSize), Random.nextInt(0, gridSize))
    } while (snake.contains(newReward))
    return newReward
}