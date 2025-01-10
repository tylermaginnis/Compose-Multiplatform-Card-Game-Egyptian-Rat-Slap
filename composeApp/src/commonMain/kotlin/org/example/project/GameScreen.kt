package org.example.project

import androidx.compose.ui.ExperimentalComposeUiApi
import org.jetbrains.compose.resources.painterResource
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.background
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import org.example.project.gameLogic.*

import kotlinproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.alpha
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import org.example.project.ILogger
private val logger: ILogger = createLogger()

@Composable
fun GameScreen(
    gameState: GameState = GameLogic.initializeGame(),
    onBackToLogin: () -> Unit,
    onPlayCard: (GameState) -> Unit = {},
    onSlap: (GameState) -> Unit = {}
) {
    var currentGameState by remember { mutableStateOf(gameState) }
    var currentPlayer by remember { mutableStateOf(gameState.players[gameState.currentPlayerIndex]) }
    var message by remember { mutableStateOf("") }
    var buttonsEnabled by remember { mutableStateOf(true) }
    var playCardClicked by remember { mutableStateOf(false) }
    var slapClicked by remember { mutableStateOf(false) }
    var isTopCardHighlighted by remember { mutableStateOf(false) }
    LaunchedEffect(currentGameState) {
        logger.debug("GameScreen", "LaunchedEffect triggered with currentGameState: $currentGameState")
        if (currentGameState.currentPlayerIndex != 0) {
            logger.debug("GameScreen", "Handling AI turn for player index: ${currentGameState.currentPlayerIndex}")
            handleAITurn(currentGameState) { newState ->
                logger.debug("GameScreen", "AI turn completed. New state: $newState")
                currentGameState = newState
                currentPlayer = newState.players[newState.currentPlayerIndex]
                onPlayCard(newState)
            }
        }
    }

    LaunchedEffect(playCardClicked) {
        if (playCardClicked) {
            playCardClicked = false
            if (currentGameState.currentPlayerIndex == 0) {
                val newGameState = GameLogic.playCard(currentGameState)
                onPlayCard(newGameState)
                currentGameState = newGameState
                currentPlayer = newGameState.players[newGameState.currentPlayerIndex]
                message = ""

                // Check if the current player has no cards left
                if (currentPlayer.hand.isEmpty() && !GameLogic.canSlap(currentGameState)) {
                    // If the player has no cards and can't slap, end the game
                    val endGameState = GameLogic.checkGameEnd(currentGameState)
                    currentGameState = endGameState
                    message = "Game over! ${endGameState.winner ?: "No winner"} wins!"
                    buttonsEnabled = false
                }
            } else {
                message = "It's not your turn!"
            }
        }
    }

    LaunchedEffect(slapClicked) {
        if (slapClicked) {
            slapClicked = false
            val (newGameState, newMessage, slapType) = GameLogic.slap(currentGameState, currentGameState.currentPlayerIndex)
            onSlap(newGameState)
            currentGameState = newGameState
            currentPlayer = newGameState.players[newGameState.currentPlayerIndex]
            message = newMessage
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        logger.debug("GameScreen", "Rendering GameScreen UI")
        Text("Welcome to Egyptian Rat Slap!")
        // Display hand sizes
        logger.debug("GameScreen", "Displaying hand sizes")
        Text("Hand Sizes:")
        currentGameState.players.forEach { player ->
            Text("${player.name}: ${player.hand.size} cards") // Use currentGameState
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Current Player: ${currentPlayer}")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Pile: ${currentGameState.pile.size} cards")

        Spacer(modifier = Modifier.height(16.dp))


        // Use a single Box for all cards, centered on the screen
        Box(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize(Alignment.Center)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type == PointerEventType.Press && buttonsEnabled) {
                                logger.debug("GameScreen", "Card pile tapped for slap")
                                slapClicked = true
                            }
                        }
                    }
                }
        ) {
            if (currentGameState.pile.isNotEmpty()) {
                currentGameState.pile.forEachIndexed { index, card ->
                    val cardImageResource = getCardImageResource(card)
                    val isBottomCard = index == 0
                    val isTopCard = index == currentGameState.pile.lastIndex

                    AnimatedCard(
                        cardImageResource = cardImageResource,
                        isPlayer1 = currentPlayer == currentGameState.players[0],
                        rotation = calculateCardRotation(index),
                        index = index,
                        gameState = currentGameState,
                        isBottomCard = isBottomCard,
                        isTopCard = isTopCard,
                        isHighlighted = isTopCard && isTopCardHighlighted,
                        onSlap = { slapClicked = true },
                        buttonsEnabled = buttonsEnabled
                    )
                }
            } else {
                logger.debug("GameScreen", "Pile is empty")
                Text("Pile is empty")
            }
        }

        // Display scores
        logger.debug("GameScreen", "Displaying scores: ${currentGameState.scores}")
        currentGameState.scores.forEach { (playerName, score) ->
            Text("$playerName: $score")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display message
        if (message.isNotEmpty()) {
            logger.debug("GameScreen", "Displaying message: $message")
            Text(message, color = if (message.startsWith("Valid")) Color.Green else Color.Red)
        } else {
            Text("")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Modify the Play Card button
        val playCardAlpha by animateFloatAsState(
            targetValue = if (buttonsEnabled) 1f else 0.5f,
            animationSpec = tween(durationMillis = 300)
        )
        logger.debug("GameScreen", "Rendering Play Card button with alpha: $playCardAlpha")
        Button(
            onClick = {
                if (buttonsEnabled) {
                    logger.debug("GameScreen", "Play Card button clicked")
                    playCardClicked = true
                }
            },
            enabled = buttonsEnabled,
            modifier = Modifier.alpha(playCardAlpha)
        ) {
            Text("Play Card")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Removed the Slap button

        Spacer(modifier = Modifier.height(8.dp))

        logger.debug("GameScreen", "Rendering Back to Login button")
        Button(onClick = onBackToLogin) {
            Text("Back to Login")
        }

        if (currentGameState.winner != null) {
            Spacer(modifier = Modifier.height(16.dp))
            logger.debug("GameScreen", "Displaying winner: ${currentGameState.winner}")
            Text("Winner: ${currentGameState.winner}")
        }
    }
}

@Composable
fun getCardImageResource(card: Card): DrawableResource {
    logger.debug("GameScreen", "Getting image resource for card: $card")
    return when (card.suit) {
        Card.Suit.SPADES -> when (card.rank) {
            Card.Rank.TWO -> Res.drawable.two_of_spades
            Card.Rank.THREE -> Res.drawable.three_of_spades
            Card.Rank.FOUR -> Res.drawable.four_of_spades
            Card.Rank.FIVE -> Res.drawable.five_of_spades
            Card.Rank.SIX -> Res.drawable.six_of_spades
            Card.Rank.SEVEN -> Res.drawable.seven_of_spades
            Card.Rank.EIGHT -> Res.drawable.eight_of_spades
            Card.Rank.NINE -> Res.drawable.nine_of_spades
            Card.Rank.TEN -> Res.drawable.ten_of_spades
            Card.Rank.JACK -> Res.drawable.jack_of_spades
            Card.Rank.QUEEN -> Res.drawable.queen_of_spades
            Card.Rank.KING -> Res.drawable.king_of_spades
            Card.Rank.ACE -> Res.drawable.ace_of_spades
        }
        Card.Suit.HEARTS -> when (card.rank) {
            Card.Rank.TWO -> Res.drawable.two_of_hearts
            Card.Rank.THREE -> Res.drawable.three_of_hearts
            Card.Rank.FOUR -> Res.drawable.four_of_hearts
            Card.Rank.FIVE -> Res.drawable.five_of_hearts
            Card.Rank.SIX -> Res.drawable.six_of_hearts
            Card.Rank.SEVEN -> Res.drawable.seven_of_hearts
            Card.Rank.EIGHT -> Res.drawable.eight_of_hearts
            Card.Rank.NINE -> Res.drawable.nine_of_hearts
            Card.Rank.TEN -> Res.drawable.ten_of_hearts
            Card.Rank.JACK -> Res.drawable.jack_of_hearts
            Card.Rank.QUEEN -> Res.drawable.queen_of_hearts
            Card.Rank.KING -> Res.drawable.king_of_hearts
            Card.Rank.ACE -> Res.drawable.ace_of_hearts
        }
        Card.Suit.DIAMONDS -> when (card.rank) {
            Card.Rank.TWO -> Res.drawable.two_of_diamonds
            Card.Rank.THREE -> Res.drawable.three_of_diamonds
            Card.Rank.FOUR -> Res.drawable.four_of_diamonds
            Card.Rank.FIVE -> Res.drawable.five_of_diamonds
            Card.Rank.SIX -> Res.drawable.six_of_diamonds
            Card.Rank.SEVEN -> Res.drawable.seven_of_diamonds
            Card.Rank.EIGHT -> Res.drawable.eight_of_diamonds
            Card.Rank.NINE -> Res.drawable.nine_of_diamonds
            Card.Rank.TEN -> Res.drawable.ten_of_diamonds
            Card.Rank.JACK -> Res.drawable.jack_of_diamonds
            Card.Rank.QUEEN -> Res.drawable.queen_of_diamonds
            Card.Rank.KING -> Res.drawable.king_of_diamonds
            Card.Rank.ACE -> Res.drawable.ace_of_diamonds
        }
        Card.Suit.CLUBS -> when (card.rank) {
            Card.Rank.TWO -> Res.drawable.two_of_clubs
            Card.Rank.THREE -> Res.drawable.three_of_clubs
            Card.Rank.FOUR -> Res.drawable.four_of_clubs
            Card.Rank.FIVE -> Res.drawable.five_of_clubs
            Card.Rank.SIX -> Res.drawable.six_of_clubs
            Card.Rank.SEVEN -> Res.drawable.seven_of_clubs
            Card.Rank.EIGHT -> Res.drawable.eight_of_clubs
            Card.Rank.NINE -> Res.drawable.nine_of_clubs
            Card.Rank.TEN -> Res.drawable.ten_of_clubs
            Card.Rank.JACK -> Res.drawable.jack_of_clubs
            Card.Rank.QUEEN -> Res.drawable.queen_of_clubs
            Card.Rank.KING -> Res.drawable.king_of_clubs
            Card.Rank.ACE -> Res.drawable.ace_of_clubs
        }
        else -> {
            logger.error("GameScreen", "Unknown suit: ${card.suit}")
            throw IllegalArgumentException("Unknown suit: ${card.suit}")
        }
    }
}

@Composable
fun AnimatedCard(
    cardImageResource: DrawableResource,
    isPlayer1: Boolean,
    rotation: Float,
    index: Int,
    gameState: GameState,
    isBottomCard: Boolean = false,
    isTopCard: Boolean = false,
    isHighlighted: Boolean = false,
    onSlap: () -> Unit,
    buttonsEnabled: Boolean
) {
    logger.debug("GameScreen", "Rendering AnimatedCard for index $index with rotation $rotation, isBottomCard: $isBottomCard, isTopCard: $isTopCard, isHighlighted: $isHighlighted")
    val cardHeight = 200.dp
    val cardWidth = 140.dp

    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        modifier = Modifier
            .size(width = cardWidth, height = cardHeight)
            .graphicsLayer {
                rotationZ = animatedRotation
                transformOrigin = TransformOrigin(0.5f, 0.5f)
            }
            .then(
                if (isHighlighted) {
                    Modifier.border(
                        width = 4.dp,
                        color = Color.Yellow,
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier
                }
            )
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Press && buttonsEnabled) {
                            logger.debug("GameScreen", "Card pile tapped for slap")
                            onSlap()
                        }
                    }
                }
            }
    ) {
        // Add the white background
        logger.debug("GameScreen", "Adding white background to AnimatedCard")
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.White)
        )

        // Add the card image with padding
        logger.debug("GameScreen", "Adding card image to AnimatedCard")
        Box(
            modifier = Modifier
                .size(width = cardWidth - 8.dp, height = cardHeight - 8.dp)  // Reduce size to create space
                .align(Alignment.Center)
        ) {
            Image(
                modifier = Modifier
                    .size(width = cardWidth - 8.dp, height = cardHeight - 8.dp),
                painter = painterResource(cardImageResource),
                contentDescription = "Animated card"
            )
        }

        // Add a consistent border for all cards
        Box(
            modifier = Modifier
                .size(width = cardWidth + 24.dp, height = cardHeight + 24.dp)
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(8.dp)
                )
        )
    }
}

