package org.example.project.gameLogic
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

enum class SlapType {
    DOUBLE,
    SANDWICH,
    TOP_BOTTOM,
    TENS,
    JOKER,
    FOUR_IN_A_ROW,
    MARRIAGE,
    UNKNOWN
}

object GameLogic {
    fun initializeGame(): GameState {
        val deck = createDeck()
        val players = listOf(
            Player("Player 1"),
            AIPlayer("Player 2")
        ).toMutableList()
        
        deck.shuffle()
        
        val player1Deck = deck.subList(0, 26).toMutableList()
        val aiPlayerDeck = deck.subList(26, 52).toMutableList()
        val pile = deck.subList(52, deck.size).toMutableList()
        
        dealCards(player1Deck, players[0])
        dealCards(aiPlayerDeck, players[1])
        
        return GameState(
            deck = mutableListOf(),  // Empty deck, as cards are now in players' hands and pile
            players = players,
            currentPlayerIndex = 0,
            pile = pile
        )
    }

    private fun createDeck(): MutableList<Card> {
        return Card.Suit.values().flatMap { suit ->
            Card.Rank.values().map { rank -> Card(rank, suit) }
        }.toMutableList()
    }

    private fun dealCards(deck: MutableList<Card>, player: Player) {
        while (deck.isNotEmpty()) {
            player.hand.add(deck.removeAt(0))
        }
    }

    suspend fun playCard(gameState: GameState): GameState {
        val currentPlayer = gameState.players[gameState.currentPlayerIndex]
        
        if (currentPlayer.hand.isEmpty()) {
            return gameState.copy(currentPlayerIndex = (gameState.currentPlayerIndex + 1) % gameState.players.size)
        }
        
        val card = currentPlayer.hand.removeAt(0) // Remove the card from the player's hand
        val newPile = gameState.pile.toMutableList()
        newPile.add(card)
        
        val newFaceUp = false
        
        val mutex = Mutex()
        
        var newCurrentPlayerIndex = gameState.currentPlayerIndex
        
        mutex.withLock {
            if (canSlap(gameState)) {
                newCurrentPlayerIndex = gameState.currentPlayerIndex
            } else if (gameState.pile.size == 0) {
                newCurrentPlayerIndex = (gameState.currentPlayerIndex + 1) % gameState.players.size
            } else if (card.rank in listOf(Card.Rank.JACK, Card.Rank.QUEEN, Card.Rank.KING)) {
                newCurrentPlayerIndex = (gameState.currentPlayerIndex + 1) % gameState.players.size
            } else {
                // If the played card is not a KING, QUEEN, or JACK, keep the current player's turn
                newCurrentPlayerIndex = gameState.currentPlayerIndex
            }
        }
        
        // Update the player's hand size
        val newPlayers = gameState.players.toMutableList()
        newPlayers[gameState.currentPlayerIndex] = currentPlayer
        
        return gameState.copy(
            pile = newPile,
            currentPlayerIndex = newCurrentPlayerIndex,
            faceUp = newFaceUp,
            players = newPlayers // Include the updated players list
        )
    }


