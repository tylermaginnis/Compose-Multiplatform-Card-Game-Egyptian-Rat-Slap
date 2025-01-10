package org.example.project.gameLogic

import kotlin.random.Random
import kotlinx.coroutines.delay

import org.example.project.ILogger
import org.example.project.createLogger
import org.example.project.ITimeProvider
import org.example.project.createTimeProvider
private val logger: ILogger = createLogger()

private val timeProvider: ITimeProvider = createTimeProvider()

class AIPlayer(name: String) : Player(name) {
    suspend fun makeMove(gameState: GameState): List<GameState> {
        // Simulate initial thinking time
        delay(Random.nextLong(500, 1500))
        
        val startTime = timeProvider.getCurrentTimeMillis()
        val maxTime = 2000L // 2 seconds timeout
        
        var attempts = 0
        val maxAttempts = 5
        
        var currentGameState = gameState
        val gameStates = mutableListOf<GameState>()
        
        while (timeProvider.getCurrentTimeMillis() - startTime < maxTime && attempts < maxAttempts) {
            attempts++
            
            val randomValue = Random.nextInt(100)
            when {
                currentGameState.pile.isEmpty() -> {
                    currentGameState = playCard(currentGameState)
                    gameStates.add(currentGameState)
                    log("Played a card: ${currentGameState.pile.last()}")
                    break
                }
                randomValue in 0..70 -> {
                    currentGameState = playCard(currentGameState)
                    gameStates.add(currentGameState)
                    log("Played a card: ${currentGameState.pile.last()}")
                    // Add delay after playing a card
                    delay(Random.nextLong(50, 150))
                    if (currentGameState.pile.last().rank in listOf(Card.Rank.JACK, Card.Rank.QUEEN, Card.Rank.KING, Card.Rank.ACE)) {
                        log("Special card played, ending turn")
                        return gameStates
                    }
                }
                randomValue in 71..90 -> {
                    if (shouldSlap(currentGameState)) {
                        currentGameState = slap(currentGameState)
                        gameStates.add(currentGameState)
                        log("Slapped the pile")
                        break
                    } else {
                        currentGameState = playCard(currentGameState)
                        gameStates.add(currentGameState)
                        log("Played a card: ${currentGameState.pile.last()}")
                    }
                }
                randomValue in 91..95 -> {
                    currentGameState = playCard(currentGameState)
                    gameStates.add(currentGameState)
                    log("Played a card: ${currentGameState.pile.last()}")
                    // Add delay after playing a card
                    delay(Random.nextLong(50, 150))
                    if (currentGameState.pile.last().rank in listOf(Card.Rank.JACK, Card.Rank.QUEEN, Card.Rank.KING, Card.Rank.ACE)) {
                        log("Special card played, ending turn")
                        return gameStates
                    }
                }
                randomValue in 96..99 -> {
                    if (shouldSlap(currentGameState)) {
                        currentGameState = slap(currentGameState)
                        gameStates.add(currentGameState)
                        log("Slapped the pile")
                        break
                    } else {
                        currentGameState = playCard(currentGameState)
                        gameStates.add(currentGameState)
                        log("Played a card: ${currentGameState.pile.last()}")
                        // Add delay after playing a card
                        delay(Random.nextLong(50, 150))
                        if (currentGameState.pile.last().rank in listOf(Card.Rank.JACK, Card.Rank.QUEEN, Card.Rank.KING, Card.Rank.ACE)) {
                            log("Special card played, ending turn")
                            return gameStates
                        }
                    }
                }
            }
        }
        
        // If no move was made, force a card play
        if (gameStates.isEmpty()) {
            currentGameState = playCard(currentGameState)
            gameStates.add(currentGameState)
            log("Forced to play a card: ${currentGameState.pile.last()}")
        }
        
        log("AI turn completed. Total moves: ${gameStates.size}")
        return gameStates
    }
    
    private suspend fun playCard(gameState: GameState): GameState {
        return GameLogic.playCard(gameState)
    }
    
    private suspend fun slap(gameState: GameState): GameState {
        val (newGameState, _) = GameLogic.slap(gameState, gameState.currentPlayerIndex)
        return newGameState
    }
    
    companion object {
        fun shouldSlap(gameState: GameState): Boolean {
            if (gameState.pile.size < 2) return false
            
            val topCard = gameState.pile.last()
            val secondCard = gameState.pile[gameState.pile.size - 2]
            val thirdCard = if (gameState.pile.size >= 3) gameState.pile[gameState.pile.size - 3] else null
            val fourthCard = if (gameState.pile.size >= 4) gameState.pile[gameState.pile.size - 4] else null
            
            return when {
                // Double
                topCard.rank == secondCard.rank -> true
                // Sandwich
                thirdCard != null && topCard.rank == thirdCard.rank -> true
                // Top Bottom
                fourthCard != null && topCard.rank == fourthCard.rank -> true
                // Tens
                (topCard.rank.value + secondCard.rank.value == 10) ||
                (thirdCard != null && topCard.rank.value + thirdCard.rank.value == 10) -> true
                // Four in a row
                gameState.pile.size >= 4 && isFourInARow(gameState.pile) -> true
                // Marriage
                (topCard.rank == Card.Rank.QUEEN && secondCard.rank == Card.Rank.KING) ||
                (topCard.rank == Card.Rank.KING && secondCard.rank == Card.Rank.QUEEN) -> true
                else -> false
            }
        }
        
        private fun isFourInARow(pile: List<Card>): Boolean {
            if (pile.size < 4) return false
            
            val lastFourCards = pile.takeLast(4)
            val ranks = lastFourCards.map { it.rank.value }.sorted()
            
            return (ranks[0] + 1 == ranks[1] && ranks[1] + 1 == ranks[2] && ranks[2] + 1 == ranks[3]) ||
                   (ranks[0] - 1 == ranks[1] && ranks[1] - 1 == ranks[2] && ranks[2] - 1 == ranks[3])
        }
        
        private fun log(message: String) {
            logger.debug("AIPlayer", message)
        }
    }
}