suspend fun handleAITurn(gameState: GameState, onAITurnComplete: (GameState) -> Unit) {
    logger.debug("GameScreen", "Handling AI turn for game state: $gameState")
    val aiPlayer = gameState.players[gameState.currentPlayerIndex] as? AIPlayer
    if (aiPlayer != null) {
        logger.debug("GameScreen", "AI player found: $aiPlayer")
        val aiGameStates = aiPlayer.makeMove(gameState)
        
        // Iterate through AI moves and update the UI for each move
        for ((index, aiGameState) in aiGameStates.withIndex()) {
            logger.debug("GameScreen", "AI move ${index + 1}: $aiGameState")
            
            // Notify that a card has been played
            onAITurnComplete(aiGameState)
            
            // Add a delay between AI moves
            if (index < aiGameStates.lastIndex) {
                delay(1000) // 1 second delay between moves
            }
        }
        
        logger.debug("GameScreen", "AI turn completed. Final state: ${aiGameStates.last()}")
    } else {
        logger.warn("GameScreen", "No AI player found at index ${gameState.currentPlayerIndex}")
    }
}

fun calculateCardRotation(index: Int): Float {
    logger.debug("GameScreen", "Calculating card rotation for index $index")
    return index * 35f  // Changed parameter name from pileSize to index
}