    suspend fun slap(gameState: GameState, playerIndex: Int): Triple<GameState, String, SlapType?> {
        val currentPlayer = gameState.players[playerIndex]
        
        if (!canSlap(gameState)) {
            // Invalid slap: Remove a card from the player's hand
            val lostCard = if (currentPlayer.hand.isNotEmpty()) {
                currentPlayer.hand.removeAt(0)
            } else {
                null
            }
            
            // Update the player's hand size
            val newPlayers = gameState.players.toMutableList()
            newPlayers[playerIndex] = currentPlayer
            
            // Add the lost card to the bottom of the pile
            val newPile = if (lostCard != null) {
                mutableListOf(lostCard) + gameState.pile
            } else {
                gameState.pile.toMutableList() // Convert to MutableList
            }
            
            return Triple(
                gameState.copy(
                    pile = newPile.toMutableList(),
                    players = newPlayers 
                ),
                "Invalid slap. You lose a card.",
                null
            )
        }
        
        val newPile = mutableListOf<Card>()
        val newHand = currentPlayer.hand.toMutableList()
        
        newHand.addAll(gameState.pile)
        newHand.shuffle()
        
        currentPlayer.hand.clear()
        currentPlayer.hand.addAll(newHand)
        
        // Update the player's hand size
        val newPlayers = gameState.players.toMutableList()
        newPlayers[playerIndex] = currentPlayer
        
        // Determine the type of slap
        val slapType = determineSlapType(gameState)
        
        // Check if the game has ended after the slap
        val endGameState = checkGameEnd(gameState.copy(
            pile = newPile,
            currentPlayerIndex = playerIndex,
            players = newPlayers, // Include the updated players list
            lastSlapType = slapType // Add the slap type to the game state
        ))
        
        return Triple(
            endGameState,
            "Valid slap! Type: $slapType. You win the pile.",
            slapType
        )
    }
    
    // New function to determine the type of slap
    private fun determineSlapType(gameState: GameState): SlapType {
        if (gameState.pile.size < 2) return SlapType.UNKNOWN
        
        val lastCard = gameState.pile.last()
        val secondLastCard = gameState.pile.getOrNull(gameState.pile.size - 2)
        val thirdLastCard = gameState.pile.getOrNull(gameState.pile.size - 3)
        val fourthLastCard = gameState.pile.getOrNull(gameState.pile.size - 4)
        
        // Double
        if (secondLastCard != null && lastCard.rank == secondLastCard.rank) {
            return SlapType.DOUBLE
        }
        
        // Sandwich
        if (thirdLastCard != null && secondLastCard != null && 
            lastCard.rank == thirdLastCard.rank && lastCard.rank != secondLastCard.rank) {
            return SlapType.SANDWICH
        }
        
        // Top Bottom
        if (gameState.pile.size >= 3 && lastCard.rank == gameState.pile[0].rank) {
            return SlapType.TOP_BOTTOM
        }
        
        // Tens
        if (secondLastCard != null) {
            val sum = lastCard.rank.value + secondLastCard.rank.value
            if (sum == 10) {
                return SlapType.TENS
            }
            if (thirdLastCard != null && lastCard.rank == Card.Rank.ACE && 
                secondLastCard.rank == Card.Rank.KING && thirdLastCard.rank.value + 9 == 10) {
                return SlapType.TENS
            }
        }
        
        
        // Four in a row
        if (fourthLastCard != null && thirdLastCard != null && secondLastCard != null) {
            val cards = listOf(fourthLastCard, thirdLastCard, secondLastCard, lastCard)
            val ascending = cards.zipWithNext { a, b -> a.rank.value + 1 == b.rank.value }.all { it }
            val descending = cards.zipWithNext { a, b -> a.rank.value - 1 == b.rank.value }.all { it }
            if (ascending || descending) {
                return SlapType.FOUR_IN_A_ROW
            }
        }
        
        // Marriage
        if (secondLastCard != null && 
            ((lastCard.rank == Card.Rank.QUEEN && secondLastCard.rank == Card.Rank.KING) ||
             (lastCard.rank == Card.Rank.KING && secondLastCard.rank == Card.Rank.QUEEN))) {
            return SlapType.MARRIAGE
        }
        
        return SlapType.UNKNOWN
    }

    fun canSlap(gameState: GameState): Boolean {
        return determineSlapType(gameState) != SlapType.UNKNOWN
    }

    fun checkGameEnd(gameState: GameState): GameState {
        val playersWithCards = gameState.players.filter { it.hand.isNotEmpty() }
        
        if (playersWithCards.size == 1) {
            val winner = playersWithCards[0]
            return gameState.copy(winner = winner.name)
        }
        
        return gameState
    }
}